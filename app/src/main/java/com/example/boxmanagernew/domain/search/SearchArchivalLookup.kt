package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchArchivalHits
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex

/**
 * Individua gli elementi effettivamente presenti in archivio.
 * Un alias Core (es. *box*) è solo un indizio: se lo stesso termine
 * compare nei nomi, prevale l'evidenza d'archivio (Nota R7–R8).
 */
class SearchArchivalLookup {

    fun find(
        question: String,
        index: SearchArchiveIndex
    ): SearchArchivalHits {

        return SearchArchivalHits(
            locations =
                matchingNames(
                    questionWithoutBoxAliases(
                        question
                    ),
                    index.locations
                ),
            categories =
                matchingNames(
                    questionWithoutBoxAliases(
                        question
                    ),
                    index.categories
                ),
            objects =
                matchingNames(
                    question,
                    index.objects
                ),
            boxes =
                matchingBoxNames(
                    question,
                    index.boxes
                )
        )
    }

    private fun questionWithoutBoxAliases(
        question: String
    ): String {

        return SearchNameMatcher.contentTokens(
            question
        ).filterNot { token ->
            SearchCoreAliases.isBoxAlias(
                token
            )
        }.joinToString(" ")
    }

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
                        SearchCoreAliases.isObjectAlias(token)
            }

        if (specific.isNotEmpty()) {

            return matchingNames(
                question,
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
