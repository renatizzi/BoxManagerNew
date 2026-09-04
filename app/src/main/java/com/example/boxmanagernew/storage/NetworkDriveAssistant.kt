package com.example.boxmanagernew.storage

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.boxmanagernew.R

/**
 * Accompagna l’utente a collegare un disco di rete (cartella sul computer
 * o hard disk in casa) tramite l’app gratuita consigliata, così BoxManager
 * può sceglierla in Sfoglia. Non configura l’altra app al posto dell’utente.
 */
object NetworkDriveAssistant {

    /** Pacchetto Play Store dell’app gratuita consigliata (non mostrare all’utente). */
    const val HELPER_PACKAGE =
        "com.wa2c.android.cifsdocumentsprovider"

    const val HELPER_PLAY_URI =
        "market://details?id=$HELPER_PACKAGE"

    const val HELPER_PLAY_WEB =
        "https://play.google.com/store/apps/details?id=$HELPER_PACKAGE"

    fun isHelperInstalled(
        context: Context
    ): Boolean {

        return try {
            context.packageManager.getPackageInfo(
                HELPER_PACKAGE,
                0
            )
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun openHelperStore(
        context: Context
    ) {

        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(HELPER_PLAY_URI)
                ).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            )
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(HELPER_PLAY_WEB)
                ).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
            )
        }
    }

    fun openHelperApp(
        context: Context
    ): Boolean {

        val launch =
            context.packageManager
                .getLaunchIntentForPackage(
                    HELPER_PACKAGE
                ) ?: return false

        context.startActivity(
            launch.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
        )

        return true
    }

    fun showSetupDialog(
        activity: AppCompatActivity
    ) {

        val installed =
            isHelperInstalled(
                activity
            )

        val message =
            if (installed) {
                activity.getString(
                    R.string.network_drive_dialog_ready
                )
            } else {
                activity.getString(
                    R.string.network_drive_dialog_need_app
                )
            }

        val builder =
            AlertDialog.Builder(
                activity
            )
                .setTitle(
                    R.string.network_drive_dialog_title
                )
                .setMessage(
                    message
                )
                .setNegativeButton(
                    android.R.string.cancel,
                    null
                )

        if (installed) {

            builder.setPositiveButton(
                R.string.network_drive_open_helper
            ) { _, _ ->
                openHelperApp(
                    activity
                )
            }

            builder.setNeutralButton(
                R.string.network_drive_open_store
            ) { _, _ ->
                openHelperStore(
                    activity
                )
            }
        } else {

            builder.setPositiveButton(
                R.string.network_drive_install_helper
            ) { _, _ ->
                openHelperStore(
                    activity
                )
            }
        }

        builder.show()
    }
}
