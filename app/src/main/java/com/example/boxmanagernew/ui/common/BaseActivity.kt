package com.example.boxmanagernew.ui.common

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.dashboard.DashboardActivity
import com.example.boxmanagernew.ui.settings.SettingsActivity
import com.example.boxmanagernew.ui.utility.UtilityActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var gestureDetector:
            GestureDetectorCompat

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        ThemeManager.initializeDefaults(this)

        setupSwipeNavigation()
    }

    override fun onResume() {

        super.onResume()

        refreshAppShell()

        refreshBottomNav()
    }

    private fun refreshBottomNav() {

        when (this) {

            is DashboardActivity ->
                BottomNavManager.setup(
                    this,
                    BottomNavManager.TAB_DASHBOARD
                )

            is MainActivity ->
                BottomNavManager.setup(
                    this,
                    BottomNavManager.TAB_BOXES
                )

            is CategoriesActivity ->
                BottomNavManager.setup(
                    this,
                    BottomNavManager.TAB_CATEGORIES
                )

            is UtilityActivity ->
                BottomNavManager.setup(
                    this,
                    BottomNavManager.TAB_UTILITY
                )

            is SettingsActivity ->
                BottomNavManager.setup(
                    this,
                    BottomNavManager.TAB_SETTINGS
                )
        }
    }

    private fun setupSwipeNavigation() {

        gestureDetector =
            GestureDetectorCompat(
                this,
                object :
                    GestureDetector.SimpleOnGestureListener() {

                    private val SWIPE_THRESHOLD =
                        180

                    private val SWIPE_VELOCITY =
                        180

                    override fun onFling(
                        e1: MotionEvent?,
                        e2: MotionEvent,
                        velocityX: Float,
                        velocityY: Float
                    ): Boolean {

                        if (e1 == null)
                            return false

                        val diffX =
                            e2.x - e1.x

                        val diffY =
                            e2.y - e1.y

                        if (
                            abs(diffX) > abs(diffY) &&
                            abs(diffX) > SWIPE_THRESHOLD &&
                            abs(velocityX) > SWIPE_VELOCITY
                        ) {

                            if (diffX > 0)
                                navigatePrevious()
                            else
                                navigateNext()

                            return true
                        }

                        return false
                    }
                }
            )
    }

    private fun navigateNext() {

        when (this) {

            is DashboardActivity ->
                openSwipe(MainActivity::class.java)

            is MainActivity ->
                openSwipe(CategoriesActivity::class.java)

            is CategoriesActivity ->
                openSwipe(UtilityActivity::class.java)

            is UtilityActivity ->
                openSwipe(SettingsActivity::class.java)

            is SettingsActivity ->
                return
        }
    }

    private fun navigatePrevious() {

        when (this) {

            is SettingsActivity ->
                openSwipe(UtilityActivity::class.java)

            is UtilityActivity ->
                openSwipe(CategoriesActivity::class.java)

            is CategoriesActivity ->
                openSwipe(MainActivity::class.java)

            is MainActivity ->
                openSwipe(DashboardActivity::class.java)

            is DashboardActivity ->
                return
        }
    }

    private fun openSwipe(
        target: Class<*>
    ) {

        if (
            this::class.java == target
        ) return

        startActivity(
            Intent(this, target).apply {

                flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        )

        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
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

            val ime =
                insets.getInsets(
                    WindowInsetsCompat.Type.ime()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                maxOf(
                    systemBars.bottom,
                    ime.bottom
                )
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
            ThemeManager.getTopBarBackground(this)
        )

        title?.setTextColor(
            ThemeManager.getTopBarTitle(this)
        )

        subtitle?.setTextColor(
            ThemeManager.getTopBarSubtitle(this)
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

        val accent =
            ThemeManager.getAccentDarkColor(this)

        findViewById<TextView>(
            R.id.textTitle
        )?.apply {

            text = title
            setTextColor(accent)
        }

        findViewById<TextView>(
            R.id.textSubtitle
        )?.apply {

            text = subtitle
            setTextColor(accent)
            alpha = 0.75f
        }
    }

    protected fun refreshAppShell() {

        refreshTopBar()

        val accent =
            ThemeManager.getAccentDarkColor(this)

        findViewById<TextView>(
            R.id.textTitle
        )?.setTextColor(accent)

        findViewById<TextView>(
            R.id.textSubtitle
        )?.apply {
            setTextColor(accent)
            alpha = 0.75f
        }

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
            ThemeManager.getAccentDarkColor(this)

        for (i in 0 until viewGroup.childCount) {

            val child =
                viewGroup.getChildAt(i)

            when (child) {

                is Button ->
                    child.backgroundTintList =
                        ColorStateList.valueOf(accent)

                is FloatingActionButton ->
                    child.backgroundTintList =
                        ColorStateList.valueOf(accent)
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

        gestureDetector.onTouchEvent(ev)

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