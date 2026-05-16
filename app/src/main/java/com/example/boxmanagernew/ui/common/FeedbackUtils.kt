package com.example.boxmanagernew.ui.common

import android.content.Context
import android.os.Vibrator

object FeedbackUtils {

    fun alert(
        context: Context
    ) {

        try {

            val vibrator =
                context.getSystemService(
                    Context.VIBRATOR_SERVICE
                ) as? Vibrator
                    ?: return

            if (
                vibrator.hasVibrator()
            ) {

                @Suppress("DEPRECATION")
                vibrator.vibrate(80)
            }

        } catch (_: Exception) {

            return
        }
    }
}