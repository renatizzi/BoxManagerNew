package com.example.boxmanagernew.domain.locale

/**
 * Nomi ufficiali di seed (AppDatabase) IT → EN.
 * Riscrivono solo le righe ancora uguali al default IT
 * al primo switch italiano → inglese (B-DEFAULT-IT-EN).
 */
object DefaultArchiveLabels {

    const val PREFS_FLAG =
        "defaults_en_applied"

    /** 16 categorie di seed + 3 posizioni. */
    val categoryItToEn =
        mapOf(
            "Abbigliamento e Calzature" to
                "Clothing and Footwear",
            "Alimenti e Bevande" to
                "Food and Beverages",
            "Attrezzi, Strumenti e Ferramenta" to
                "Tools, Instruments and Hardware",
            "Bricolage e Materiali" to
                "DIY and Materials",
            "Cancelleria e Scuola" to
                "Stationery and School",
            "Collezionismo" to
                "Collectibles",
            "Documenti e Archivi" to
                "Documents and Archives",
            "Elettronica e Informatica" to
                "Electronics and IT",
            "Fai da te" to
                "Do-it-yourself",
            "Foto e Video" to
                "Photo and Video",
            "Hobby" to
                "Hobby",
            "Imballaggi e Contenitori" to
                "Packaging and Containers",
            "Libri e Riviste" to
                "Books and Magazines",
            "Medicinali e Salute" to
                "Medicines and Health",
            "Oggetti di valore" to
                "Valuables",
            "Miscellanea" to
                "Miscellaneous"
        )

    val locationItToEn =
        mapOf(
            "Garage" to "Garage",
            "Cantina" to "Basement",
            "Soffitta" to "Attic"
        )
}
