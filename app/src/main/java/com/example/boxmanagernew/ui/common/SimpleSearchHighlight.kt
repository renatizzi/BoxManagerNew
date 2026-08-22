package com.example.boxmanagernew.ui.common

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import com.example.boxmanagernew.util.SimpleSearch

object SimpleSearchHighlight {

    fun paint(
        text: String,
        query: String
    ): SpannableString {

        val result =
            SpannableString(text)

        SimpleSearch
            .highlightRanges(text, query)
            .forEach { range ->

                if (
                    range.first >= 0 &&
                    range.last < text.length
                ) {

                    result.setSpan(
                        BackgroundColorSpan(
                            Color.YELLOW
                        ),
                        range.first,
                        range.last + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

        return result
    }
}
