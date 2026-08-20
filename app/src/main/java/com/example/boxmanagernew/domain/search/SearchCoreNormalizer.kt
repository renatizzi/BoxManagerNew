package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.SearchNormalizedToken

class SearchCoreNormalizer {

    fun normalize(
        token: String
    ): SearchNormalizedToken {

        val canonical =
            SearchCoreAliases.canonicalToken(
                token
            )

        val normalizedToken =
            canonical ?: token

        return SearchNormalizedToken(
            originalToken = token,
            normalizedToken = normalizedToken,
            isCoreEntityToken =
                canonical != null
        )
    }
}
