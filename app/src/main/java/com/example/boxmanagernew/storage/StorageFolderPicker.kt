package com.example.boxmanagernew.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.boxmanagernew.R

/**
 * Selettore cartella Backup / Esporta / Condivisione.
 *
 * Usa il selettore cartelle Android con radici avanzate e, se presenti,
 * elenca prima i volumi e i dischi di rete / spazi online già collegati,
 * così l’utente può aprirli senza cercare nel menu del sistema.
 */
object StorageFolderPicker {

    fun createTreeIntent(
        initialUri: Uri? = null
    ): Intent {

        return Intent(
            Intent.ACTION_OPEN_DOCUMENT_TREE
        ).apply {

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                    Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
            )

            // Mostra radici aggiuntive (SD / USB / alcune reti OEM).
            // EXTRA_SHOW_ADVANCED non è API pubblica stabile: stringa documentata.
            putExtra(
                "android.provider.extra.SHOW_ADVANCED",
                true
            )
            putExtra(
                "android.content.extra.SHOW_ADVANCED",
                true
            )

            if (
                initialUri != null &&
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {

                putExtra(
                    DocumentsContract.EXTRA_INITIAL_URI,
                    initialUri
                )
            }
        }
    }

    /**
     * Se ci sono radici registrate (volumi / rete / cloud), mostra un elenco;
     * altrimenti apre direttamente il selettore di sistema (con advanced).
     */
    fun choose(
        activity: AppCompatActivity,
        launcher: ActivityResultLauncher<Uri?>
    ) {

        val roots =
            StorageFolderRoots.list(
                activity
            )

        if (roots.isEmpty()) {
            launcher.launch(null)
            return
        }

        val labels =
            Array(roots.size + 1) { index ->

                if (
                    index < roots.size
                ) {

                    val root =
                        roots[index]

                    if (
                        root.subtitle.isNullOrBlank()
                    ) {
                        root.title
                    } else {
                        "${root.title}\n${root.subtitle}"
                    }
                } else {
                    activity.getString(
                        R.string.storage_folder_browse_all
                    )
                }
            }

        AlertDialog.Builder(
            activity
        )
            .setTitle(
                R.string.storage_folder_pick_title
            )
            .setItems(
                labels
            ) { _, which ->

                if (
                    which >= roots.size
                ) {
                    launcher.launch(null)
                } else {
                    launcher.launch(
                        roots[which].initialUri
                    )
                }
            }
            .setNegativeButton(
                android.R.string.cancel,
                null
            )
            .show()
    }
}

/**
 * Contratto SAF allineato a [StorageFolderPicker.createTreeIntent]
 * (SHOW_ADVANCED + permessi persistenti).
 */
class OpenStorageTreeContract :
    ActivityResultContract<Uri?, Uri?>() {

    override fun createIntent(
        context: Context,
        input: Uri?
    ): Intent {

        return StorageFolderPicker.createTreeIntent(
            input
        )
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): Uri? {

        if (
            resultCode !=
            Activity.RESULT_OK
        ) {
            return null
        }

        return intent?.data
    }
}
