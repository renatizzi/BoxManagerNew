package com.example.boxmanagernew.domain.search.model

data class SearchArchivalHits(

    val locations: List<String> = emptyList(),

    val categories: List<String> = emptyList(),

    val objects: List<String> = emptyList(),

    val boxes: List<String> = emptyList()
)
