package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchResponse

class SearchEngineA(

    private val lexicalMatrix:
    SearchLexicalIndicatorMatrix =
        SearchLexicalIndicatorMatrix()
) {

    private val stopWords =
        setOf(
            "a","ad","al","alla","allo","che","con","da","del",
            "della","dello","dei","degli","delle","di",
            "e","gli","ha","hai","ho","i","il","in","l","la",
            "le","li","lo","messo","nel","nella","nello","nei",
            "negli","nelle","per","quale","quali","quanto",
            "quanti","sei","si","sono","su","tra","un","una","uno"
        )

    fun execute(
        analysis: SearchAnalysisResult
    ): SearchResponse {

        val operationalQuery =
            analysis.operationalQuery
                ?: extractOperationalQuery(
                    analysis.originalQuery
                )

        return SearchResponse(
            success = true,
            message = "ENGINE_A_RESULT",
            operationalQuery =
                operationalQuery,
            debugMarker =
                "[M6] OPQ=$operationalQuery"
        )
    }

    private fun extractOperationalQuery(
        query: String
    ): String {

        val canonicalQuery =
            query
                .trim()
                .lowercase()
                .replace('’', '\'')
                .replace('ʼ', '\'')
                .replace('`', '\'')
                .replace(
                    Regex("\\bdov'\\s*[èe]'?\\b"),
                    "dove è"
                )
                .replace(
                    Regex("\\bqual\\s+è\\b"),
                    "quale è"
                )
                .replace(
                    Regex("\\bqual\\s+e'\\b"),
                    "quale è"
                )
                .replace(
                    Regex("\\bcom'\\s*è\\b"),
                    "come è"
                )
                .replace(
                    Regex("\\bcom'\\s*e'\\b"),
                    "come è"
                )
                .replace(
                    Regex("\\bcos'\\s*è\\b"),
                    "cosa è"
                )
                .replace(
                    Regex("\\bcos'\\s*e'\\b"),
                    "cosa è"
                )
                .replace(
                    Regex("\\bc'\\s*è\\b"),
                    "ci è"
                )
                .replace(
                    Regex("\\bc'\\s*e'\\b"),
                    "ci è"
                )
                .replace(
                    Regex("\\bs'\\s*è\\b"),
                    "si è"
                )
                .replace(
                    Regex("\\bs'\\s*e'\\b"),
                    "si è"
                )

        val indicators =
            lexicalMatrix.findIndicators(
                canonicalQuery
            )

        val simpleSearchIndicators =
            indicators[
                SearchLexicalIndicatorMatrix
                    .SIMPLE_SEARCH
            ] ?: emptySet()

        return canonicalQuery
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
            .filterNot {
                simpleSearchIndicators.contains(it)
            }
            .joinToString(" ")
    }
}