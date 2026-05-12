package com.example.boxmanagernew.ui.common

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

object DialogUtils {

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