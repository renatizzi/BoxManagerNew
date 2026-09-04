package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchArchivalHits
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityResult
import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Unico avvio vivo: Pipeline 3.3.6 fasi 1–10, senza piano-nomi prima.
 */
class GlobalSearchDispatcher(

    private val normalizer: SearchNormalizer =
        SearchNormalizer(),

    private val tokenizer: SearchTokenizer =
        SearchTokenizer(),

    private val lexicalIndicatorMatrix:
    SearchLexicalIndicatorMatrix =
        SearchLexicalIndicatorMatrix(),

    private val archiveLookup: SearchArchiveLookup =
        SearchArchiveLookup(),

    private val entityRecognizer: SearchEntityRecognizer =
        SearchEntityRecognizer(),

    private val archiveFulcrumResolver:
    SearchArchiveFulcrumResolver =
        SearchArchiveFulcrumResolver(),

    private val pathBuilder: SearchArchivePathBuilder =
        SearchArchivePathBuilder(),

    private val transformationResolver:
    SearchArchiveTransformationResolver =
        SearchArchiveTransformationResolver(),

    private val satisfiabilityEvaluator:
    SearchSatisfiabilityEvaluator =
        SearchSatisfiabilityEvaluator(),

    private val engineB: SearchEngineB =
        SearchEngineB()
) {

    fun dispatch(
        question: String,
        archiveIndex: SearchArchiveIndex? = null,
        locale: SearchLocale = SearchLocale.IT
    ): SearchResponse {

        return SearchLocaleContext.run(locale) {
            dispatchPipeline(
                question,
                archiveIndex
            )
        }
    }

    private fun dispatchPipeline(
        question: String,
        archiveIndex: SearchArchiveIndex?
    ): SearchResponse {

        val normalizedQuestion =
            normalizer.normalize(
                question
            )

        tokenizer.tokenize(
            normalizedQuestion
        )

        val lexicalIndicatorGroups =
            lexicalIndicatorMatrix
                .findIndicators(
                    normalizedQuestion
                        .normalizedQuestion
                )

        val lookupResult =
            archiveLookup.lookup(
                searchText =
                    normalizedQuestion
                        .normalizedQuestion,
                index = archiveIndex,
                indicators =
                    lexicalIndicatorGroups
            )

        val recognizedEntitiesResult =
            entityRecognizer.recognize(
                lookupResult,
                lexicalIndicatorGroups
            )

        val fulcrumResult =
            archiveFulcrumResolver.resolve(
                question =
                    normalizedQuestion
                        .normalizedQuestion,
                hits = lookupResult.hits
            )

        val archivePath =
            pathBuilder.build(
                fulcrumResult.fulcrum,
                recognizedEntitiesResult.recognizedEntities
            )

        val archiveTransformation =
            transformationResolver.resolve(
                archivePath
            )

        val extras =
            extrasFor(
                archiveTransformation,
                lookupResult.hits
            )

        val navigationSatisfiable =
            hasNavigationExtras(
                extras
            )

        val satisfiability =
            satisfiabilityEvaluator.evaluate(
                normalizedQuestion.normalizedQuestion,
                lexicalIndicatorGroups,
                archiveTransformation,
                navigationSatisfiable
            )

        val requestType =
            requestTypeOf(
                satisfiability
            )

        val debugMarker =
            listOf(
                "[M1] QUESTION=${normalizedQuestion.normalizedQuestion}",
                "[M2] INDICATORS=$lexicalIndicatorGroups",
                "[M3] ENTITIES=${recognizedEntitiesResult.recognizedEntities}",
                "[M5] FULCRUM=${fulcrumResult.fulcrum}",
                "[D3] FULCRUM_REASON=${fulcrumResult.reason}",
                "[PATH] ${archivePath.steps}",
                "[TRANSFORM] $archiveTransformation",
                "[SATISFIABLE] ${satisfiability.satisfiableByEngineA}",
                "[TYPE] $requestType",
                "[PATTERN] ${satisfiability.matchedPatternId}"
            ).joinToString("\n")

        val homonymCores =
            if (archiveIndex != null) {
                archiveLookup.homonymCoresForClarification(
                    normalizedQuestion.normalizedQuestion,
                    archiveIndex,
                    lookupResult.hits
                )
            } else {
                emptySet()
            }

        if (
            homonymCores.size >= 2
        ) {

            return SearchResponse(
                success = false,
                message =
                    SearchConfiguration.homonymClarifyMessage(
                        homonymCores
                    ),
                requiresClarification = true,
                clarificationType =
                    SearchClarificationType.AMBIGUOUS_CORE,
                dominantFulcrum =
                    fulcrumResult.fulcrum,
                archiveTransformation =
                    archiveTransformation,
                requestType = requestType,
                debugMarker = debugMarker
            )
        }

        if (
            requestType ==
            SearchRequestType.ARCHIVE_NAVIGATION &&
            satisfiability.satisfiableByEngineA
        ) {

            return SearchResponse(
                success = true,
                message = "ENGINE_A_RESULT",
                operationalQuery =
                    extras.objectTerms
                        .ifBlank { null },
                dominantFulcrum =
                    outputFulcrum(
                        extras
                    ),
                locationTerms =
                    extras.locationTerms,
                categoryTerms =
                    extras.categoryTerms,
                boxTerms =
                    extras.boxTerms,
                objectTerms =
                    extras.objectTerms,
                highlightTerms =
                    if (archiveIndex != null) {
                        archiveLookup.highlightKeys(
                            normalizedQuestion.normalizedQuestion,
                            archiveIndex
                        )
                    } else {
                        ""
                    },
                archiveTransformation =
                    archiveTransformation,
                requestType = requestType,
                debugMarker = debugMarker
            )
        }

        if (
            requestType ==
            SearchRequestType.ARCHIVE_QUERY
        ) {

            if (
                archiveIndex != null &&
                satisfiability.matchedPatternId ==
                SearchF7Pattern.ID
            ) {

                val engineResponse =
                    engineB.execute(
                        SearchEngineB.f7Query(),
                        archiveIndex
                    )

                return engineResponse.copy(
                    dominantFulcrum =
                        fulcrumResult.fulcrum,
                    archiveTransformation =
                        archiveTransformation,
                    requestType =
                        SearchRequestType.ARCHIVE_QUERY,
                    matchedPatternId =
                        SearchF7Pattern.ID,
                    debugMarker = debugMarker
                )
            }

            if (
                archiveIndex != null &&
                archiveTransformation ==
                SearchArchiveTransformation.OBJECT_TO_LOCATION
            ) {

                val engineResponse =
                    engineB.execute(
                        SearchEngineB.objectLocationQuery(),
                        archiveIndex
                    )

                return engineResponse.copy(
                    dominantFulcrum =
                        fulcrumResult.fulcrum,
                    archiveTransformation =
                        archiveTransformation,
                    requestType =
                        SearchRequestType.ARCHIVE_QUERY,
                    debugMarker = debugMarker
                )
            }

            if (
                archiveIndex != null &&
                archiveTransformation ==
                SearchArchiveTransformation.OBJECT_TO_CATEGORY
            ) {

                val engineResponse =
                    engineB.execute(
                        SearchEngineB.f8Query(),
                        archiveIndex
                    )

                return engineResponse.copy(
                    dominantFulcrum =
                        fulcrumResult.fulcrum,
                    archiveTransformation =
                        archiveTransformation,
                    requestType =
                        SearchRequestType.ARCHIVE_QUERY,
                    debugMarker = debugMarker
                )
            }

            if (
                archiveIndex != null &&
                archiveTransformation ==
                SearchArchiveTransformation.BOX_TO_LOCATION
            ) {

                val engineResponse =
                    engineB.execute(
                        SearchEngineB.boxLocationQuery(),
                        archiveIndex
                    )

                return engineResponse.copy(
                    dominantFulcrum =
                        fulcrumResult.fulcrum,
                    archiveTransformation =
                        archiveTransformation,
                    requestType =
                        SearchRequestType.ARCHIVE_QUERY,
                    debugMarker = debugMarker
                )
            }

            if (
                archiveIndex != null &&
                archiveTransformation ==
                SearchArchiveTransformation.BOX_TO_CATEGORY
            ) {

                val engineResponse =
                    engineB.execute(
                        SearchEngineB.boxCategoryQuery(),
                        archiveIndex
                    )

                return engineResponse.copy(
                    dominantFulcrum =
                        fulcrumResult.fulcrum,
                    archiveTransformation =
                        archiveTransformation,
                    requestType =
                        SearchRequestType.ARCHIVE_QUERY,
                    debugMarker = debugMarker
                )
            }

            return SearchResponse(
                success = false,
                message =
                    SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE,
                dominantFulcrum =
                    fulcrumResult.fulcrum,
                archiveTransformation =
                    archiveTransformation,
                requestType = requestType,
                debugMarker = debugMarker
            )
        }

        val message =
            if (
                lookupResult.hasMatches
            ) {
                SearchConfiguration.MSG_NOT_UNDERSTOOD
            } else {
                SearchConfiguration.MSG_NO_RESULTS
            }

        return SearchResponse(
            success = false,
            message = message,
            dominantFulcrum =
                fulcrumResult.fulcrum,
            archiveTransformation =
                archiveTransformation,
            requestType = requestType,
            debugMarker = debugMarker
        )
    }

    private fun requestTypeOf(
        satisfiability: SearchSatisfiabilityResult
    ): SearchRequestType? {

        if (
            satisfiability.satisfiableByEngineA
        ) {

            return SearchRequestType.ARCHIVE_NAVIGATION
        }

        if (
            satisfiability.satisfiableByEngineB
        ) {

            return SearchRequestType.ARCHIVE_QUERY
        }

        return null
    }

    private fun extrasFor(
        transformation: SearchArchiveTransformation,
        hits: SearchArchivalHits
    ): NavigationExtras {

        val categoryTerms =
            SearchConfiguration.packLocationTerms(
                hits.categories.filterNot { name ->
                    SearchCoreAliases.isCategoryAlias(
                        name
                    )
                }
            )

        val locationTerms =
            SearchConfiguration.packLocationTerms(
                hits.locations.filterNot { name ->
                    SearchCoreAliases.isLocationAlias(
                        name
                    )
                }
            )

        val boxTerms =
            SearchConfiguration.packLocationTerms(
                hits.boxes.filterNot { name ->
                    hits.locations.any { location ->
                        CanonicalNormalizer.allTokensMatchWords(
                            name,
                            location
                        ) &&
                                CanonicalNormalizer.allTokensMatchWords(
                                    location,
                                    name
                                )
                    }
                }
            )

        val objectTerms =
            SearchConfiguration.packLocationTerms(
                hits.objects.filterNot { name ->
                    SearchCoreAliases.isLocationAlias(name) ||
                            SearchCoreAliases.isCategoryAlias(name)
                }
            )

        return when (transformation) {

            SearchArchiveTransformation.CATEGORY_TO_BOX ->
                NavigationExtras(
                    categoryTerms = categoryTerms,
                    locationTerms = locationTerms
                )

            SearchArchiveTransformation.LOCATION_TO_BOX ->
                NavigationExtras(
                    locationTerms = locationTerms
                )

            SearchArchiveTransformation.OBJECT_TO_BOX ->
                NavigationExtras(
                    objectTerms = objectTerms
                )

            SearchArchiveTransformation.NONE ->
                NavigationExtras(
                    boxTerms = boxTerms
                )

            else ->
                NavigationExtras()
        }
    }

    private fun hasNavigationExtras(
        extras: NavigationExtras
    ): Boolean {

        return extras.boxTerms.isNotBlank() ||
                extras.locationTerms.isNotBlank() ||
                extras.categoryTerms.isNotBlank() ||
                extras.objectTerms.isNotBlank()
    }

    private fun outputFulcrum(
        extras: NavigationExtras
    ) =
        when {

            extras.objectTerms.isNotBlank() ->
                SearchFulcrum.OBJECT

            extras.boxTerms.isNotBlank() ->
                SearchFulcrum.BOX

            extras.categoryTerms.isNotBlank() ->
                SearchFulcrum.BOX

            extras.locationTerms.isNotBlank() ->
                SearchFulcrum.BOX

            else ->
                SearchFulcrum.BOX
        }

    private data class NavigationExtras(

        val locationTerms: String = "",

        val categoryTerms: String = "",

        val boxTerms: String = "",

        val objectTerms: String = ""
    )
}
