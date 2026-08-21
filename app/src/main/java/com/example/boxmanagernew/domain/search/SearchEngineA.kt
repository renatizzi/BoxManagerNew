package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchAnalysisResult
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.util.CanonicalNormalizer

class SearchEngineA(

    private val lexicalMatrix:
    SearchLexicalIndicatorMatrix =
        SearchLexicalIndicatorMatrix()
) {

    private val stopWords =
        setOf(
            "a","ad","al","alla","allo","che","con","da","del",
            "della","dello","dei","degli","delle","di",
            "e","è","é","gli","ha","hai","ho","i","il","in","l","la",
            "le","li","lo","nel","nella","nello","nei",
            "negli","nelle","per","quale","quali","quanto",
            "quanti","sei","si","sono","su","tra","un","una","uno",
            "trova","cerca","mostra","dammi","dimmi","dove"
        )

    private val archivalVerbs =
        setOf(
            "mettere",
            "messo",
            "messa",
            "messi",
            "messe",

            "conservare",
            "conservato",
            "conservata",
            "conservati",
            "conservate",

            "riporre",
            "riposto",
            "riposta",
            "riposti",
            "riposte",

            "collocare",
            "collocato",
            "collocata",
            "collocati",
            "collocate",

            "depositare",
            "depositato",
            "depositata",
            "depositati",
            "depositate",

            "sistemare",
            "sistemato",
            "sistemata",
            "sistemati",
            "sistemate",

            "custodire",
            "custodisco",
            "custodisci",
            "custodisce",
            "custodiamo",
            "custodiscono",
            "custodito",
            "custodita",
            "custoditi",
            "custodite",

            "archiviare",
            "archiviato",
            "archiviata",
            "archiviati",
            "archiviate"
        )

    private val listScopeWords =
        setOf(
            "contenitore",
            "contenitori",
            "oggetto",
            "oggetti",
            "categoria",
            "categorie",
            "fammi",
            "vedere",
            "vedi",
            "mostrami",
            "quali",
            "quale",
            "contengono",
            "contiene",
            "elenco",
            "lista",
            "trovano",
            "trovo",
            "trovato",
            "trovati",
            "trovate",
            "tutto",
            "tutta",
            "tutti",
            "tutte",
            "quello",
            "quella",
            "quelli",
            "quelle"
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
            dominantFulcrum =
                analysis.dominantFulcrum,
            debugMarker =
                "[M9] QUERY=$operationalQuery"
        )
    }

    private fun extractOperationalQuery(
        query: String
    ): String {

        val canonicalQuery =
            SearchNormalizer()
                .normalize(
                    query
                )
                .normalizedQuestion

        val indicators =
            lexicalMatrix.findIndicators(
                canonicalQuery
            )

        val indicatorTerms =
            indicators.values.flatten().toSet()

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
                archivalVerbs.contains(it)
            }
            .filterNot { token ->

                indicatorTerms.any { term ->

                    CanonicalNormalizer.wholeWordMatches(
                        token,
                        term
                    )
                }
            }
            .filterNot {
                listScopeWords.contains(it) ||
                        SearchCoreAliases.isLocationAlias(it) ||
                        SearchCoreAliases.isObjectAlias(it) ||
                        SearchCoreAliases.isCategoryAlias(it) ||
                        SearchCoreAliases.isBoxAlias(it)
            }
            .joinToString(" ")
    }
}