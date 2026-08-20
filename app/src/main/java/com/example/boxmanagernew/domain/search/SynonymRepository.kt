package com.example.boxmanagernew.domain.search

import com.example.boxmanagernew.domain.search.model.CoreEntityType

class SynonymRepository {

    fun getCoreEntityType(
        term: String
    ): CoreEntityType? {

        return SearchCoreAliases.coreEntityType(
            term
        )
    }
}
