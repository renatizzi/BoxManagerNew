package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.domain.search.model.SearchSatisfiability
import com.example.boxmanagernew.domain.search.model.SearchSatisfiabilityInput

class GlobalSearchDispatcher(

    private val normalizer: SearchNormalizer =
        SearchNormalizer(),

    private val tokenizer: SearchTokenizer =
        SearchTokenizer(),

    private val corePipeline: SearchCoreNormalizationPipeline =
        SearchCoreNormalizationPipeline(),

    private val lexicalIndicatorMatrix:
    SearchLexicalIndicatorMatrix =
        SearchLexicalIndicatorMatrix(),

    private val interpreter: SearchInterpreter =
        SearchInterpreter(),

    private val archiveLookup: SearchArchiveLookup =
        SearchArchiveLookup(),

    private val entityRecognizer: SearchEntityRecognizer =
        SearchEntityRecognizer(),

    private val fulcrumResolver: SearchFulcrumResolver =
        SearchFulcrumResolver(),

    private val questionRepository: SearchQuestionRepository =
        SearchQuestionRepository(),

    private val evaluatorV2:
    SearchSatisfiabilityEvaluatorV2 =
        SearchSatisfiabilityEvaluatorV2(),

    private val router: SearchRouter =
        SearchRouter(),

    private val engineA: SearchEngineA =
        SearchEngineA()
) {

    fun dispatch(
        question: String
    ): SearchResponse {

        val normalizedQuestion =
            normalizer.normalize(
                question
            )

        val tokenizedQuestion =
            tokenizer.tokenize(
                normalizedQuestion
            )

        val coreNormalizationResult =
            corePipeline.normalize(
                tokenizedQuestion
            )

        val lexicalIndicatorGroups =
            lexicalIndicatorMatrix
                .findIndicators(
                    coreNormalizationResult
                        .normalizedQuestion
                )

        val interpretation =
            interpreter.interpret(
                coreNormalizationResult
            )

        val d1Marker =
            "[D1] INTERPRETATION=$interpretation"

        val d2Marker =
            "[D2] INTERPRETATION_REASON=TO_BE_IMPLEMENTED"

        val lookupResult =
            archiveLookup.lookup(
                searchText =
                    coreNormalizationResult
                        .normalizedQuestion
            )

        if (
            !lookupResult.hasMatches
        ) {

            return SearchResponse(
                success = false,
                message =
                    "LOOKUP hasMatches=false matches=0",
                debugMarker =
                    listOf(
                        "[M1] QUESTION=${coreNormalizationResult.normalizedQuestion}",
                        "[M2] INDICATORS=$lexicalIndicatorGroups",
                        d1Marker,
                        d2Marker
                    ).joinToString("\n")
            )
        }

        val recognizedEntitiesResult =
            entityRecognizer.recognize(
                lookupResult
            )

        val m3Marker =
            "[M3] ENTITIES=${
                recognizedEntitiesResult
                    .recognizedEntities
            }"

        val fulcrumResult =
            fulcrumResolver.resolve(
                recognizedEntitiesResult
            )

        val m5Marker =
            "[M5] FULCRUM=${fulcrumResult.fulcrum}"

        val d3Marker =
            "[D3] FULCRUM_REASON=${fulcrumResult.reason}"

        val matchedPatterns =
            questionRepository.getPatterns()

        val satisfiabilityInput =
            SearchSatisfiabilityInput(
                originalQuestion =
                    question,
                interpretation =
                    interpretation,
                fulcrumResult =
                    fulcrumResult,
                recognizedEntitiesResult =
                    recognizedEntitiesResult,
                matchedPatterns =
                    matchedPatterns,
                lexicalIndicators =
                    emptyList(),
                lexicalIndicatorGroups =
                    lexicalIndicatorGroups
            )

        val satisfiabilityResult =
            evaluatorV2.evaluate(
                satisfiabilityInput
            )

        val m4Marker =
            "[M4] PATTERN=${satisfiabilityResult.matchedPatternId}"

        val m6Marker =
            "[M6] SATISFIABILITY=${
                satisfiabilityResult.satisfiableByEngineA
            }"

        val m7Marker =
            "[M7] CLASSIFICATION=${
                satisfiabilityResult.finalClassification
            }"

        if (
            recognizedEntitiesResult
                .recognizedEntities
                .isEmpty()
        ) {

            return SearchResponse(
                success = false,
                message =
                    "Non ho compreso la richiesta.",
                debugMarker =
                    listOf(
                        "[M1] QUESTION=${coreNormalizationResult.normalizedQuestion}",
                        "[M2] INDICATORS=$lexicalIndicatorGroups",
                        m3Marker,
                        m4Marker,
                        m5Marker,
                        m6Marker,
                        m7Marker,
                        d1Marker,
                        d2Marker,
                        d3Marker
                    ).joinToString("\n")
            )
        }

        if (
            satisfiabilityResult
                .requiresClarification
        ) {

            return SearchResponse(
                success = false,
                message =
                    "Puoi formulare la richiesta in modo più preciso?",
                requiresClarification = true,
                clarificationType =
                    SearchClarificationType.GENERIC_REQUEST,
                debugMarker =
                    listOf(
                        "[M1] QUESTION=${coreNormalizationResult.normalizedQuestion}",
                        "[M2] INDICATORS=$lexicalIndicatorGroups",
                        m3Marker,
                        m4Marker,
                        m5Marker,
                        m6Marker,
                        m7Marker,
                        d1Marker,
                        d2Marker,
                        d3Marker
                    ).joinToString("\n")
            )
        }

        return when (
            satisfiabilityResult
                .finalClassification
        ) {

            SearchClassification.ENGINE_A -> {

                val analysis =
                    SearchAnalysisResult(
                        originalQuery =
                            question,
                        operationalQuery =
                            null,
                        interpretation =
                            interpretation,
                        recognizedEntities =
                            recognizedEntitiesResult
                                .recognizedEntities
                                .map {
                                    it.entityType
                                }
                                .toSet(),
                        dominantFulcrum =
                            fulcrumResult.fulcrum,
                        satisfiability =
                            SearchSatisfiability
                                .SATISFIABLE_BY_ENGINE_A,
                        classification =
                            SearchClassification
                                .ENGINE_A,
                        patternId =
                            satisfiabilityResult
                                .matchedPatternId
                    )

                val routingResult =
                    router.route(
                        analysis,
                        satisfiabilityResult
                    )

                val response =
                    engineA.execute(
                        analysis
                    )

                response.copy(
                    debugMarker =
                        listOf(
                            "[M1] QUESTION=${coreNormalizationResult.normalizedQuestion}",
                            "[M2] INDICATORS=$lexicalIndicatorGroups",
                            m3Marker,
                            m4Marker,
                            m5Marker,
                            m6Marker,
                            m7Marker,
                            routingResult.debugMarker ?: "",
                            d1Marker,
                            d2Marker,
                            d3Marker
                        ).joinToString("\n")
                )
            }

            SearchClassification.ENGINE_B ->
                SearchResponse(
                    success = false,
                    message =
                        "Motore B non ancora disponibile.",
                    debugMarker =
                        listOf(
                            "[M1] QUESTION=${coreNormalizationResult.normalizedQuestion}",
                            "[M2] INDICATORS=$lexicalIndicatorGroups",
                            m3Marker,
                            m4Marker,
                            m5Marker,
                            m6Marker,
                            m7Marker,
                            d1Marker,
                            d2Marker,
                            d3Marker
                        ).joinToString("\n")
                )
        }
    }
}