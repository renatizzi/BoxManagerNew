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
                id = "find_object",
                variants = listOf(
                    "cerca oggetto",
                    "dove ho messo",
                    "dov è",
                    "dove si trova",
                    "trova"
                ),
                involvedEntities = setOf(
                    CoreEntityType.OBJECT
                ),
                interpretation =
                    SearchInterpretation.FIND_OBJECT,
                dominantFulcrum =
                    SearchFulcrum.OBJECT,
                clarificationType =
                    SearchClarificationType.NONE,
                satisfiability =
                    SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification =
                    SearchClassification.ENGINE_A,
                dominantStrategy =
                    SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput =
                    "OBJECT_LOCATION"
            ),

            SearchQuestionPattern(
                id = "box_contents",
                variants = listOf(
                    "cosa c è nel contenitore",
                    "cosa contiene",
                    "quali oggetti ci sono"
                ),
                involvedEntities = setOf(
                    CoreEntityType.BOX,
                    CoreEntityType.OBJECT
                ),
                interpretation =
                    SearchInterpretation.FIND_BOX,
                dominantFulcrum =
                    SearchFulcrum.BOX,
                clarificationType =
                    SearchClarificationType.NONE,
                satisfiability =
                    SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification =
                    SearchClassification.ENGINE_A,
                dominantStrategy =
                    SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput =
                    "BOX_CONTENTS"
            ),

            SearchQuestionPattern(
                id = "boxes_in_location",
                variants = listOf(
                    "quali contenitori ci sono",
                    "contenitori presenti",
                    "contenitori conservati"
                ),
                involvedEntities = setOf(
                    CoreEntityType.BOX,
                    CoreEntityType.LOCATION
                ),
                interpretation =
                    SearchInterpretation.FIND_BOX,
                dominantFulcrum =
                    SearchFulcrum.BOX,
                clarificationType =
                    SearchClarificationType.NONE,
                satisfiability =
                    SearchSatisfiability.SATISFIABLE_BY_ENGINE_A,
                classification =
                    SearchClassification.ENGINE_A,
                dominantStrategy =
                    SearchStrategy.DIRECT_MATCH,
                supportsEngineA = true,
                expectedOutput =
                    "BOX_LIST"
            )
        )
    }
}