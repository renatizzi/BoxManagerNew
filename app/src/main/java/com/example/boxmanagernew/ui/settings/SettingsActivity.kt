package com.example.boxmanagernew.ui.settings

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.premium.ArchivioCompletoAccess
import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy
import com.example.boxmanagernew.domain.premium.ArchivioCompletoPolicy
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.ThemeManager
import com.google.android.material.switchmaterial.SwitchMaterial

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
    private lateinit var cardLocations: View

    private var currentPalette =
        ThemeManager.PALETTE_ORANGE

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_settings
        )

        setupAppShell()
        setupViews()
        loadPreferences()
        setupListeners()
        setupPaletteSelector()
        updateThemeLabel()
        setupDebugUnlock()
        setupAdminParams()

        setupBottomNav()

        refreshAppShell()
    }

    private fun setupViews() {

        setupPageHeader(
            "Impostazioni",
            "Setup Archivio"
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

        cardLocations =
            findViewById(R.id.cardLocations)
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

            } else false
        }

        buttonSave.setOnClickListener {
            saveUsername()
        }

        cardLocations.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LocationsActivity::class.java
                )
            )
        }
    }

    private fun setupPaletteSelector() {

        currentPalette =
            detectCurrentPalette()

        updatePaletteSelection()

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

    private fun detectCurrentPalette(): String {

        val color =
            ThemeManager.getAccentDarkColor(this)

        return when (color) {

            0xFF0D47A1.toInt() ->
                ThemeManager.PALETTE_BLUE

            0xFF1B5E20.toInt() ->
                ThemeManager.PALETTE_GREEN

            else ->
                ThemeManager.PALETTE_ORANGE
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

        refreshAppShell()

        updatePaletteSelection()
    }

    private fun updatePaletteSelection() {

        resetPalette(paletteOrange)
        resetPalette(paletteBlue)
        resetPalette(paletteGreen)

        val selected =
            when (currentPalette) {

                ThemeManager.PALETTE_BLUE ->
                    paletteBlue

                ThemeManager.PALETTE_GREEN ->
                    paletteGreen

                else ->
                    paletteOrange
            }

        val drawable =
            GradientDrawable().apply {

                shape =
                    GradientDrawable.RECTANGLE

                cornerRadius =
                    16f

                setColor(
                    ContextCompat.getColor(
                        this@SettingsActivity,
                        R.color.list_row_fill
                    )
                )

                setStroke(
                    6,
                    ThemeManager.getAccentDarkColor(
                        this@SettingsActivity
                    )
                )
            }

        selected.background =
            drawable
    }

    private fun resetPalette(
        view: LinearLayout
    ) {

        view.setBackgroundResource(
            R.drawable.bg_box_selector
        )
    }

    private fun updateThemeLabel() {

        textCurrentTheme.text =
            if (
                ThemeManager.isNightModeEnabled(
                    this
                )
            ) {
                "Tema corrente: Dark"
            } else {
                "Tema corrente: Light"
            }
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

        setupAdminParams()
    }

    private fun setupAdminParams() {

        val card =
            findViewById<View>(R.id.cardAdminParams)

        val username =
            editUserName.text
                .toString()
                .trim()
                .ifEmpty {
                    getSharedPreferences(
                        PREFS,
                        Context.MODE_PRIVATE
                    ).getString(KEY_USERNAME, "") ?: ""
                }

        if (!ArchivioCompletoPolicy.isAdminUsername(username)) {
            card.visibility = View.GONE
            return
        }

        card.visibility = View.VISIBLE

        val access =
            ArchivioCompletoAccess(this)

        findViewById<TextView>(R.id.textAdminParamsTitle).text =
            ArchivioCompletoCopy.SETTINGS_PARAMS_TITLE

        findViewById<TextView>(R.id.textAdminParamsHint).text =
            ArchivioCompletoCopy.SETTINGS_PARAMS_HINT

        findViewById<TextView>(R.id.textParamTrialLabel).text =
            ArchivioCompletoCopy.SETTINGS_PARAM_TRIAL

        findViewById<TextView>(R.id.textParamBonusLabel).text =
            ArchivioCompletoCopy.SETTINGS_PARAM_BONUS

        findViewById<TextView>(R.id.textParamFriendsLabel).text =
            ArchivioCompletoCopy.SETTINGS_PARAM_FRIENDS

        val editTrial =
            findViewById<EditText>(R.id.editParamTrialDays)

        val editBonus =
            findViewById<EditText>(R.id.editParamShareBonusDays)

        val editFriends =
            findViewById<EditText>(R.id.editParamShareFriends)

        editTrial.setText(access.trialDays().toString())
        editBonus.setText(access.shareBonusDays().toString())
        editFriends.setText(access.shareFriendsRequired().toString())

        val saveParams =
            findViewById<Button>(R.id.buttonSaveAdminParams)

        saveParams.text =
            ArchivioCompletoCopy.SETTINGS_PARAMS_SAVE

        saveParams.setOnClickListener {
            val trial =
                editTrial.text.toString().toIntOrNull()
                    ?: ArchivioCompletoPolicy.DEFAULT_TRIAL_DAYS

            val bonus =
                editBonus.text.toString().toIntOrNull()
                    ?: ArchivioCompletoPolicy.DEFAULT_SHARE_BONUS_DAYS

            val friends =
                editFriends.text.toString().toIntOrNull()
                    ?: ArchivioCompletoPolicy.DEFAULT_SHARE_FRIENDS

            access.saveParams(trial, bonus, friends)

            editTrial.setText(access.trialDays().toString())
            editBonus.setText(access.shareBonusDays().toString())
            editFriends.setText(access.shareFriendsRequired().toString())

            Toast.makeText(
                this,
                ArchivioCompletoCopy.SETTINGS_PARAMS_SAVED,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupDebugUnlock() {

        val card =
            findViewById<View>(R.id.cardDebugUnlock)

        if (!BuildConfig.DEBUG) {
            card.visibility = View.GONE
            return
        }

        card.visibility = View.VISIBLE

        val access =
            ArchivioCompletoAccess(this)

        findViewById<TextView>(R.id.textDebugUnlockTitle).text =
            ArchivioCompletoCopy.SETTINGS_UNLOCK_TITLE

        findViewById<TextView>(R.id.textDebugUnlockHint).text =
            ArchivioCompletoCopy.SETTINGS_UNLOCK_HINT

        val switchUnlock =
            findViewById<SwitchMaterial>(R.id.switchDebugUnlock)

        switchUnlock.isChecked =
            access.isDebugUnlock()

        switchUnlock.setOnCheckedChangeListener { _, checked ->
            access.setDebugUnlock(checked)
        }

        val expire =
            findViewById<Button>(R.id.buttonResetTrials)

        expire.text =
            ArchivioCompletoCopy.SETTINGS_EXPIRE_TRIAL

        expire.setOnClickListener {
            access.expireTrialForDebug()
            switchUnlock.isChecked = false
        }

        val restart =
            findViewById<Button>(R.id.buttonRestartTrial)

        restart.text =
            ArchivioCompletoCopy.SETTINGS_RESTART_TRIAL

        restart.setOnClickListener {
            access.restartTrialForDebug()
            switchUnlock.isChecked = false
        }
    }
}