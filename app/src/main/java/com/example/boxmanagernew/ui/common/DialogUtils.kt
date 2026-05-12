package com.example.boxmanagernew.ui.common

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter

object DialogUtils {

    data class BoxDialogViews(
        val view: View,
        val errorText: TextView,
        val name: EditText,
        val spinner: Spinner,
        val position: EditText,
        val date: TextView,
        val container: LinearLayout
    )

    fun inflateAddBoxDialog(
        context: Context
    ): View {

        return LayoutInflater.from(context)
            .inflate(
                R.layout.dialog_add_box,
                null
            )
    }

    fun createRequiredFieldErrorText(
        context: Context
    ): TextView {

        return TextView(context).apply {

            setTextColor(
                context.getColor(
                    android.R.color.holo_red_dark
                )
            )

            visibility =
                View.GONE

            text =
                "Dato obbligatorio"
        }
    }

    fun bindBoxDialogViews(
        context: Context,
        view: View
    ): BoxDialogViews {

        val errorText =
            createRequiredFieldErrorText(context)

        val name =
            view.findViewById<EditText>(
                R.id.editBoxName
            )

        val spinner =
            view.findViewById<Spinner>(
                R.id.spinnerCategory
            )

        val position =
            view.findViewById<EditText>(
                R.id.editPosition
            )

        val date =
            view.findViewById<TextView>(
                R.id.textLastModified
            )

        val container =
            view as LinearLayout

        container.addView(
            errorText,
            0
        )

        return BoxDialogViews(
            view = view,
            errorText = errorText,
            name = name,
            spinner = spinner,
            position = position,
            date = date,
            container = container
        )
    }

    fun setupBoxDialogInputs(
        name: EditText,
        position: EditText
    ) {

        name.inputType =
            InputType.TYPE_CLASS_TEXT

        position.inputType =
            InputType.TYPE_CLASS_TEXT
    }

    fun setupBoxDialogWatchers(
        name: EditText,
        position: EditText,
        errorText: TextView
    ) {

        name.addTextChangedListener(
            UiUtils.noEnterWatcher(
                name,
                errorText
            )
        )

        position.addTextChangedListener(
            UiUtils.noEnterWatcher(
                position,
                null
            )
        )
    }

    fun setupCategorySpinner(
        context: Context,
        spinner: Spinner,
        categories: List<CategoryEntity>
    ) {

        spinner.adapter =
            CategorySpinnerAdapter(
                context,
                categories
            )
    }

    fun setupCategorySelection(
        spinner: Spinner,
        categories: List<CategoryEntity>,
        categoryId: Int
    ) {

        val index =
            categories.indexOfFirst {

                it.id == categoryId
            }

        if (index >= 0) {

            spinner.setSelection(index)
        }
    }

    fun setupLastModifiedText(
        dateView: TextView,
        timestamp: Long
    ) {

        dateView.text =
            "Ultima modifica: ${
                UiUtils.formatDate(timestamp)
            }"
    }

    fun preloadEditBoxData(
        views: BoxDialogViews,
        box: Box,
        categories: List<CategoryEntity>
    ) {

        views.name.setText(
            box.name
        )

        views.position.setText(
            box.position
        )

        setupCategorySelection(
            views.spinner,
            categories,
            box.categoryId
        )

        setupLastModifiedText(
            views.date,
            box.lastModified
        )
    }

    fun validateRequiredName(
        name: String,
        errorText: TextView
    ): Boolean {

        return if (name.isEmpty()) {

            errorText.visibility =
                View.VISIBLE

            false

        } else {

            true
        }
    }

    fun showDeleteConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(
                "Conferma eliminazione?"
            )
            .setPositiveButton(
                "SI"
            ) { _, _ ->

                onConfirm()
            }
            .setNegativeButton(
                "NO",
                null
            )
            .show()
    }

    fun showDeleteWithObjectsConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(
                "Confermi anche l'eliminazione degli oggetti contenuti?"
            )
            .setPositiveButton(
                "SI"
            ) { _, _ ->

                onConfirm()
            }
            .setNegativeButton(
                "NO",
                null
            )
            .show()
    }

    fun showMoveConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(
                "Conferma spostamento?"
            )
            .setPositiveButton(
                "SI"
            ) { _, _ ->

                onConfirm()
            }
            .setNegativeButton(
                "NO",
                null
            )
            .show()
    }
}