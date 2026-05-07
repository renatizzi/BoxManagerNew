package com.example.boxmanagernew.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BottomNavManager

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val PREFS = "boxmanager_settings"
        const val KEY_USERNAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_settings)

        val root = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val editUserName = findViewById<EditText>(R.id.editUserName)
        val buttonSave = findViewById<Button>(R.id.buttonSaveUser)
        val textSaveMessage = findViewById<TextView>(R.id.textSaveMessage)

        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        editUserName.setText(
            prefs.getString(KEY_USERNAME, "")
        )

        editUserName.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                editUserName.clearFocus()
                true
            } else false
        }

        buttonSave.setOnClickListener {

            val value = editUserName.text.toString().trim()

            prefs.edit()
                .putString(KEY_USERNAME, value)
                .apply()

            hideKeyboard()

            editUserName.clearFocus()

            textSaveMessage.visibility = View.VISIBLE
        }

        BottomNavManager.setup(this, BottomNavManager.TAB_SETTINGS)
    }

    private fun hideKeyboard() {

        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}