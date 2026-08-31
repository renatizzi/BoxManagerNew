package com.example.boxmanagernew.family

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class FamilyExportCoordinatorPolicyTest {

    @Test
    fun beginExport_alwaysLaunchesFolderPicker() {
        val beginExportBody = coordinatorSource()
            .substringAfter("fun beginExport")
            .substringBefore("fun onFolderChosen")

        assertTrue(beginExportBody.contains("launchFolderPicker()"))
        assertFalse(
            "beginExport non deve riusare la cartella memorizzata",
            beginExportBody.contains("rememberedFolderUri")
        )
    }

    private fun coordinatorSource(): String {
        val candidates = listOf(
            File("src/main/java/com/example/boxmanagernew/ui/family/FamilyExportCoordinator.kt"),
            File("app/src/main/java/com/example/boxmanagernew/ui/family/FamilyExportCoordinator.kt")
        )
        return candidates.first { it.isFile }.readText()
    }
}
