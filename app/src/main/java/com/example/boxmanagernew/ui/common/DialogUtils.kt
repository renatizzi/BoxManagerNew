package com.example.boxmanagernew.ui.common

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtils {

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