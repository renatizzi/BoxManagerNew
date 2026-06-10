package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchResponse

class SearchEngineA {

    private val stopWords =
        setOf(
            "a","ad","al","alla","allo","che","con","da","del",
            "della","dello","dei","degli","delle","di","dove",
            "e","gli","ha","hai","ho","i","il","in","l","la",
            "le","li","lo","messo","nel","nella","nello","nei",
            "negli","nelle","per","quale","quali","quanto",
            "quanti","sei","si","sono","su","tra","un","una","uno"
        )

    fun execute(
        analysis: SearchAnalysisResult
    ): SearchResponse {

        val operationalQuery =
            extractOperationalQuery(
                analysis.originalQuery
            )

        return SearchResponse(
            success = true,
            message = "ENGINE_A_RESULT",
            operationalQuery =
                operationalQuery
        )
    }

    private fun extractOperationalQuery(
        query: String
    ): String {

        return query
            .lowercase()
            .replace(
                Regex("[^a-zàèéìòù0-9 ]"),
                " "
            )
            .split("\\s+".toRegex())
            .filter {
                it.isNotBlank()
            }
            .filterNot {
                stopWords.contains(it)
            }
            .joinToString(" ")
    }
}