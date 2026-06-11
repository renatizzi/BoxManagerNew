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
                    "LOOKUP hasMatches=false matches=0"
            )
        }

        val recognizedEntitiesResult =
            entityRecognizer.recognize(
                lookupResult
            )

        val fulcrumResult =
            fulcrumResolver.resolve(
                recognizedEntitiesResult
            )

        val matchedPatterns =
            questionRepository.getPatterns()

        val satisfiabilityResult =
            evaluatorV2.evaluate(
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
            )

        if (
            recognizedEntitiesResult
                .recognizedEntities
                .isEmpty()
        ) {

            return SearchResponse(
                success = false,
                message =
                    "Non ho compreso la richiesta."
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
                    SearchClarificationType.GENERIC_REQUEST
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

                engineA.execute(
                    analysis
                )
            }

            SearchClassification.ENGINE_B ->
                SearchResponse(
                    success = false,
                    message =
                        "Motore B non ancora disponibile."
                )
        }
    }
}