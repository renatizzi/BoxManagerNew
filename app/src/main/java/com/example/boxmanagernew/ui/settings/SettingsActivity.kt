package com.example.boxmanagernew.ui.settings

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.ThemeManager

class SettingsActivity : BaseActivity() {

    companion object {

        const val PREFS =
            "boxmanager_settings"

        const val KEY_USERNAME =
            "username"
    }

    private lateinit var editUserName: EditText
    private lateinit var buttonSave: Button
    private lateinit var textSaveMessage: View

    private lateinit var paletteOrange: LinearLayout
    private lateinit var paletteBlue: LinearLayout
    private lateinit var paletteGreen: LinearLayout

    private lateinit var textCurrentTheme: TextView

    private var currentPalette =
        ThemeManager.PALETTE_ORANGE

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setupEdgeToEdge()

        setupTopBar()

        setupViews()

        loadPreferences()

        setupListeners()

        setupPaletteSelector()

        updateThemeLabel()

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_SETTINGS
        )
    }

    private fun setupViews() {

        setupPageHeader(
            title = "Impostazioni",
            subtitle = "Setup Archivio"
        )

        editUserName =
            findViewById(R.id.editUserName)

        buttonSave =
            findViewById(R.id.buttonSaveUser)

        textSaveMessage =
            findViewById(R.id.textSaveMessage)

        paletteOrange =
            findViewById(R.id.paletteOrange)

        paletteBlue =
            findViewById(R.id.paletteBlue)

        paletteGreen =
            findViewById(R.id.paletteGreen)

        textCurrentTheme =
            findViewById(R.id.textCurrentTheme)
    }

    private fun loadPreferences() {

        val prefs =
            getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )

        editUserName.setText(
            prefs.getString(
                KEY_USERNAME,
                ""
            )
        )
    }

    private fun setupListeners() {

        editUserName.setOnEditorActionListener {
                _,
                actionId,
                _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                hideKeyboardAndClearFocus()

                true

            } else {

                false
            }
        }

        buttonSave.setOnClickListener {

            saveUsername()
        }
    }

    private fun setupPaletteSelector() {

        selectPalette(
            ThemeManager.PALETTE_ORANGE
        )

        paletteOrange.setOnClickListener {

            selectPalette(
                ThemeManager.PALETTE_ORANGE
            )
        }

        paletteBlue.setOnClickListener {

            selectPalette(
                ThemeManager.PALETTE_BLUE
            )
        }

        paletteGreen.setOnClickListener {

            selectPalette(
                ThemeManager.PALETTE_GREEN
            )
        }
    }

    private fun selectPalette(
        palette: String
    ) {

        currentPalette =
            palette

        ThemeManager.applyPalette(
            this,
            palette
        )

        refreshTopBar()

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_SETTINGS
        )

        updatePaletteSelection()
    }

    private fun updatePaletteSelection() {

        resetPalette(
            paletteOrange
        )

        resetPalette(
            paletteBlue
        )

        resetPalette(
            paletteGreen
        )

        val selectedView =
            when (currentPalette) {

                ThemeManager.PALETTE_BLUE ->
                    paletteBlue

                ThemeManager.PALETTE_GREEN ->
                    paletteGreen

                else ->
                    paletteOrange
            }

        selectedView.setBackgroundResource(
            R.drawable.bg_context_card
        )

        selectedView.alpha = 1f
    }

    private fun resetPalette(
        view: LinearLayout
    ) {

        view.setBackgroundResource(
            R.drawable.bg_box_selector
        )

        view.alpha = 0.92f
    }

    private fun updateThemeLabel() {

        val currentNightMode =
            resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK

        val themeName =
            if (
                currentNightMode ==
                Configuration.UI_MODE_NIGHT_YES
            ) {

                "Dark"

            } else {

                "Light"
            }

        textCurrentTheme.text =
            "Tema corrente: $themeName"
    }

    private fun saveUsername() {

        val value =
            editUserName.text
                .toString()
                .trim()

        getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        ).edit()
            .putString(
                KEY_USERNAME,
                value
            )
            .apply()

        refreshTopBar()

        hideKeyboardAndClearFocus()

        textSaveMessage.visibility =
            View.VISIBLE
    }
}