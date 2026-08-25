package com.example.boxmanagernew.ui.premium

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy

object PremiumCopyFormatter {

    fun formatPitch(
        pitch: ArchivioCompletoCopy.FeaturePitch,
        includeCallToAction: Boolean = true
    ): CharSequence {
        val sb =
            SpannableStringBuilder(pitch.lead)

        pitch.example?.let { example ->
            val exampleStart =
                sb.length + " (Esempio: ".length

            sb.append(" (Esempio: ")
            sb.append(example)
            sb.setSpan(
                StyleSpan(Typeface.ITALIC),
                exampleStart,
                sb.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            sb.append(").")
        }

        if (includeCallToAction) {
            pitch.callToAction?.let { cta ->
                if (sb.isNotEmpty() && !sb.endsWith(".")) {
                    sb.append(".")
                }
                sb.append(" ")
                sb.append(cta)
            }
        }

        return sb
    }
}
