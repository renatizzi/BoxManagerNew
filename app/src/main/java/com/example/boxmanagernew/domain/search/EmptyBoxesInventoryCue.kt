package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.util.CanonicalNormalizer

/**
 * Inventario Motore A (termini vuoti, fulcro BOX): riconosce
 * la domanda sui contenitori vuoti per riusare FILTER_EMPTY_BOXES.
 */
object EmptyBoxesInventoryCue {

    fun matches(
        question: String
    ): Boolean {

        val normalized =
            SearchNormalizer()
                .normalize(question)
                .normalizedQuestion

        val tokens =
            CanonicalNormalizer.wordTokens(
                normalized
            )

        val emptyCue =
            tokens.any { token ->
                token == "vuoto" ||
                    token == "vuoti" ||
                    token == "empty"
            }

        val boxCue =
            tokens.any { token ->
                SearchCoreAliases.isBoxAlias(
                    token
                )
            }

        return emptyCue && boxCue
    }
}
