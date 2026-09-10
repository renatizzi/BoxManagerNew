package com.example.boxmanagernew.premium

import com.example.boxmanagernew.domain.premium.PremiumFeature
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Gate M3: Disco + Condividi Archivio = Archivio completo (SI 10/09).
 */
class PremiumFeatureM3GateTest {

    @Test
    fun enum_includesNetworkDriveAndArchiveShare() {
        assertEquals(
            setOf(
                "ADVANCED_SEARCH",
                "QR_SCAN",
                "QR_LABEL",
                "IMPORT",
                "EXPORT",
                "NETWORK_DRIVE",
                "ARCHIVE_SHARE"
            ),
            PremiumFeature.entries.map { it.name }.toSet()
        )
    }

    @Test
    fun strings_existForNewPremiumPitches() {
        assertTrue(stringIt("premium_feature_network_drive").contains("Disco"))
        assertTrue(stringEn("premium_feature_network_drive").contains("Network"))
        assertTrue(stringIt("premium_feature_archive_share").contains("Condividi"))
        assertTrue(stringEn("premium_feature_archive_share").contains("Share"))
        assertTrue(stringIt("premium_pitch_network_drive_lead").isNotBlank())
        assertTrue(stringEn("premium_pitch_archive_share_example").contains("Receive"))
    }

    @Test
    fun playFlavor_enablesFamilyBetaForM3Prep() {
        val gradle = File("build.gradle.kts").readText()
        // play block: FAMILY_BETA true e nessun suffix package (prima di create famiglia)
        val playIdx = gradle.indexOf("create(\"play\")")
        val famIdx = gradle.indexOf("create(\"famiglia\")")
        require(playIdx >= 0 && famIdx > playIdx) {
            "play/famiglia flavors missing"
        }
        val playBlock = gradle.substring(playIdx, famIdx)
        assertTrue(
            playBlock.contains("buildConfigField(\"boolean\", \"FAMILY_BETA\", \"true\")")
        )
        assertTrue(!playBlock.contains("applicationIdSuffix ="))
    }

    @Test
    fun utilityAndSettings_wirePremiumGates() {
        val utility =
            File("src/main/java/com/example/boxmanagernew/ui/utility/UtilityActivity.kt")
                .readText()
        assertTrue(utility.contains("PremiumFeature.ARCHIVE_SHARE"))
        assertTrue(utility.contains("ArchivioCompletoNav.start"))
        val settings =
            File("src/main/java/com/example/boxmanagernew/ui/settings/SettingsActivity.kt")
                .readText()
        assertTrue(settings.contains("PremiumFeature.NETWORK_DRIVE"))
        val family =
            File("src/main/java/com/example/boxmanagernew/ui/family/FamilyCatalogActivity.kt")
                .readText()
        assertTrue(family.contains("PremiumFeature.ARCHIVE_SHARE"))
        assertTrue(family.contains("allowActivity"))
    }

    private fun stringIt(name: String): String =
        stringValue("src/main/res/values/strings.xml", name)

    private fun stringEn(name: String): String =
        stringValue("src/main/res/values-en/strings.xml", name)

    private fun stringValue(path: String, name: String): String {
        val xml = File(path).readText()
        val match =
            Regex(
                """<string name="$name">(.*?)</string>""",
                RegexOption.DOT_MATCHES_ALL
            ).find(xml)
                ?: error("missing $name in $path")
        return match.groupValues[1]
            .replace("\\'", "'")
            .replace("\\\"", "\"")
            .replace("\\n", "\n")
    }
}
