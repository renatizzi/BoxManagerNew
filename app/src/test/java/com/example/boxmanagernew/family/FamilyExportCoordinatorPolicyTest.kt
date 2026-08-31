package com.example.boxmanagernew.family

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Regole condivisione Invia (B4.8):
 * 1. Selettore cartella sempre
 * 2. takePersistableUriPermission solo in onFolderChosen (consenso Android prima volta)
 */
class FamilyExportCoordinatorPolicyTest {

    @Test
    fun req1_beginExport_alwaysOpensFolderPicker() {
        val body = beginExportBody()
        assertTrue(body.contains("launchFolderPicker()"))
        assertFalse(
            "beginExport non deve saltare il selettore",
            body.contains("askExportFileName")
        )
        assertFalse(
            "beginExport non deve decidere in base a rememberedFolderUri",
            body.contains("rememberedFolderUri")
        )
    }

    @Test
    fun req2_onFolderChosen_persistsAuthorizationForReuse() {
        val body = onFolderChosenBody()
        assertTrue(body.contains("takePersistableUriPermission"))
        assertTrue(body.contains("rememberFolder"))
    }

    @Test
    fun req1_activity_launchesPickerWithInitialUriWhenKnown() {
        val source = activitySource()
        assertTrue(
            source.contains("folderPicker.launch(exportPersister.rememberedFolderUri())")
        )
    }

    private fun beginExportBody(): String =
        coordinatorSource()
            .substringAfter("fun beginExport")
            .substringBefore("fun onFolderChosen")

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

    private fun familySource(relative: String): String {
        val candidates = listOf(
            File(relative.removePrefix("app/")),
            File(relative)
        )
        return candidates.first { it.isFile }.readText()
    }
}
