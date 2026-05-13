package com.example.boxmanagernew.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

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

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        setupEdgeToEdge()

        setupTopBar()

        setupViews()

        loadPreferences()

        setupListeners()

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