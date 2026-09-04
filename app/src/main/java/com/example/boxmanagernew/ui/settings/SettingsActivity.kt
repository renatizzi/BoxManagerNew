package com.example.boxmanagernew.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
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
import com.example.boxmanagernew.domain.locale.LocalePreference
import com.example.boxmanagernew.domain.premium.ArchivioCompletoAccess
import com.example.boxmanagernew.domain.premium.ArchivioCompletoPolicy
import com.example.boxmanagernew.domain.privacy.PrivacyPolicy
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.LocaleManager
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
    private lateinit var cardPrivacy: View

    private lateinit var optionItalian: LinearLayout
    private lateinit var optionEnglish: LinearLayout

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
        setupLanguageSelector()
        updateThemeLabel()
        setupDebugUnlock()
        setupUnlockCode()
        setupAdminParams()

        setupBottomNav()

        refreshAppShell()
        updateLanguageSelection()
    }

    private fun setupViews() {

        setupPageHeader(
            getString(R.string.page_settings_title),
            getString(R.string.page_settings_subtitle)
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

        cardPrivacy =
            findViewById(R.id.cardPrivacy)

        optionItalian =
            findViewById(R.id.optionItalian)

        optionEnglish =
            findViewById(R.id.optionEnglish)

        findViewById<TextView>(R.id.textPrivacyLabel).text =
            getString(R.string.privacy_settings_label)
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

        cardPrivacy.setOnClickListener {
            openPrivacyPolicy()
        }
    }

    private fun openPrivacyPolicy() {
        val intent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(PrivacyPolicy.PUBLIC_URL)
            )
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(
                this,
                PrivacyPolicy.PUBLIC_URL,
                Toast.LENGTH_LONG
            ).show()
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

    private fun setupLanguageSelector() {

        findViewById<View>(R.id.cardLanguage).visibility =
            View.VISIBLE

        updateLanguageSelection()

        optionItalian.setOnClickListener {
            selectLanguage(LocalePreference.IT)
        }

        optionEnglish.setOnClickListener {
            selectLanguage(LocalePreference.EN)
        }
    }

    private fun selectLanguage(languageTag: String) {

        if (
            LocaleManager.effectiveTag(this) ==
            LocalePreference.resolve(languageTag)
        ) {
            return
        }

        LocaleManager.setLanguage(this, languageTag)
    }

    private fun updateLanguageSelection() {

        resetPalette(optionItalian)
        resetPalette(optionEnglish)

        val selected =
            if (
                LocalePreference.isEnglish(
                    LocaleManager.effectiveTag(this)
                )
            ) {
                optionEnglish
            } else {
                optionItalian
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
                getString(R.string.settings_theme_current_dark)
            } else {
                getString(R.string.settings_theme_current_light)
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
        setupUnlockCode()
    }

    private fun setupUnlockCode() {

        val card =
            findViewById<View>(R.id.cardUnlockCode)

        if (BuildConfig.DEBUG) {
            card.visibility = View.GONE
            return
        }

        card.visibility = View.VISIBLE

        val access =
            ArchivioCompletoAccess(this)

        val title =
            findViewById<TextView>(R.id.textUnlockCodeTitle)

        val hint =
            findViewById<TextView>(R.id.textUnlockCodeHint)

        val inputLayout =
            findViewById<View>(R.id.layoutUnlockCode)

        val editCode =
            findViewById<EditText>(R.id.editUnlockCode)

        val buttonRedeem =
            findViewById<Button>(R.id.buttonRedeemUnlockCode)

        title.text =
            getString(R.string.premium_settings_code_title)

        editCode.hint =
            getString(R.string.premium_unlock_code_hint)

        buttonRedeem.text =
            getString(R.string.premium_button_redeem)

        if (access.isOpen()) {
            hint.text =
                getString(R.string.premium_settings_code_active)
            inputLayout.visibility = View.GONE
            buttonRedeem.visibility = View.GONE
            return
        }

        hint.text =
            getString(R.string.premium_settings_code_hint)

        inputLayout.visibility = View.VISIBLE
        buttonRedeem.visibility = View.VISIBLE

        buttonRedeem.setOnClickListener {
            val raw =
                editCode.text.toString()

            if (!access.redeemCode(raw)) {
                Toast.makeText(
                    this,
                    getString(R.string.premium_code_ko),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            Toast.makeText(
                this,
                getString(R.string.premium_code_ok),
                Toast.LENGTH_SHORT
            ).show()

            editCode.text?.clear()
            setupUnlockCode()
        }
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
            getString(R.string.premium_settings_params_title)

        findViewById<TextView>(R.id.textAdminParamsHint).text =
            getString(R.string.premium_settings_params_hint)

        findViewById<TextView>(R.id.textParamTrialLabel).text =
            getString(R.string.premium_settings_param_trial)

        findViewById<TextView>(R.id.textParamBonusLabel).text =
            getString(R.string.premium_settings_param_bonus)

        findViewById<TextView>(R.id.textParamFriendsLabel).text =
            getString(R.string.premium_settings_param_friends)

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
            getString(R.string.premium_settings_params_save)

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
                getString(R.string.premium_settings_params_saved),
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
            getString(R.string.premium_settings_unlock_title)

        findViewById<TextView>(R.id.textDebugUnlockHint).text =
            getString(R.string.premium_settings_unlock_hint)

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
            getString(R.string.premium_settings_expire_trial)

        expire.setOnClickListener {
            access.expireTrialForDebug()
            switchUnlock.isChecked = false
        }

        val restart =
            findViewById<Button>(R.id.buttonRestartTrial)

        restart.text =
            getString(R.string.premium_settings_restart_trial)

        restart.setOnClickListener {
            access.restartTrialForDebug()
            switchUnlock.isChecked = false
        }
    }
}