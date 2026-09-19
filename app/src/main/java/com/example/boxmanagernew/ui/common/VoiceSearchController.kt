package com.example.boxmanagernew.ui.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Locale

/**
 * Microfono su EditText (drawable end).
 * Avvio solo con **pressione prolungata** sul microfono (B-VOICE-MIC-ACCIDENTAL):
 * uno sfioro / tap breve non parte la registrazione.
 */
class VoiceSearchController(
    private val activity: ComponentActivity
) {

    private var field: EditText? = null
    private var onSpoken: ((String) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var longPressArmed = false
    private var longPressFired = false

    private val launcher =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val target =
                field ?: return@registerForActivityResult

            if (result.resultCode == Activity.RESULT_OK) {

                val spoken =
                    result.data
                        ?.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                        )
                        ?.firstOrNull()
                        ?.trim()
                        .orEmpty()

                if (spoken.isNotEmpty()) {
                    target.setText(spoken)
                    onSpoken?.invoke(spoken)
                }
            }

            releaseKeyboard(target)
        }

    fun attach(
        target: EditText,
        onSpoken: ((String) -> Unit)? = null
    ) {

        field = target
        this.onSpoken = onSpoken

        val longPressTimeout =
            ViewConfiguration.getLongPressTimeout().toLong()

        target.setOnTouchListener { view, event ->

            val edit = view as EditText

            if (!hitEndDrawable(edit, event)) {
                cancelLongPress()
                return@setOnTouchListener false
            }

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                    field = edit
                    longPressArmed = true
                    longPressFired = false
                    handler.postDelayed({
                        if (longPressArmed && !longPressFired) {
                            longPressFired = true
                            field = edit
                            launch()
                        }
                    }, longPressTimeout)
                    true
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {
                    val wasLong = longPressFired
                    cancelLongPress()
                    // Tap breve sul mic: consuma il tocco, non avvia voice.
                    wasLong || true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (!hitEndDrawable(edit, event)) {
                        cancelLongPress()
                    }
                    true
                }

                else -> true
            }
        }
    }

    private fun cancelLongPress() {
        longPressArmed = false
        handler.removeCallbacksAndMessages(null)
    }

    private fun launch() {

        field?.let { releaseKeyboard(it) }

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault().toLanguageTag()
                )

                putExtra(
                    RecognizerIntent.EXTRA_MAX_RESULTS,
                    1
                )
            }

        try {
            launcher.launch(intent)
        } catch (_: ActivityNotFoundException) {
        }
    }

    private fun releaseKeyboard(
        target: EditText
    ) {

        target.clearFocus()

        val imm =
            activity.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        imm.hideSoftInputFromWindow(
            target.windowToken,
            0
        )

        target.post {

            target.clearFocus()

            imm.hideSoftInputFromWindow(
                target.windowToken,
                0
            )
        }
    }

    private fun hitEndDrawable(
        view: EditText,
        event: MotionEvent
    ): Boolean {

        val drawable =
            view.compoundDrawablesRelative[2]
                ?: return false

        val width =
            drawable.bounds.width()
        // Hit stretta (era +16dp): riduce gli sfiori involontari.
        val extra =
            (4 * view.resources.displayMetrics.density).toInt()

        return if (
            view.layoutDirection ==
                View.LAYOUT_DIRECTION_RTL
        ) {
            event.x <=
                (view.paddingStart + width + extra)
        } else {
            event.x >=
                (
                    view.width -
                        view.paddingEnd -
                        width -
                        extra
                    )
        }
    }
}
