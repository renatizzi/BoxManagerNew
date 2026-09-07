package com.example.boxmanagernew.domain.qr

/**
 * Policy di imposizione etichette sul foglio (REQUISITI_QR_AVANZATO T2 / R5).
 * Separata dal layout etichetta V1.
 */
enum class LabelSheetSpec(
    val rows: Int,
    val cols: Int,
    /** Larghezza pagina PDF in punti (≈ 1/72"). */
    val pageWidthPt: Int,
    /** Altezza pagina PDF in punti. */
    val pageHeightPt: Int,
    val marginPt: Int,
    /** true = stampa tipica A4; false = A6 (L0). */
    val isoA4: Boolean
) {
    /** L0 — 1 etichetta / pagina (A6, come V1). */
    ONE_PER_PAGE(
        rows = 1,
        cols = 1,
        pageWidthPt = 298,
        pageHeightPt = 420,
        marginPt = 12,
        isoA4 = false
    ),

    /** L1 — A4 2×2 (4 etichette). */
    A4_2X2(
        rows = 2,
        cols = 2,
        pageWidthPt = 595,
        pageHeightPt = 842,
        marginPt = 18,
        isoA4 = true
    ),

    /** L1 — A4 2×3 (6 etichette). */
    A4_2X3(
        rows = 3,
        cols = 2,
        pageWidthPt = 595,
        pageHeightPt = 842,
        marginPt = 16,
        isoA4 = true
    );

    val labelsPerPage: Int get() = rows * cols

    fun pageCount(labelCount: Int): Int {
        if (labelCount <= 0) {
            return 0
        }
        return (labelCount + labelsPerPage - 1) / labelsPerPage
    }

    companion object {
        fun presets(): List<LabelSheetSpec> = entries.toList()
    }
}
