package com.example.boxmanagernew.family

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regole condivisione Invia (B4.9):
 * 1. L'utente può sempre cambiare cartella (pulsante Cartella nel box nome file).
 * 2. Dopo il primo CONSENTI, riuso cartella memorizzata senza riaprire il selettore.
 */
class FamilyExportCoordinatorPolicyTest {

    @Test
    fun req2_beginExport_reusesSavedFolderWithoutPicker() {
        val body = beginExportBody()
        assertTrue(body.contains("rememberedFolderUri()"))
        assertTrue(body.contains("askExportFileName"))
        assertTrue(body.contains("launchFolderPicker()"))
    }

    @Test
    fun req1_askExportFileName_offersFolderChange() {
        val body = askExportFileNameBody()
        assertTrue(body.contains("onBrowseFolder"))
        assertTrue(body.contains("launchFolderPicker()"))
    }

    @Test
    fun req2_onFolderChosen_persistsAuthorizationForReuse() {
        val body = onFolderChosenBody()
        assertTrue(body.contains("takePersistableUriPermission"))
        assertTrue(body.contains("rememberFolder"))
    }

    @Test
    fun req1_dialogUtils_hasCartellaNeutralButton() {
        val source = dialogUtilsSource()
        assertTrue(source.contains("setNeutralButton(\"Cartella\""))
        assertTrue(source.contains("onBrowseFolder"))
    }

    @Test
    fun exportSuccess_doesNotShowCompletionDialogInActivity() {
        val source = activitySource()
        assertFalse(
            "Invia non deve mostrare toast/dialog di conferma salvataggio",
            source.contains("buildExportSummary")
        )
        assertFalse(source.contains("Toast.makeText"))
    }

    @Test
    fun familyShareLayout_hidesVerticalScrollbars() {
        val layout = layoutSource()
        assertFalse(layout.contains("android:scrollbars=\"vertical\""))
        assertTrue(layout.contains("android:scrollbars=\"none\""))
    }

    private fun beginExportBody(): String =
        coordinatorSource()
            .substringAfter("fun beginExport")
            .substringBefore("fun onFolderChosen")

    private fun askExportFileNameBody(): String =
        coordinatorSource()
            .substringAfter("private fun askExportFileName")
            .substringBefore("private fun writeExport")

    private fun onFolderChosenBody(): String =
        coordinatorSource()
            .substringAfter("fun onFolderChosen")
            .substringBefore("fun cancelPending")

    private fun coordinatorSource(): String =
        familySource(
            "app/src/main/java/com/example/boxmanagernew/ui/family/FamilyExportCoordinator.kt"
        )

    private fun activitySource(): String =
        familySource(
            "app/src/main/java/com/example/boxmanagernew/ui/family/FamilyCatalogActivity.kt"
        )

    private fun dialogUtilsSource(): String =
        familySource(
            "app/src/main/java/com/example/boxmanagernew/ui/common/DialogUtils.kt"
        )

    private fun layoutSource(): String =
        familySource("app/src/main/res/layout/activity_family_catalog.xml")

    private fun familySource(relative: String): String {
        val candidates = listOf(
            File(relative.removePrefix("app/")),
            File(relative)
        )
        return candidates.first { it.isFile }.readText()
    }
}
