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
import com.google.android.material.switchmaterial.SwitchMaterial

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
            ThemeManager.getAccentColor(
                this
            )

        val accentDark =
            ThemeManager.getAccentDarkColor(
                this
            )

        val white =
            0xFFFFFFFF.toInt()

        val disabled =
            0xFFBDBDBD.toInt()

        for (i in 0 until viewGroup.childCount) {

            val child =
                viewGroup.getChildAt(i)

            when (child) {

                is Button -> {

                    child.backgroundTintList =
                        ColorStateList.valueOf(
                            accentDark
                        )
                }

                is FloatingActionButton -> {

                    child.backgroundTintList =
                        ColorStateList.valueOf(
                            accentDark
                        )
                }

                is SwitchMaterial -> {

                    child.thumbTintList =
                        ColorStateList(
                            arrayOf(
                                intArrayOf(
                                    android.R.attr.state_checked
                                ),
                                intArrayOf()
                            ),
                            intArrayOf(
                                white,
                                white
                            )
                        )

                    child.trackTintList =
                        ColorStateList(
                            arrayOf(
                                intArrayOf(
                                    android.R.attr.state_checked
                                ),
                                intArrayOf()
                            ),
                            intArrayOf(
                                accentDark,
                                disabled
                            )
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