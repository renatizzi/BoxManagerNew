package com.example.boxmanagernew.domain.search.model

data class SearchResponse(

    val success: Boolean,

    val message: String,

    val operationalQuery: String? = null,

    val requiresClarification: Boolean = false,

    val clarificationType: SearchClarificationType =
        SearchClarificationType.NONE,

    val dominantFulcrum: SearchFulcrum? = null,

    val locationTerms: String = "",

    val categoryTerms: String = "",

    val boxTerms: String = "",

    val objectTerms: String = "",

    val highlightTerms: String = "",

    val archiveTransformation:
    SearchArchiveTransformation? = null,

    val requestType: SearchRequestType? = null,

    val debugMarker: String? = null,

    val resultBoxNames: List<String> = emptyList(),

    val resultObjectNames: List<String> = emptyList(),

    /** Pattern Motore B (es. PATTERN_007 / F7); null se non applicabile. */
    val matchedPatternId: String? = null
)