package com.example.boxmanagernew.ui.common

import android.graphics.Typeface
import android.widget.Button

object UiUtils {

    fun updateSortButton(
        button: Button,
        isAscending: Boolean
    ) {

        val arrow =
            if (isAscending) {
                "▲"
            } else {
                "▼"
            }

        button.text =
            "ORDINA $arrow"

        button.textSize =
            18f

        button.setTypeface(
            null,
            Typeface.BOLD
        )
    }
}
