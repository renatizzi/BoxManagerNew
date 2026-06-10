package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType
import com.example.boxmanagernew.domain.search.model.SearchClarificationType
import com.example.boxmanagernew.domain.search.model.SearchClassification
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchInterpretation
import com.example.boxmanagernew.domain.search.model.SearchQuestionPattern
import com.example.boxmanagernew.domain.search.model.SearchSatisfiability
import com.example.boxmanagernew.domain.search.model.SearchStrategy

class SearchQuestionRepository {

    fun getPatterns(): List<SearchQuestionPattern> {

        return listOf(

            SearchQuestionPattern(
                id = "PATTERN_001",
                variants = listOf(
                    "cerca",
                    "dove ho messo",
                    "dov'è",
                    "dove si trova",
                    "in quale contenitore trovo",
                    "trova",
                    "dove ho conservato"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT
                ),
                interpretation = SearchInterpretation.FIND_OBJECT,
                dominantFulcrum = SearchFulcrum.OBJECT,
                clarificationType = SearchClarificationType.NONE,
                satisfiability = SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification = SearchClassification.ENGINE_A,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput = "OBJECT_LOCATION"
            ),

            SearchQuestionPattern(
                id = "PATTERN_002",
                variants = listOf(
                    "cosa c'è nel",
                    "cosa contiene",
                    "quali oggetti ci sono",
                    "elenco degli oggetti conservati nel"
                ),
                involvedEntities = setOf(
                    CoreEntityType.BOX,
                    CoreEntityType.OBJECT
                ),
                interpretation = SearchInterpretation.FIND_BOX,
                dominantFulcrum = SearchFulcrum.BOX,
                clarificationType = SearchClarificationType.NONE,
                satisfiability = SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification = SearchClassification.ENGINE_A,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput = "BOX_CONTENTS"
            ),

            SearchQuestionPattern(
                id = "PATTERN_003",
                variants = listOf(
                    "quali contenitori ci sono in",
                    "quali contenitori sono presenti in",
                    "quali sono i contenitori conservati in",
                    "elenco dei contenitori in"
                ),
                involvedEntities = setOf(
                    CoreEntityType.LOCATION,
                    CoreEntityType.BOX
                ),
                interpretation = SearchInterpretation.FIND_BOX,
                dominantFulcrum = SearchFulcrum.BOX,
                clarificationType = SearchClarificationType.NONE,
                satisfiability = SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification = SearchClassification.ENGINE_A,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput = "BOX_LIST"
            ),

            SearchQuestionPattern(
                id = "PATTERN_004",
                variants = listOf(
                    "quali sono i contenitori dove ho conservato",
                    "dove ho messo",
                    "in quali contenitori ho conservato",
                    "elenco dei contenitori dove ho conservato"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT,
                    CoreEntityType.BOX
                ),
                interpretation = SearchInterpretation.FIND_BOX,
                dominantFulcrum = SearchFulcrum.BOX,
                clarificationType = SearchClarificationType.AMBIGUOUS_OBJECT,
                satisfiability = SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification = SearchClassification.ENGINE_A,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput = "BOX_LIST"
            ),

            SearchQuestionPattern(
                id = "PATTERN_005",
                variants = listOf(
                    "conservato dopo",
                    "trova conservato dopo",
                    "in quale contenitore trovo conservato dopo"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT
                ),
                interpretation = SearchInterpretation.FIND_OBJECT,
                dominantFulcrum = SearchFulcrum.OBJECT,
                clarificationType = SearchClarificationType.NONE,
                satisfiability = SearchSatisfiability.REQUIRES_ENGINE_B,
                classification = SearchClassification.ENGINE_B,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = false,
                expectedOutput = "BACKLOG_V2"
            ),

            SearchQuestionPattern(
                id = "PATTERN_006",
                variants = listOf(
                    "dove ho conservato",
                    "in quali luoghi ho conservato",
                    "elenco dei posti dove sono conservati"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT,
                    CoreEntityType.BOX,
                    CoreEntityType.LOCATION
                ),
                interpretation = SearchInterpretation.FIND_OBJECT,
                dominantFulcrum = SearchFulcrum.LOCATION,
                clarificationType = SearchClarificationType.AMBIGUOUS_LOCATION,
                satisfiability = SearchSatisfiability.REQUIRES_ENGINE_B,
                classification = SearchClassification.ENGINE_B,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = false,
                expectedOutput = "LOCATION_LIST"
            ),

            SearchQuestionPattern(
                id = "PATTERN_007",
                variants = listOf(
                    "contenitori che contengono doppioni",
                    "contenitori con oggetti uguali",
                    "dove trovo lo stesso tipo di oggetti"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT,
                    CoreEntityType.BOX
                ),
                interpretation = SearchInterpretation.FIND_OBJECT,
                dominantFulcrum = SearchFulcrum.OBJECT,
                clarificationType = SearchClarificationType.NONE,
                satisfiability = SearchSatisfiability.REQUIRES_ENGINE_B,
                classification = SearchClassification.ENGINE_B,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = false,
                expectedOutput = "DUPLICATES"
            ),

            SearchQuestionPattern(
                id = "PATTERN_008",
                variants = listOf(
                    "contenitori con categoria diversa",
                    "categoria diversa e oggetti uguali",
                    "contenitori con oggetti uguali"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT,
                    CoreEntityType.BOX,
                    CoreEntityType.CATEGORY
                ),
                interpretation = SearchInterpretation.FIND_OBJECT,
                dominantFulcrum = SearchFulcrum.OBJECT,
                clarificationType = SearchClarificationType.NONE,
                satisfiability = SearchSatisfiability.REQUIRES_ENGINE_B,
                classification = SearchClassification.ENGINE_B,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = false,
                expectedOutput = "CATEGORY_COMPARISON"
            ),

            SearchQuestionPattern(
                id = "PATTERN_009",
                variants = listOf(
                    "dove ho conservato",
                    "elenco dei posti dove ho conservato",
                    "trova i posti dove sono"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT,
                    CoreEntityType.BOX,
                    CoreEntityType.LOCATION
                ),
                interpretation = SearchInterpretation.FIND_OBJECT,
                dominantFulcrum = SearchFulcrum.LOCATION,
                clarificationType = SearchClarificationType.AMBIGUOUS_LOCATION,
                satisfiability = SearchSatisfiability.REQUIRES_ENGINE_B,
                classification = SearchClassification.ENGINE_B,
                dominantStrategy = SearchStrategy.DIRECT_MATCH,
                supportsEngineA = false,
                expectedOutput = "LOCATION_LIST"
            )
        )
    }
}