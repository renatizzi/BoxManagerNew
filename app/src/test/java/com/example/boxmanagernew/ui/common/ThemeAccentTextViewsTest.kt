package com.example.boxmanagernew.ui.common

import android.view.View
import com.example.boxmanagernew.R
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeAccentTextViewsTest {

    @Test
    fun appliesAccent_forDashboardAndUtilityCardLabels() {
        assertTrue(
            ThemeAccentTextViews.appliesAccent(
                R.id.titleBoxes
            )
        )
        assertTrue(
            ThemeAccentTextViews.appliesAccent(
                R.id.textDashboardQuickBackup
            )
        )
        assertTrue(
            ThemeAccentTextViews.appliesAccent(
                R.id.textBackup
            )
        )
    }

    @Test
    fun appliesAccent_forFamilyShareButtons() {
        assertTrue(
            ThemeAccentTextViews.appliesAccent(
                R.id.textExportSharedTables
            )
        )
        assertTrue(
            ThemeAccentTextViews.appliesAccent(
                R.id.textImportMerge
            )
        )
    }

    @Test
    fun appliesAccent_ignoresUnregisteredViews() {
        assertFalse(
            ThemeAccentTextViews.appliesAccent(
                R.id.textTitle
            )
        )
        assertFalse(
            ThemeAccentTextViews.appliesAccent(
                View.NO_ID
            )
        )
    }
}
