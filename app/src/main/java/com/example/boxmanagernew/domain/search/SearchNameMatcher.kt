package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.util.CanonicalNormalizer
import java.text.Normalizer

object SearchNameMatcher {

    private val functionWords =
        setOf(
            "a","ad","al","alla","allo","che","con","da","del",
            "della","dello","dei","degli","delle","di",
            "e","è","é","gli","ha","hai","ho","i","il","in","l","la",
            "le","li","lo","nel","nella","nello","nei",
            "negli","nelle","per","quale","quali","quanto",
            "quanti","sei","si","sono","su","tra","un","una","uno",
            "cerca","mostra","dammi","dimmi","trova","dove",
            "fammi","vedere","vedi","mostrami",
            "contengono","contiene","elenco","lista",
            "trovano","trovo","trovato","trovati","trovate",
            "tutto","tutta","tutti","tutte",
            "quello","quella","quelli","quelle",
            "della","delle","dei","dello",
            "parte","parti","fanno","fare","fatto","fatti","fatte",
            "appartiene","appartenenti","appartenere",
            "sono","è","sia","siano",
            "nome","nomi"
        )

    fun isFunctionWord(
        token: String
    ): Boolean {

        return functionWords.contains(
            token.lowercase()
        )
    }

    fun contentTokens(
        question: String
    ): List<String> {

        return CanonicalNormalizer.wordTokens(
            question
        ).filterNot { token ->
            functionWords.contains(token)
        }
    }

    fun tokenAppearsInName(
        name: String,
        token: String
    ): Boolean {

        if (
            name.isBlank() ||
            token.isBlank()
        ) {
            return false
        }

        val nameWords =
            CanonicalNormalizer.wordTokens(
                name
            )

        if (
            nameWords.any { word ->
                nameWordMatches(
                    token,
                    word
                )
            }
        ) {
            return true
        }

        val needle =
            CanonicalNormalizer.canonical(
                token
            )

        return needle.length >= 3 &&
                CanonicalNormalizer.canonical(
                    name
                ).contains(needle)
    }

    /**
     * Parola intera nel nome. Non sottostringa, non Box1, non BOX_vuoto.
     * L'underscore resta nel token; il trattino separa le parole.
     */
    fun wholeWordInName(
        name: String,
        token: String
    ): Boolean {

        if (
            name.isBlank() ||
            token.isBlank()
        ) {
            return false
        }

        return archivalNameWords(
            name
        ).any { word ->

            CanonicalNormalizer.wholeWordMatches(
                token,
                word
            )
        }
    }

    private fun archivalNameWords(
        name: String
    ): List<String> {

        val stripped =
            Normalizer
                .normalize(
                    name.lowercase().trim(),
                    Normalizer.Form.NFD
                )
                .replace(
                    "\\p{InCombiningDiacriticalMarks}+"
                        .toRegex(),
                    ""
                )
                .replace("-", " ")
                .replace("/", " ")
                .replace(
                    "[^a-z0-9_ ]"
                        .toRegex(),
                    " "
                )
                .replace(
                    "\\s+"
                        .toRegex(),
                    " "
                )
                .trim()

        if (stripped.isBlank()) {
            return emptyList()
        }

        return stripped
            .split(" ")
            .filter {
                it.isNotBlank()
            }
    }

    fun matches(
        name: String,
        question: String,
        siblingNames: List<String>
    ): Boolean {

        if (name.isBlank()) {
            return false
        }

        val nameWords =
            CanonicalNormalizer.wordTokens(
                name
            )

        if (nameWords.isEmpty()) {
            return false
        }

        val questionTokens =
            contentTokens(
                question
            )

        val required =
            questionTokens.filter { token ->
                shouldRequire(
                    token,
                    nameWords,
                    name,
                    siblingNames,
                    questionTokens
                )
            }

        if (required.isEmpty()) {
            return false
        }

        if (
            required.all { token ->
                token.length < 3 ||
                        token.all { it.isDigit() }
            }
        ) {
            return false
        }

        return required.all { token ->
            nameWords.any { word ->
                nameWordMatches(
                    token,
                    word
                )
            }
        }
    }

    private fun shouldRequire(
        token: String,
        nameWords: List<String>,
        name: String,
        siblingNames: List<String>,
        questionTokens: List<String>
    ): Boolean {

        val inThisName =
            nameWords.any { word ->
                nameWordMatches(
                    token,
                    word
                )
            }

        if (inThisName) {
            return true
        }

        if (
            isStandaloneOtherName(
                token,
                name,
                siblingNames,
                questionTokens
            )
        ) {
            return false
        }

        if (
            SearchCoreAliases.isObjectAlias(token) ||
                    SearchCoreAliases.isBoxAlias(token) ||
                    SearchCoreAliases.isLocationAlias(token) ||
                    SearchCoreAliases.isCategoryAlias(token)
        ) {
            return false
        }

        return true
    }

    private fun isStandaloneOtherName(
        token: String,
        name: String,
        siblingNames: List<String>,
        questionTokens: List<String>
    ): Boolean {

        return siblingNames.any { other ->

            if (other == name) {
                false
            } else {

                val words =
                    CanonicalNormalizer.wordTokens(
                        other
                    )

                if (words.isEmpty()) {
                    false
                } else {

                    val tokenBelongs =
                        words.any { word ->
                            nameWordMatches(
                                token,
                                word
                            )
                        }

                    val otherEvidenced =
                        words.all { word ->
                            questionTokens.any { questionToken ->
                                nameWordMatches(
                                    questionToken,
                                    word
                                )
                            }
                        }

                    val sharesStem =
                        words.any { siblingWord ->
                            CanonicalNormalizer.wordTokens(
                                name
                            ).any { nameWord ->
                                nameWordMatches(
                                    siblingWord,
                                    nameWord
                                )
                            }
                        }

                    tokenBelongs &&
                            otherEvidenced &&
                            !sharesStem
                }
            }
        }
    }

    private fun nameWordMatches(
        queryToken: String,
        nameWord: String
    ): Boolean {

        if (
            CanonicalNormalizer.wholeWordMatches(
                queryToken,
                nameWord
            )
        ) {
            return true
        }

        val query =
            CanonicalNormalizer.normalize(
                queryToken
            )

        val name =
            CanonicalNormalizer.normalize(
                nameWord
            )

        return query.length >= 3 &&
                name.startsWith(query) &&
                name.substring(query.length)
                    .all { it.isDigit() }
    }
}
