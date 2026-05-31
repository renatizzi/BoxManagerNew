package com.example.boxmanagernew.domain.search.model

data class SearchTokenizedQuestion(

    val normalizedQuestion: String,

    val tokens: List<String>
)