package com.example.boxmanagernew.ui.common

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object FeedbackUtils {

    private const val DURATION_MS = 1000L

    fun alert(
        context: Context
    ) {

        try {

            val vibrator =
                context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as? Vibrator

            vibrator?.let {

                if (it.hasVibrator()) {

                    if (
                        Build.VERSION.SDK_INT >=
                        Build.VERSION_CODES.O
                    ) {

                        it.vibrate(
                            VibrationEffect.createOneShot(
                                DURATION_MS,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )

                    } else {

                        @Suppress("DEPRECATION")
                        it.vibrate(
                            DURATION_MS
                        )
                    }
                }
            }
            val tone =
                ToneGenerator(
                    AudioManager.STREAM_NOTIFICATION,
                    100
                )

            tone.startTone(
                ToneGenerator.TONE_SUP_ERROR,
                1000
            )

        } catch (_: Exception) {

            return
        }
    }
}