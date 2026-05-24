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
import com.example.boxmanagernew.domain.model.Location
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.settings.LocationSpinnerAdapter

object DialogUtils {

    data class BoxDialogViews(
        val view: View,
        val errorText: TextView,
        val name: EditText,
        val spinner: Spinner,
        val position: Spinner,
        val date: TextView,
        val container: LinearLayout
    )

    data class ObjectDialogViews(
        val view: View,
        val errorText: TextView,
        val name: EditText,
        val description: EditText,
        val quantity: EditText
    )

    fun createBoxDialog(
        context: Context,
        categories: List<CategoryEntity>,
        timestamp: Long,
        box: Box? = null,
        locations: List<Location> = emptyList()
    ): BoxDialogViews {

        val view =
            LayoutInflater.from(context)
                .inflate(
                    R.layout.dialog_add_box,
                    null
                )

        val error =
            TextView(context).apply {
                text = "Dato obbligatorio"
                visibility = View.GONE
                setTextColor(
                    context.getColor(
                        android.R.color.holo_red_dark
                    )
                )
            }

        val name =
            view.findViewById<EditText>(
                R.id.editBoxName
            )

        val spinner =
            view.findViewById<Spinner>(
                R.id.spinnerCategory
            )

        val position =
            view.findViewById<Spinner>(
                R.id.spinnerLocation
            )

        val date =
            view.findViewById<TextView>(
                R.id.textLastModified
            )

        val container =
            view as LinearLayout

        container.addView(error, 0)

        name.addTextChangedListener(
            UiUtils.noEnterWatcher(
                name,
                error
            )
        )

        setupCategorySpinner(
            context,
            spinner,
            categories
        )

        position.adapter =
            LocationSpinnerAdapter(
                context,
                locations
            )

        if (locations.isNotEmpty()) {
            position.setSelection(0)
        }

        date.text =
            "Ultima modifica: ${
                UiUtils.formatDate(timestamp)
            }"

        if (box != null) {

            name.setText(box.name)

            val categoryIndex =
                categories.indexOfFirst {
                    it.id == box.categoryId
                }

            if (categoryIndex >= 0) {
                spinner.setSelection(categoryIndex)
            }

            val locationIndex =
                locations.indexOfFirst {
                    it.name == box.position
                }

            if (locationIndex >= 0) {
                position.setSelection(locationIndex)
            }
        }

        return BoxDialogViews(
            view,
            error,
            name,
            spinner,
            position,
            date,
            container
        )
    }

    fun createObjectDialog(
        context: Context,
        layout: Int,
        nameValue: String = "",
        descriptionValue: String? = null,
        quantityValue: Int? = null
    ): ObjectDialogViews {

        val view =
            LayoutInflater.from(context)
                .inflate(layout, null)

        val error =
            view.findViewById<TextView>(
                if (
                    layout ==
                    R.layout.dialog_edit_object
                )
                    R.id.textErrorEditObject
                else
                    R.id.textErrorObject
            )

        val name =
            view.findViewById<EditText>(
                R.id.editObjectName
            )

        val description =
            view.findViewById<EditText>(
                R.id.editObjectDescription
            )

        val quantity =
            view.findViewById<EditText>(
                R.id.editObjectQuantity
            )

        name.addTextChangedListener(
            UiUtils.noEnterWatcher(
                name,
                error
            )
        )

        description.addTextChangedListener(
            UiUtils.noEnterWatcher(
                description,
                null
            )
        )

        quantity.addTextChangedListener(
            UiUtils.noEnterWatcher(
                quantity,
                null
            )
        )

        name.setText(nameValue)
        description.setText(descriptionValue)
        quantity.setText(quantityValue?.toString() ?: "")

        return ObjectDialogViews(
            view,
            error,
            name,
            description,
            quantity
        )
    }

    fun createBoxConfirmDialog(
        context: Context,
        view: View
    ): AlertDialog {

        return AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Conferma", null)
            .setNegativeButton("Annulla", null)
            .create()
    }

    fun setupDialogConfirmButton(
        dialog: AlertDialog,
        onConfirm: () -> Unit
    ) {

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                onConfirm()
            }
        }
    }

    fun validateRequiredName(
        name: String,
        errorText: TextView
    ): Boolean {

        return if (
            name.trim().isEmpty()
        ) {

            FeedbackUtils.alert(
                errorText.context
            )

            errorText.visibility =
                View.VISIBLE

            false

        } else true
    }

    fun showMoveBoxesDialog(
        context: Context,
        onConfirm: (String) -> Unit
    ) {

        val input =
            EditText(context)

        input.inputType =
            InputType.TYPE_CLASS_TEXT

        input.addTextChangedListener(
            UiUtils.noEnterWatcher(
                input,
                null
            )
        )

        AlertDialog.Builder(context)
            .setTitle("Nuova posizione")
            .setView(input)
            .setPositiveButton("Conferma") { _, _ ->
                onConfirm(
                    input.text.toString().trim()
                )
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    fun showObjectsDeleteDialog(
        context: Context,
        onDelete: () -> Unit,
        onMoveObjects: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(
                "Confermi anche l'eliminazione degli oggetti contenuti?"
            )
            .setPositiveButton("SI") { _, _ ->
                onDelete()
            }
            .setNegativeButton("NO") { _, _ ->
                onMoveObjects()
            }
            .show()
    }

    fun showDeleteConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage("Conferma eliminazione?")
            .setPositiveButton("SI") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("NO", null)
            .show()
    }

    fun showMoveConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage("Conferma spostamento?")
            .setPositiveButton("SI") { _, _ ->
                onConfirm()
            }
            .setNegativeButton("NO", null)
            .show()
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
}