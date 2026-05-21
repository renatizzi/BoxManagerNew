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

    private const val SORT_ASC = "▲"
    private const val SORT_DESC = "▼"
    private const val SORT_LABEL = "ORDINA"
    private const val SORT_TEXT_SIZE = 18f
    private const val DATE_PATTERN = "dd/MM/yyyy HH:mm"

    fun updateSortButton(
        button: Button,
        isAscending: Boolean
    ) {

        val arrow =
            if (isAscending) SORT_ASC
            else SORT_DESC

        button.text =
            "$SORT_LABEL $arrow"

        button.textSize =
            SORT_TEXT_SIZE

        button.setTypeface(
            null,
            Typeface.BOLD
        )

        button.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                ThemeManager.getAccentDarkColor(
                    button.context
                )
            )
    }

    fun setupSearchAndSort(
        search: EditText,
        sortButton: Button,
        isAscending: Boolean,
        onSearchChanged: (String) -> Unit,
        onSortClicked: () -> Unit
    ) {

        search.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(
                    s: Editable?
                ) {

                    onSearchChanged(
                        s.toString()
                    )
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {}
            }
        )

        updateSortButton(
            sortButton,
            isAscending
        )

        sortButton.setOnClickListener {

            onSortClicked()

            updateSortButton(
                sortButton,
                !isAscending
            )
        }
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
                            .replace("\n"," ")

                    editText.setText(
                        cleaned
                    )

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
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}
        }
    }

    fun formatDate(
        ts: Long
    ): String {

        return SimpleDateFormat(
            DATE_PATTERN,
            Locale.getDefault()
        ).format(
            Date(ts)
        )
    }
}