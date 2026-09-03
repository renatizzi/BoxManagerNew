package com.example.boxmanagernew.ui.common

import android.view.View
import com.example.boxmanagernew.R

/**
 * TextView che ricevono il colore accent della palette scelta in Impostazioni.
 * Indipendente dalla lingua: non confrontare il testo visualizzato.
 */
object ThemeAccentTextViews {

  private val accentViewIds: Set<Int> =
      setOf(
          // Dashboard — card KPI
          R.id.titleBoxes,
          R.id.titleCategories,
          // Dashboard — accesso rapido
          R.id.textDashboardQuickBackup,
          R.id.textDashboardQuickRestore,
          R.id.textDashboardQuickQr,
          R.id.textDashboardQuickImport,
          // Utility
          R.id.textBackup,
          R.id.textRestore,
          R.id.textImport,
          R.id.textQr,
          R.id.textFamilyCatalog,
          // Condivisione archivio (flavor famiglia)
          R.id.textExportSharedTables,
          R.id.textImportSharedTables,
          R.id.textExportMerge,
          R.id.textImportMerge
      )

  fun appliesAccent(view: View): Boolean =
      appliesAccent(view.id)

  fun appliesAccent(viewId: Int): Boolean {
    return viewId != View.NO_ID && viewId in accentViewIds
  }
}
