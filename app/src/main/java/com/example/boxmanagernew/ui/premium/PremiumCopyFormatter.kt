package com.example.boxmanagernew.ui.premium

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy

object PremiumCopyFormatter {

    fun formatPitch(
        context: Context,
        pitch: ArchivioCompletoCopy.FeaturePitch,
        includeCallToAction: Boolean = true
    ): CharSequence {
        val sb =
            SpannableStringBuilder(pitch.lead)

        pitch.example?.let { example ->
            val prefix = context.getString(R.string.premium_example_prefix)
            val exampleStart = sb.length + prefix.length

            sb.append(prefix)
            sb.append(example)
            sb.setSpan(
                StyleSpan(Typeface.ITALIC),
                exampleStart,
                sb.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            sb.append(context.getString(R.string.premium_example_suffix))
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
