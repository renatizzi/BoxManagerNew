package com.example.boxmanagernew.ui.common

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    fun showContextMessage(
        contextCard: View,
        messageView: TextView,
        message: String
    ) {

        contextCard.visibility =
            View.VISIBLE

        messageView.text =
            message
    }

    fun hideContextMessage(
        contextCard: View
    ) {

        contextCard.visibility =
            View.GONE
    }

    fun noEnterWatcher(
        editText: EditText,
        error: TextView?
    ): TextWatcher {

        return object : TextWatcher {

            override fun afterTextChanged(
                s: Editable?
            ) {

                if (
                    s != null &&
                    s.contains("\n")
                ) {

                    val cleaned =
                        s.toString()
                            .replace(
                                "\n",
                                " "
                            )

                    editText.setText(cleaned)

                    editText.setSelection(
                        cleaned.length
                    )
                }

                error?.visibility =
                    View.GONE
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        }
    }

    fun formatDate(
        ts: Long
    ): String {

        return SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(
            Date(ts)
        )
    }
}