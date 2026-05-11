package com.example.boxmanagernew.ui.common

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.R

abstract class BaseActivity : AppCompatActivity() {

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

        TopBarUtils.bindTitle(
            findViewById(R.id.textGlobalTitle)
        )

        TopBarUtils.bindSubtitle(
            this,
            findViewById(R.id.textGlobalSubtitle)
        )
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