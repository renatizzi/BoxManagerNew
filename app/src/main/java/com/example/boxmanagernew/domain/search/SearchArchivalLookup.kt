package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchArchivalHits
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Individua gli elementi effettivamente presenti in archivio.
 * Un alias Core (es. *box*) è solo un indizio: se lo stesso termine
 * compare nei nomi, prevale l'evidenza d'archivio (Nota R7–R8).
 */
class SearchArchivalLookup {

    @Suppress("UNUSED_PARAMETER")
    fun find(
        question: String,
        index: SearchArchiveIndex,
        indicators: Map<String, Set<String>> =
            emptyMap()
    ): SearchArchivalHits {

        val objectLookup =
            matchingObjects(
                question,
                index
            )

        val hits =
            SearchArchivalHits(
                locations =
                    matchingNames(
                        questionWithoutForeignAliases(
                            question,
                            SearchCoreAliases::isBoxAlias,
                            SearchCoreAliases::isObjectAlias,
                            SearchCoreAliases::isCategoryAlias
                        ),
                        index.locations
                    ),
                categories =
                    matchingNames(
                        questionWithoutForeignAliases(
                            question,
                            SearchCoreAliases::isBoxAlias,
                            SearchCoreAliases::isObjectAlias,
                            SearchCoreAliases::isLocationAlias
                        ),
                        index.categories
                    ),
                objects =
                    objectLookup.keys,
                boxes =
                    matchingBoxNames(
                        question,
                        index.boxes
                    ),
                objectBoxIntersection =
                    objectLookup.boxIntersection,
                hasObjectBoxes =
                    objectLookup.hasObjectBoxes
            )

        return applyCoreSelector(
            question,
            index,
            hits
        )
    }

    private fun questionWithoutForeignAliases(
        question: String,
        vararg foreign: (String) -> Boolean
    ): String {

        return SearchNameMatcher.contentTokens(
            question
        ).filterNot { token ->

            foreign.any { isForeign ->
                isForeign(
                    token
                )
            }
        }.joinToString(" ")
    }

    private fun applyCoreSelector(
        question: String,
        index: SearchArchiveIndex,
        hits: SearchArchivalHits
    ): SearchArchivalHits {

        val tokens =
            SearchNameMatcher.contentTokens(
                question
            )

        val selectorCores =
            preferNonObjectSelector(
                tokens.mapNotNull { token ->

                    if (
                        isPureSelector(
                            token,
                            index
                        )
                    ) {
                        SearchCoreAliases.coreEntityType(
                            token
                        )
                    } else {
                        null
                    }
                }.distinct()
            )

        val nameTokens =
            archivalNameTokens(
                tokens,
                index
            )

        val nameCores =
            nameTokens.flatMap { token ->

                coresNamed(
                    token,
                    index
                )
            }.toSet()

        val selected =
            resolveSelectedCore(
                selectorCores,
                nameCores
            ) ?: return hits

        val homographs =
            tokens.filterNot { token ->

                isPureSelector(
                    token,
                    index
                )
            }.filter { token ->

                coresNamed(
                    token,
                    index
                ).size >= 2
            }

        val keyQuestion =
            when {

                nameTokens.isNotEmpty() ->
                    nameTokens.joinToString(" ")

                homographs.isNotEmpty() ->
                    homographs.joinToString(" ")

                else ->
                    return hits
            }

        val selectedNames =
            when (selected) {

                CoreEntityType.OBJECT ->
                    matchingObjects(
                        keyQuestion,
                        index
                    ).keys

                CoreEntityType.BOX ->
                    matchingBoxNames(
                        keyQuestion,
                        index.boxes
                    )

                CoreEntityType.LOCATION ->
                    matchingNames(
                        keyQuestion,
                        index.locations
                    )

                CoreEntityType.CATEGORY ->
                    matchingNames(
                        keyQuestion,
                        index.categories
                    )
            }

        fun otherCore(
            names: List<String>
        ): List<String> {

            return names.filterNot { name ->

                homographs.any { token ->

                    SearchNameMatcher.wholeWordInName(
                        name,
                        token
                    )
                }
            }
        }

        return hits.copy(
            objects =
                if (
                    selected ==
                    CoreEntityType.OBJECT
                ) {
                    selectedNames.ifEmpty {
                        hits.objects
                    }
                } else {
                    otherCore(
                        hits.objects
                    )
                },
            boxes =
                if (
                    selected ==
                    CoreEntityType.BOX
                ) {
                    selectedNames.ifEmpty {
                        hits.boxes
                    }
                } else {
                    otherCore(
                        hits.boxes
                    )
                },
            locations =
                if (
                    selected ==
                    CoreEntityType.LOCATION
                ) {
                    selectedNames.ifEmpty {
                        hits.locations
                    }
                } else {
                    otherCore(
                        hits.locations
                    )
                },
            categories =
                if (
                    selected ==
                    CoreEntityType.CATEGORY
                ) {
                    selectedNames.ifEmpty {
                        hits.categories
                    }
                } else {
                    otherCore(
                        hits.categories
                    )
                }
        )
    }

    private fun resolveSelectedCore(
        selectorCores: List<CoreEntityType>,
        nameCores: Set<CoreEntityType>
    ): CoreEntityType? {

        selectorCores.singleOrNull()?.let { core ->
            return core
        }

        if (nameCores.size == 1) {
            return nameCores.single()
        }

        val qualifying =
            selectorCores.filter { core ->
                core in nameCores
            }

        return qualifying.singleOrNull()
    }

    private fun coresNamed(
        token: String,
        index: SearchArchiveIndex
    ): Set<CoreEntityType> {

        val cores =
            mutableSetOf<CoreEntityType>()

        if (
            index.archivalObjects().any { record ->

                exactWordInName(
                    record.name,
                    token,
                    inflect = true
                )
            }
        ) {
            cores.add(CoreEntityType.OBJECT)
        }

        if (
            index.boxes.any { name ->

                exactWordInName(
                    name,
                    token,
                    inflect = true
                )
            }
        ) {
            cores.add(CoreEntityType.BOX)
        }

        if (
            index.locations.any { name ->

                !SearchCoreAliases.isLocationAlias(
                    name
                ) &&
                        exactWordInName(
                            name,
                            token,
                            inflect = false
                        )
            }
        ) {
            cores.add(CoreEntityType.LOCATION)
        }

        if (
            index.categories.any { name ->

                !SearchCoreAliases.isCategoryAlias(
                    name
                ) &&
                        exactWordInName(
                            name,
                            token,
                            inflect = false
                        )
            }
        ) {
            cores.add(CoreEntityType.CATEGORY)
        }

        return cores
    }

    fun homonymCoresForClarification(
        question: String,
        index: SearchArchiveIndex,
        hits: SearchArchivalHits? = null
    ): Set<CoreEntityType> {

        val tokens =
            SearchNameMatcher.contentTokens(
                question
            )

        val selectorCores =
            preferNonObjectSelector(
                tokens.mapNotNull { token ->

                    if (
                        isPureSelector(
                            token,
                            index
                        )
                    ) {
                        SearchCoreAliases.coreEntityType(
                            token
                        )
                    } else {
                        null
                    }
                }.distinct()
            )

        val nameTokens =
            archivalNameTokens(
                tokens,
                index
            )

        val nameCores =
            nameTokens.flatMap { token ->

                coresNamed(
                    token,
                    index
                )
            }.toSet()

        if (
            resolveSelectedCore(
                selectorCores,
                nameCores
            ) != null
        ) {
            return emptySet()
        }

        val resolvedHits =
            hits ?: find(
                question,
                index
            )

        val candidateKeys =
            maximalKeys(
                resolvedHits.objects +
                        resolvedHits.boxes +
                        resolvedHits.locations +
                        resolvedHits.categories,
                question
            ).ifEmpty {
                nameTokens
            }

        val cores =
            mutableSetOf<CoreEntityType>()

        for (key in candidateKeys) {

            val identified =
                coresForCompleteKey(
                    key,
                    index
                ).toMutableSet()

            SearchCoreAliases.coreEntityType(
                key
            )?.let { alias ->

                identified.add(
                    alias
                )
            }

            if (identified.size < 2) {
                continue
            }

            val keyWords =
                SearchNameMatcher.contentTokens(
                    key
                )

            val qualifiers =
                preferNonObjectSelector(
                    tokens.filter { token ->

                        keyWords.none { word ->

                            CanonicalNormalizer.wholeWordMatches(
                                token,
                                word
                            )
                        }
                    }.mapNotNull { token ->

                        SearchCoreAliases.coreEntityType(
                            token
                        )
                    }.distinct()
                )

            val qualifying =
                qualifiers.filter { core ->
                    core in identified
                }

            if (qualifying.size == 1) {
                continue
            }

            cores.addAll(
                identified
            )
        }

        return cores
    }

    private fun coresForCompleteKey(
        key: String,
        index: SearchArchiveIndex
    ): Set<CoreEntityType> {

        val cores =
            mutableSetOf<CoreEntityType>()

        fun listed(
            names: List<String>
        ): Boolean {

            return names.any { name ->

                sameCompleteKey(
                    key,
                    name
                )
            }
        }

        if (
            listed(
                index.archivalObjects().map { record ->
                    record.name
                }
            )
        ) {
            cores.add(CoreEntityType.OBJECT)
        }

        if (
            listed(
                index.boxes
            )
        ) {
            cores.add(CoreEntityType.BOX)
        }

        if (
            listed(
                index.locations.filterNot { name ->
                    SearchCoreAliases.isLocationAlias(
                        name
                    )
                }
            )
        ) {
            cores.add(CoreEntityType.LOCATION)
        }

        if (
            listed(
                index.categories.filterNot { name ->
                    SearchCoreAliases.isCategoryAlias(
                        name
                    )
                }
            )
        ) {
            cores.add(CoreEntityType.CATEGORY)
        }

        return cores
    }

    private fun sameCompleteKey(
        left: String,
        right: String
    ): Boolean {

        val leftWords =
            SearchNameMatcher.contentTokens(
                left
            )

        val rightWords =
            SearchNameMatcher.contentTokens(
                right
            )

        if (
            leftWords.isEmpty() ||
            leftWords.size !=
            rightWords.size
        ) {
            return false
        }

        return leftWords.indices.all { wordIndex ->

            CanonicalNormalizer.wholeWordMatches(
                leftWords[wordIndex],
                rightWords[wordIndex]
            )
        }
    }

    private fun archivalNameTokens(
        tokens: List<String>,
        index: SearchArchiveIndex
    ): List<String> {

        return tokens.filterNot { token ->

            isPureSelector(
                token,
                index
            ) ||
                    SearchCoreAliases.coreEntityType(
                        token
                    ) != null ||
                    isCueToken(
                        token
                    )
        }
    }

    private fun isCueToken(
        token: String
    ): Boolean {

        return SearchLexicalIndicatorMatrix
            .isOfficialIndicator(
                token
            ) &&
                SearchCoreAliases.coreEntityType(
                    token
                ) == null
    }

    private fun maximalKeys(
        names: List<String>,
        question: String
    ): List<String> {

        val questionTokens =
            SearchNameMatcher.contentTokens(
                question
            )

        fun coverage(
            name: String
        ): Set<String> {

            return SearchNameMatcher.contentTokens(
                name
            ).map { token ->
                tokenStem(
                    token
                )
            }.filter { stem ->

                questionTokens.any { token ->

                    CanonicalNormalizer.wholeWordMatches(
                        token,
                        stem
                    )
                }
            }.toSet()
        }

        return names.filter { name ->

            val own =
                coverage(
                    name
                )

            own.isNotEmpty() &&
                    names.none { other ->

                        other != name &&
                                coverage(
                                    other
                                ).containsAll(
                                    own
                                ) &&
                                coverage(
                                    other
                                ).size >
                                own.size
                    }
        }
    }

    fun highlightKeys(
        question: String,
        index: SearchArchiveIndex
    ): String {

        return SearchNameMatcher.contentTokens(
            question
        ).filterNot { token ->

            isPureSelector(
                token,
                index
            )
        }.joinToString(" ")
    }

    fun needsHomonymClarification(
        question: String,
        index: SearchArchiveIndex
    ): Boolean {

        return homonymCoresForClarification(
            question,
            index
        ).size >= 2
    }

    private fun identifiedCores(
        token: String,
        index: SearchArchiveIndex
    ): Set<CoreEntityType> {

        val cores =
            coresNamed(
                token,
                index
            ).toMutableSet()

        SearchCoreAliases.coreEntityType(
            token
        )?.let { alias ->

            cores.add(
                alias
            )
        }

        return cores
    }

    private fun extraQualifierCores(
        tokens: List<String>,
        homonym: String
    ): Set<CoreEntityType> {

        return tokens
            .filter { token ->
                token != homonym
            }.mapNotNull { token ->

                SearchCoreAliases.coreEntityType(
                    token
                )
            }.toSet()
    }

    private fun isPureSelector(
        token: String,
        index: SearchArchiveIndex
    ): Boolean {

        val core =
            SearchCoreAliases.coreEntityType(
                token
            ) ?: return false

        if (
            identifiedCores(
                token,
                index
            ).size >= 2
        ) {
            return false
        }

        return !hasSameCoreName(
            token,
            core,
            index
        )
    }

    private fun hasSameCoreName(
        token: String,
        core: CoreEntityType,
        index: SearchArchiveIndex
    ): Boolean {

        val names =
            when (core) {

                CoreEntityType.OBJECT ->
                    index.archivalObjects().map { record ->
                        record.name
                    }

                CoreEntityType.BOX ->
                    index.boxes

                CoreEntityType.LOCATION ->
                    index.locations

                CoreEntityType.CATEGORY ->
                    index.categories
            }

        return names.any { name ->

            isSingleName(
                name,
                token
            )
        }
    }

    private fun preferNonObjectSelector(
        cores: List<CoreEntityType>
    ): List<CoreEntityType> {

        if (
            cores.size > 1 &&
            cores.contains(
                CoreEntityType.OBJECT
            ) &&
            cores.any { core ->
                core != CoreEntityType.OBJECT
            }
        ) {

            return cores.filter { core ->
                core != CoreEntityType.OBJECT
            }
        }

        return cores
    }

    private fun exactWordInName(
        name: String,
        token: String,
        inflect: Boolean
    ): Boolean {

        val words =
            SearchNameMatcher.contentTokens(
                name
            )

        if (words.size != 1) {
            return false
        }

        return if (inflect) {

            CanonicalNormalizer.wholeWordMatches(
                token,
                words.first()
            )
        } else {

            CanonicalNormalizer.normalize(
                token
            ) ==
                    CanonicalNormalizer.normalize(
                        words.first()
                    )
        }
    }

    private fun isSingleName(
        name: String,
        token: String
    ): Boolean {

        val words =
            SearchNameMatcher.contentTokens(
                name
            )

        return words.size == 1 &&
                CanonicalNormalizer.normalize(
                    token
                ) ==
                CanonicalNormalizer.normalize(
                    words.first()
                )
    }

    private fun matchingObjects(
        question: String,
        index: SearchArchiveIndex
    ): ObjectLookup {

        val questionTokens =
            SearchNameMatcher.contentTokens(
                question
            )

        val matches =
            mutableListOf<IntegralMatch>()

        for (record in index.archivalObjects()) {

            val nameTokens =
                archivalTokens(
                    record.name,
                    index,
                    stripForeignNames = false
                )

            val descTokens =
                archivalTokens(
                    record.description,
                    index,
                    stripForeignNames = true
                )

            val nameHits =
                nameTokens.filter { token ->
                    inQuestion(
                        token,
                        questionTokens
                    )
                }

            val descHits =
                descTokens.filter { token ->
                    inQuestion(
                        token,
                        questionTokens
                    )
                }

            val nameComplete =
                nameTokens.isNotEmpty() &&
                        nameHits.size ==
                        nameTokens.size

            val descComplete =
                descTokens.isNotEmpty() &&
                        descHits.size ==
                        descTokens.size

            val nameCoverage =
                stems(
                    nameTokens
                )

            val descCoverage =
                stems(
                    descTokens
                )

            val nameIsIntegralKey =
                nameComplete &&
                        covers(
                            nameCoverage,
                            descCoverage
                        ) &&
                        nameCoverage.size >=
                        descCoverage.size

            when {

                descComplete &&
                        !nameIsIntegralKey -> {

                    matches.add(
                        IntegralMatch(
                            key =
                                phraseOf(
                                    questionTokens,
                                    descTokens
                                ),
                            coverage =
                                descCoverage,
                            complete = true
                        )
                    )
                }

                nameComplete -> {

                    matches.add(
                        IntegralMatch(
                            key = record.name,
                            coverage =
                                nameCoverage,
                            complete = true
                        )
                    )
                }

                questionCoveredBy(
                    nameTokens,
                    questionTokens
                ) -> {

                    matches.add(
                        IntegralMatch(
                            key = record.name,
                            coverage =
                                stems(
                                    nameHits
                                ),
                            complete = false
                        )
                    )
                }

                questionCoveredBy(
                    descTokens,
                    questionTokens
                ) -> {

                    matches.add(
                        IntegralMatch(
                            key =
                                phraseOf(
                                    questionTokens,
                                    descHits
                                ),
                            coverage =
                                stems(
                                    descHits
                                ),
                            complete = false
                        )
                    )
                }
            }
        }

        val notSubset =
            matches.filter { match ->

                matches.none { other ->

                    other.coverage.size >
                            match.coverage.size &&
                            covers(
                                other.coverage,
                                match.coverage
                            )
                }
            }

        val keys =
            notSubset.filter { match ->

                match.complete ||
                        notSubset.none { other ->

                            other.complete &&
                                    sameCoverage(
                                        other.coverage,
                                        match.coverage
                                    )
                        }
            }.map { match ->
                match.key
            }.filter { key ->
                key.isNotBlank()
            }.distinct()
                .sortedByDescending {
                    it.length
                }

        return ObjectLookup(
            keys = keys,
            boxIntersection = emptyList(),
            hasObjectBoxes = false
        )
    }

    private fun archivalTokens(
        text: String,
        index: SearchArchiveIndex,
        stripForeignNames: Boolean
    ): List<String> {

        return SearchNameMatcher.contentTokens(
            text
        ).filterNot { token ->

            SearchCoreAliases.isLocationAlias(
                token
            ) ||
                    SearchCoreAliases.isCategoryAlias(
                        token
                    ) ||
                    (
                            stripForeignNames &&
                                    isLocationOrCategoryName(
                                        token,
                                        index
                                    )
                            )
        }
    }

    private fun isLocationOrCategoryName(
        token: String,
        index: SearchArchiveIndex
    ): Boolean {

        return listOf(
            index.locations,
            index.categories
        ).any { names ->

            names.any { name ->

                CanonicalNormalizer.wordTokens(
                    name
                ).size == 1 &&
                        SearchNameMatcher.wholeWordInName(
                            name,
                            token
                        )
            }
        }
    }

    private fun questionCoveredBy(
        fieldTokens: List<String>,
        questionTokens: List<String>
    ): Boolean {

        if (
            fieldTokens.isEmpty() ||
            questionTokens.isEmpty()
        ) {
            return false
        }

        return questionTokens.all { token ->

            fieldTokens.any { word ->

                CanonicalNormalizer.wholeWordMatches(
                    token,
                    word
                )
            }
        }
    }

    private fun inQuestion(
        archivalToken: String,
        questionTokens: List<String>
    ): Boolean {

        return questionTokens.any { token ->

            CanonicalNormalizer.wholeWordMatches(
                token,
                archivalToken
            )
        }
    }

    private fun phraseOf(
        questionTokens: List<String>,
        archivalTokens: List<String>
    ): String {

        return questionTokens.filter { token ->

            archivalTokens.any { word ->

                CanonicalNormalizer.wholeWordMatches(
                    token,
                    word
                )
            }
        }.joinToString(" ")
    }

    private fun stems(
        tokens: List<String>
    ): Set<String> {

        return tokens.map { token ->
            tokenStem(
                token
            )
        }.toSet()
    }

    private fun covers(
        larger: Set<String>,
        smaller: Set<String>
    ): Boolean {

        if (smaller.isEmpty()) {
            return true
        }

        return smaller.all { small ->

            larger.any { big ->

                CanonicalNormalizer.wholeWordMatches(
                    small,
                    big
                )
            }
        }
    }

    private fun sameCoverage(
        left: Set<String>,
        right: Set<String>
    ): Boolean {

        return covers(
            left,
            right
        ) &&
                covers(
                    right,
                    left
                )
    }

    private fun tokenStem(
        token: String
    ): String {

        val normalized =
            CanonicalNormalizer.normalize(
                token
            )

        val variants =
            listOf(
                normalized,
                CanonicalNormalizer.singularPluralVariant(
                    normalized
                ),
                CanonicalNormalizer.irregularVariant(
                    normalized
                )
            )

        return variants.sorted().first()
    }

    private data class IntegralMatch(

        val key: String,

        val coverage: Set<String>,

        val complete: Boolean
    )

    private data class ObjectLookup(

        val keys: List<String>,

        val boxIntersection: List<String>,

        val hasObjectBoxes: Boolean
    )

    private fun matchingBoxNames(
        question: String,
        names: List<String>
    ): List<String> {

        val tokens =
            SearchNameMatcher.contentTokens(
                question
            )

        val specific =
            tokens.filterNot { token ->
                SearchCoreAliases.isBoxAlias(token) ||
                        SearchCoreAliases.isLocationAlias(token) ||
                        SearchCoreAliases.isCategoryAlias(token) ||
                        SearchCoreAliases.isObjectAlias(token) ||
                        isCueToken(
                            token
                        )
            }

        val hasNameToken =
            specific.any { token ->
                !token.all { char ->
                    char.isDigit()
                }
            }

        if (hasNameToken) {

            return matchingNames(
                specific.joinToString(" "),
                names
            )
        }

        val boxAliases =
            tokens.filter { token ->
                SearchCoreAliases.isBoxAlias(
                    token
                )
            }

        if (boxAliases.isEmpty()) {

            return matchingNames(
                question,
                names
            )
        }

        return names
            .filter { name ->

                name.isNotBlank() &&
                        boxAliases.any { alias ->
                            SearchNameMatcher.wholeWordInName(
                                name,
                                alias
                            )
                        }
            }
            .sortedByDescending {
                it.length
            }
    }

    fun matchingNames(
        question: String,
        names: List<String>
    ): List<String> {

        return names
            .filter { name ->

                name.isNotBlank() &&
                        SearchNameMatcher.matches(
                            name,
                            question,
                            names
                        )
            }
            .sortedByDescending {
                it.length
            }
    }
}
