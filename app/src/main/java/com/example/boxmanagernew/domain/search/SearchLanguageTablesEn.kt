package com.example.boxmanagernew.domain.search

/**
 * Tabelle EN ufficiali (bozza CK0 SI + S1–S3).
 * Elenchi interi 1.3.3 / 3.3.5 / 4.5 / 4.17. Plurali = solo matching.
 * Rumore fase 1: elenco chiuso (S2), non un interprete.
 */
object SearchLanguageTablesEn {

    val objectTerms =
        setOf(
            "object", "objects",
            "article", "articles",
            "item", "items",
            "utensil", "utensils",
            "thing", "things",
            "affair", "affairs",
            "stuff",
            "product", "products",
            "tool", "tools"
        )

    val boxTerms =
        setOf(
            "container", "containers",
            "box", "boxes",
            "carton", "cartons",
            "crate", "crates",
            "pack", "packs",
            "trunk", "trunks",
            "envelope", "envelopes",
            "mailer", "mailers",
            "drawer", "drawers",
            "jar", "jars",
            "vase", "vases",
            "basin", "basins",
            "receptacle", "receptacles",
            "chest", "chests",
            "coffer", "coffers",
            "bin", "bins",
            "dumpster", "dumpsters",
            "safe", "safes",
            "wallet", "wallets",
            "organizer", "organizers",
            "jewelbox", "jewelboxes",
            "briefcase", "briefcases",
            "wrapping", "wrappings",
            "case", "cases",
            "cover", "covers",
            "packaging",
            "closet", "closets",
            "wardrobe", "wardrobes",
            "cabinet", "cabinets",
            "bookcase", "bookcases",
            "shelf", "shelves"
        )

    val locationTerms =
        setOf(
            "location", "locations",
            "place", "places",
            "spot", "spots",
            "site", "sites",
            "area", "areas",
            "zone", "zones",
            "perimeter", "perimeters",
            "space", "spaces",
            "room", "rooms",
            "city", "cities",
            "town", "towns",
            "locality", "localities",
            "point", "points"
        )

    val categoryTerms =
        setOf(
            "category", "categories",
            "class", "classes",
            "classification", "classifications",
            "group", "groups",
            "aggregate", "aggregates",
            "grouping", "groupings",
            "species",
            "family", "families",
            "order", "orders",
            "division", "divisions",
            "grade", "grades",
            "tier", "tiers",
            "type", "types",
            "typology", "typologies",
            "quality", "qualities",
            "kind", "kinds"
        )

    val confrontoTerms =
        setOf(
            "identical",
            "same",
            "duplicate",
            "duplicates",
            "different",
            "comparison"
        )

    val aggregazioneTerms =
        setOf(
            "all",
            "list",
            "which"
        )

    val locationClues =
        setOf(
            "where"
        )

    val duplicateConfronto =
        setOf(
            "identical",
            "same",
            "duplicate",
            "duplicates"
        )

    val f7Variants =
        listOf(
            "Search all the containers that contain duplicates",
            "In which containers are there identical objects",
            "List of the containers that have identical objects",
            "Where do I find the same type of objects",
            "Find the containers that have at least one identical object"
        )

    const val F7_HEADING =
        "List of the containers that have identical objects"

    val f8Variants =
        listOf(
            "Search the containers with a different category that contain the same type of object",
            "Which containers have a different category and contain identical objects",
            "Find containers with a different category and identical objects",
            "List of containers with a different category and identical objects"
        )

    const val F8_HEADING =
        "List of the containers that have a different category and contain identical objects"

    const val MSG_NOT_UNDERSTOOD =
        "I did not understand the request."

    const val MSG_CLARIFY =
        "Can you phrase the request more precisely?"

    const val MSG_HOMONYM_PREFIX =
        "Rephrase the question so it is clear whether you mean"

    const val PHRASE_OBJECT =
        "an object"

    const val PHRASE_BOX =
        "a container"

    const val PHRASE_LOCATION =
        "a location"

    const val PHRASE_CATEGORY =
        "a category"

    const val MSG_NO_RESULTS =
        "No results found."

    const val MSG_INTERROGATION_UNAVAILABLE =
        "This type of request is not yet available."

    val functionWords =
        setOf(
            "a", "an", "the", "of", "to", "for", "from", "at", "by",
            "on", "in", "with", "without", "and", "or", "but", "if", "as",
            "that", "this", "these", "those", "it", "its", "my", "me",
            "i", "you", "we", "they", "is", "are", "am", "was", "were",
            "be", "been", "being", "do", "does", "did", "have", "has",
            "had", "not", "no", "so", "too", "very", "just", "than",
            "then", "there", "here", "into", "onto",
            // Specchio IT (quale/quali/dove/elenco/lista/tutto): senza queste
            // restano in contentTokens e matchingNames richiede «what»/«in»
            // dentro il nome luogo → Cellar/Garage non riconosciuti in EN.
            "what", "which", "where", "who", "whom", "whose",
            "how", "when", "why",
            "all", "every", "everything", "list",
            "used", "use",
            "find", "search", "show", "tell", "give", "see", "looking",
            "look", "contain", "contains", "containing", "contained",
            "name", "names"
        )

    /** S2: locuzioni fisse, elenco chiuso. Non togliere type of / kind of (S3). */
    val noisePhrases =
        listOf(
            "in order to"
        )
}
