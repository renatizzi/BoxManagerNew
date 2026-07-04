package com.example.boxmanagernew.domain.search.model

data class PipelineOutput(

    val originalQuestion: String,

    val normalizedQuestion: String,

    val interpretation: SearchInterpretation,

    val recognizedEntities:
    SearchRecognizedEntitiesResult,

    val fulcrum:
    SearchFulcrumResult,

    val archivePath:
    SearchArchivePath? = null,

    val archiveTransformation:
    SearchArchiveTransformation? = null,

    val requestType:
    SearchRequestType,

    val archiveQuery:
    SearchArchiveQuery? = null
)