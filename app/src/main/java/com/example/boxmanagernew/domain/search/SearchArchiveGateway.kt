package com.example.boxmanagernew.domain.search

class SearchArchiveGateway {

    fun hasMatches(
        searchText: String
    ): Boolean {

        return searchText
            .trim()
            .isNotBlank()
    }
}