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
import android.widget.CompoundButton
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
import com.example.boxmanagernew.ui.help.QuickStartGuideActivity
import com.example.boxmanagernew.ui.settings.SettingsActivity
import com.example.boxmanagernew.ui.utility.UtilityActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.math.abs

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var gestureDetector:
            GestureDetectorCompat

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        ThemeManager.applyStoredNightMode(this)

        super.onCreate(savedInstanceState)

        ThemeManager.initializeDefaults(this)

        setupSwipeNavigation()
    }

    override fun onResume() {

        super.onResume()

        refreshAppShell()
    }

    override fun onPause() {

        detachNightSwitch()

        super.onPause()
    }

    /**
     * Cornice comune: edge-to-edge + top bar globale.
     * La bottom nav si aggiorna in [refreshAppShell] via [BottomNavManager.tabFor].
     */
    protected fun setupAppShell() {
        setupEdgeToEdge()
        setupTopBar()
    }

    protected fun setupBottomNav() {
        refreshBottomNav()
    }

    private fun refreshBottomNav() {

        val tab =
            BottomNavManager.tabFor(this)
                ?: return

        BottomNavManager.setup(
            this,
            tab
        )
    }

    private fun setupSwipeNavigation() {

        gestureDetector =
            GestureDetectorCompat(
                this,
                object :
                    GestureDetector.SimpleOnGestureListener() {

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
                            abs(diffX) > 180 &&
                            abs(velocityX) > 180
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

        applySystemBarContrast()
    }

    private fun applySystemBarContrast() {

        val lightPage =
            !ThemeManager.isNightModeEnabled(this)

        val controller =
            WindowCompat.getInsetsController(
                window,
                window.decorView
            )

        // Dark OFF: pagina chiara dietro la status bar → icone scure.
        // Dark ON: pagina scura → icone chiare.
        controller.isAppearanceLightStatusBars =
            lightPage

        controller.isAppearanceLightNavigationBars =
            lightPage
    }

    protected fun setupTopBar() =
        refreshTopBar()

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

        bindNightSwitch()
        bindHelpButton()
    }

    private fun bindHelpButton() {

        val button =
            findViewById<TextView>(
                R.id.buttonHelp
            ) ?: return

        if (this is QuickStartGuideActivity) {
            button.visibility = View.GONE
            return
        }

        button.visibility = View.VISIBLE

        button.setTextColor(
            ThemeManager.getTopBarTitle(this)
        )

        button.setOnClickListener {

            if (isFinishing || isDestroyed) {
                return@setOnClickListener
            }

            startActivity(
                Intent(
                    this,
                    QuickStartGuideActivity::class.java
                )
            )
        }
    }

    private fun bindNightSwitch() {

        val switch =
            findViewById<SwitchMaterial>(
                R.id.switchTheme
            ) ?: return

        val isDark =
            ThemeManager.isNightModeEnabled(
                this
            )

        switch.setOnCheckedChangeListener(null)
        switch.setOnClickListener(null)
        switch.isChecked = isDark
        switch.jumpDrawablesToCurrentState()

        switch.setOnClickListener {

            if (
                isFinishing ||
                isDestroyed
            ) {
                return@setOnClickListener
            }

            val night =
                switch.isChecked

            if (
                night ==
                ThemeManager.isNightModeEnabled(
                    this
                )
            ) {
                return@setOnClickListener
            }

            ThemeManager.setNightMode(
                this,
                night
            )
        }
    }

    private fun detachNightSwitch() {

        findViewById<SwitchMaterial>(
            R.id.switchTheme
        )?.apply {

            setOnCheckedChangeListener(null)
            setOnClickListener(null)
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

        applySystemBarContrast()

        refreshBottomNav()

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

        applyRuntimeTheme(
            root,
            accent
        )
    }

    private fun applyRuntimeTheme(
        viewGroup: ViewGroup,
        accent: Int
    ) {

        for (i in 0 until viewGroup.childCount) {

            val child =
                viewGroup.getChildAt(i)

            when (child) {

                is CompoundButton -> {
                    // Switch Dark: non è un pulsante da tinteggiare.
                }

                is Button ->
                    child.backgroundTintList =
                        ColorStateList.valueOf(accent)

                is FloatingActionButton ->
                    child.backgroundTintList =
                        ColorStateList.valueOf(accent)

                is TextView -> {

                    val functionCardTexts =
                        listOf(
                            "📦 Contenitori",
                            "🏷 Categorie",
                            "☁ Backup",
                            "↺ Ripristino",
                            "▣ Codice QR",
                            "📥 Importa Dati",
                            "💾 Backup",
                            "🔄 Ripristino",
                            "📥 Importa dati",
                            "📱 Codice QR"
                        )

                    if (
                        functionCardTexts.contains(
                            child.text.toString()
                        )
                    ) {

                        child.setTextColor(
                            accent
                        )
                    }
                }
            }

            if (child is ViewGroup) {
                applyRuntimeTheme(
                    child,
                    accent
                )
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