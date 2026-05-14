package com.example.boxmanagernew.ui.common

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.R
import com.google.android.material.card.MaterialCardView

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        ThemeManager.initializeDefaults(
            this
        )
    }

    override fun onResume() {

        super.onResume()

        refreshAppShell()
    }

    protected fun setupEdgeToEdge() {

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        val root =
            findViewById<View>(
                android.R.id.content
            )

        ViewCompat.setOnApplyWindowInsetsListener(
            root
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    protected fun setupTopBar() {

        val title =
            findViewById<TextView>(
                R.id.textGlobalTitle
            )

        val subtitle =
            findViewById<TextView>(
                R.id.textGlobalSubtitle
            )

        val topBar =
            findViewById<MaterialCardView>(
                R.id.globalTopBar
            )

        if (
            title != null &&
            subtitle != null
        ) {

            TopBarUtils.bindTopBar(
                this,
                title,
                subtitle
            )
        }

        topBar?.setCardBackgroundColor(
            ThemeManager.getTopBarBackground(
                this
            )
        )
    }

    protected fun setupPageHeader(
        title: String,
        subtitle: String
    ) {

        findViewById<TextView>(
            R.id.textTitle
        )?.text =
            title

        findViewById<TextView>(
            R.id.textSubtitle
        )?.text =
            subtitle
    }

    protected fun refreshTopBar() {

        val title =
            findViewById<TextView>(
                R.id.textGlobalTitle
            )

        val subtitle =
            findViewById<TextView>(
                R.id.textGlobalSubtitle
            )

        val topBar =
            findViewById<MaterialCardView>(
                R.id.globalTopBar
            )

        title?.let {

            TopBarUtils.bindTitle(
                this,
                it
            )
        }

        subtitle?.let {

            TopBarUtils.bindSubtitle(
                this,
                it
            )
        }

        topBar?.setCardBackgroundColor(
            ThemeManager.getTopBarBackground(
                this
            )
        )
    }

    protected fun refreshAppShell() {

        refreshTopBar()
    }

    protected fun hideKeyboard(
        view: View?
    ) {

        if (view == null) {
            return
        }

        val imm =
            getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        imm.hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

    protected fun hideKeyboardAndClearFocus() {

        currentFocus?.let {

            hideKeyboard(it)

            it.clearFocus()
        }
    }

    override fun dispatchTouchEvent(
        ev: MotionEvent
    ): Boolean {

        if (
            ev.action ==
            MotionEvent.ACTION_DOWN
        ) {

            val view =
                currentFocus

            if (view is EditText) {

                val rect =
                    Rect()

                view.getGlobalVisibleRect(rect)

                if (
                    !rect.contains(
                        ev.rawX.toInt(),
                        ev.rawY.toInt()
                    )
                ) {

                    hideKeyboard(view)

                    view.clearFocus()
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}