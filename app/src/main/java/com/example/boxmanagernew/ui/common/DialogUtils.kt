package com.example.boxmanagernew.ui.common

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.settings.LocationSpinnerAdapter
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration

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
                text = context.getString(R.string.common_required_field)
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
            context.getString(
                R.string.dialog_last_modified_prefix,
                UiUtils.formatDate(timestamp)
            )

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
            .setPositiveButton(context.getString(R.string.common_confirm), null)
            .setNegativeButton(context.getString(R.string.common_cancel), null)
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
            .setTitle(context.getString(R.string.dialog_new_location))
            .setView(input)
            .setPositiveButton(context.getString(R.string.common_confirm)) { _, _ ->
                onConfirm(
                    input.text.toString().trim()
                )
            }
            .setNegativeButton(context.getString(R.string.common_cancel), null)
            .show()
    }

    fun showObjectsDeleteDialog(
        context: Context,
        onDelete: () -> Unit,
        onMoveObjects: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(
                context.getString(R.string.dialog_delete_contained_objects)
            )
            .setPositiveButton(context.getString(R.string.common_yes)) { _, _ ->
                onDelete()
            }
            .setNegativeButton(context.getString(R.string.common_no)) { _, _ ->
                onMoveObjects()
            }
            .show()
    }

    fun showDeleteConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.dialog_delete_confirm))
            .setPositiveButton(context.getString(R.string.common_yes)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.common_no), null)
            .show()
    }

    fun showBoxQrDeleteConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.qr_delete_confirm))
            .setPositiveButton(context.getString(R.string.common_yes)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.common_no), null)
            .show()
    }

    fun showCameraPermissionRationale(
        context: Context,
        onContinue: () -> Unit,
        onCancel: () -> Unit
    ): AlertDialog {

        return AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.privacy_camera_rationale))
            .setPositiveButton(
                context.getString(R.string.privacy_camera_continue)
            ) { _, _ ->
                onContinue()
            }
            .setNegativeButton(
                context.getString(R.string.privacy_camera_cancel)
            ) { _, _ ->
                onCancel()
            }
            .setCancelable(false)
            .show()
    }

    fun showReplaceBackupConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        showReplaceBackupConfirmation(
            context,
            onConfirm,
            null
        )
    }

    fun showReplaceBackupConfirmation(
        context: Context,
        onConfirm: () -> Unit,
        onDecline: (() -> Unit)?
    ) {

        AlertDialog.Builder(context)
            .setMessage(BackupConfiguration.fileExists(context))
            .setPositiveButton(context.getString(R.string.common_yes)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.common_no)) { _, _ ->
                onDecline?.invoke()
            }
            .show()
    }

    fun showExportFileName(
        context: Context,
        defaultName: String,
        exists: (String) -> Boolean,
        onSave: (fileName: String, overwrite: Boolean) -> Unit,
        onBrowseFolder: (() -> Unit)? = null,
        normalizeName: (String) -> String = { ViewOutputConfiguration.csvFileName(it) },
        title: String? = null,
        folderName: String? = null
    ) {

        val pad =
            (16 * context.resources.displayMetrics.density).toInt()

        val name =
            EditText(context).apply {
                setText(defaultName)
                setSelection(defaultName.length)
                inputType = InputType.TYPE_CLASS_TEXT
            }

        val prompt =
            TextView(context).apply {
                setPadding(0, pad, 0, 0)
            }

        val column =
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(pad, pad, pad, pad)
                if (!folderName.isNullOrBlank()) {
                    addView(
                        TextView(context).apply {
                            text = folderName
                            setPadding(0, 0, 0, pad)
                        }
                    )
                }
                addView(name)
                addView(prompt)
            }

        fun typedFileName(): String {
            return normalizeName(name.text.toString())
        }

        fun refreshPrompt() {
            prompt.text =
                ViewOutputConfiguration.exportFilePrompt(
                    context,
                    exists(typedFileName())
                )
        }

        refreshPrompt()

        name.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    refreshPrompt()
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
        )

        val builder =
            AlertDialog.Builder(context)
                .setView(column)
                .setPositiveButton(context.getString(R.string.common_yes), null)
                .setNegativeButton(context.getString(R.string.common_no), null)

        if (!title.isNullOrBlank()) {
            builder.setTitle(title)
        }

        if (onBrowseFolder != null) {
            builder.setNeutralButton(context.getString(R.string.common_folder), null)
        }

        val dialog = builder.create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val fileName = typedFileName()
                onSave(
                    fileName,
                    exists(fileName)
                )
                dialog.dismiss()
            }

            dialog.getButton(
                AlertDialog.BUTTON_NEGATIVE
            ).setOnClickListener {
                dialog.dismiss()
            }

            if (onBrowseFolder != null) {
                dialog.getButton(
                    AlertDialog.BUTTON_NEUTRAL
                ).setOnClickListener {
                    dialog.dismiss()
                    onBrowseFolder.invoke()
                }
            }
        }

        dialog.show()
    }

    fun showRestoreConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(BackupConfiguration.restoreConfirm(context))
            .setPositiveButton(context.getString(R.string.common_yes)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.common_no), null)
            .show()
    }

    fun showMoveConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {

        AlertDialog.Builder(context)
            .setMessage(context.getString(R.string.dialog_move_confirm))
            .setPositiveButton(context.getString(R.string.common_yes)) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(context.getString(R.string.common_no), null)
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