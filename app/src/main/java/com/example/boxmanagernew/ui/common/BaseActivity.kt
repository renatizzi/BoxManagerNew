package com.example.boxmanagernew.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

            val bars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                bars.left,
                bars.top,
                bars.right,
                bars.bottom
            )

            insets
        }
    }

    protected fun setupTopBar() {

        refreshTopBar()
    }

    protected fun refreshTopBar() {

        val topBar =
            findViewById<MaterialCardView>(
                R.id.globalTopBar
            )

        val title =
            findViewById<TextView>(
                R.id.textGlobalTitle
            )

        val subtitle =
            findViewById<TextView>(
                R.id.textGlobalSubtitle
            )

        topBar?.setCardBackgroundColor(
            ThemeManager.getTopBarBackground(
                this
            )
        )

        title?.setTextColor(
            ThemeManager.getTopBarTitle(
                this
            )
        )

        subtitle?.setTextColor(
            ThemeManager.getTopBarSubtitle(
                this
            )
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
    }

    protected fun setupPageHeader(
        title: String,
        subtitle: String
    ) {

        findViewById<TextView>(
            R.id.textTitle
        )?.text = title

        findViewById<TextView>(
            R.id.textSubtitle
        )?.text = subtitle
    }

    protected fun refreshAppShell() {

        refreshTopBar()

        val root =
            findViewById<ViewGroup>(
                android.R.id.content
            )

        applyRuntimeTheme(root)
    }

    private fun applyRuntimeTheme(
        viewGroup: ViewGroup
    ) {

        val accent =
            ThemeManager.getAccentDarkColor(
                this
            )

        for (i in 0 until viewGroup.childCount) {

            val child =
                viewGroup.getChildAt(i)

            when (child) {

                is Button -> {

                    child.backgroundTintList =
                        ColorStateList.valueOf(
                            accent
                        )
                }

                is FloatingActionButton -> {

                    child.backgroundTintList =
                        ColorStateList.valueOf(
                            accent
                        )
                }
            }

            if (child is ViewGroup) {

                applyRuntimeTheme(child)
            }
        }
    }

    protected fun hideKeyboard(
        view: View?
    ) {

        if (view == null) return

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

                view.getGlobalVisibleRect(
                    rect
                )

                if (
                    !rect.contains(
                        ev.rawX.toInt(),
                        ev.rawY.toInt()
                    )
                ) {

                    hideKeyboard(
                        view
                    )

                    view.clearFocus()
                }
            }
        }

        return super.dispatchTouchEvent(
            ev
        )
    }
}