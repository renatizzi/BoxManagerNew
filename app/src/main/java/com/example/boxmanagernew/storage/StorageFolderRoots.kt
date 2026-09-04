package com.example.boxmanagernew.storage

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import com.example.boxmanagernew.R

/**
 * Radici selezionabili oltre al selettore generico: volumi montati
 * (USB / SD / eventuali reti OEM) e provider già registrati
 * (spazio online o disco di rete già reso visibile dal sistema).
 *
 * Non implementa il protocollo di rete: BoxManager riusa il selettore
 * cartelle Android sulle aree già visibili ad Android (B-SEL-CARTELLA).
 */
data class StorageFolderRoot(
    val title: String,
    val subtitle: String?,
    val initialUri: Uri?
)

object StorageFolderRoots {

    fun list(
        context: Context
    ): List<StorageFolderRoot> {

        val seen =
            linkedSetOf<String>()

        val out =
            mutableListOf<StorageFolderRoot>()

        fun add(
            title: String,
            subtitle: String?,
            uri: Uri?
        ) {

            val key =
                (uri?.toString() ?: title)
                    .lowercase()

            if (!seen.add(key)) {
                return
            }

            out.add(
                StorageFolderRoot(
                    title = title,
                    subtitle = subtitle,
                    initialUri = uri
                )
            )
        }

        addStorageVolumes(
            context,
            ::add
        )

        addDocumentProviderRoots(
            context,
            ::add
        )

        return out
    }

    private fun addStorageVolumes(
        context: Context,
        add: (String, String?, Uri?) -> Unit
    ) {

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.N
        ) {
            return
        }

        val manager =
            context.getSystemService(
                StorageManager::class.java
            ) ?: return

        manager.storageVolumes.forEach { volume ->

            val readable =
                android.os.Environment.MEDIA_MOUNTED ==
                    volume.state ||
                    android.os.Environment.MEDIA_MOUNTED_READ_ONLY ==
                    volume.state

            if (!readable) {
                return@forEach
            }

            val title =
                volume.getDescription(
                    context
                ).orEmpty()
                    .ifBlank {
                        context.getString(
                            R.string.storage_folder_volume_fallback
                        )
                    }

            val initialUri =
                openTreeInitialUri(
                    volume
                )

            val subtitle =
                when {

                    volume.isPrimary ->
                        context.getString(
                            R.string.storage_folder_volume_primary
                        )

                    volume.isRemovable ->
                        context.getString(
                            R.string.storage_folder_volume_removable
                        )

                    else ->
                        context.getString(
                            R.string.storage_folder_volume_shared
                        )
                }

            add(
                title,
                subtitle,
                initialUri
            )
        }
    }

    private fun openTreeInitialUri(
        volume: android.os.storage.StorageVolume
    ): Uri? {

        if (
            Build.VERSION.SDK_INT <
            Build.VERSION_CODES.Q
        ) {
            return null
        }

        return try {

            val intent =
                volume.createOpenDocumentTreeIntent()
                    ?: return null

            if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.TIRAMISU
            ) {
                intent.getParcelableExtra(
                    DocumentsContract.EXTRA_INITIAL_URI,
                    Uri::class.java
                )
            } else if (
                Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.O
            ) {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(
                    DocumentsContract.EXTRA_INITIAL_URI
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun addDocumentProviderRoots(
        context: Context,
        add: (String, String?, Uri?) -> Unit
    ) {

        val providers =
            context.packageManager
                .queryIntentContentProviders(
                    Intent(
                        DocumentsContract.PROVIDER_INTERFACE
                    ),
                    PackageManager.MATCH_DEFAULT_ONLY
                )

        providers.forEach { info ->

            val authority =
                info.providerInfo
                    ?.authority
                    ?.takeIf {
                        it.isNotBlank()
                    } ?: return@forEach

            val rootsUri =
                DocumentsContract.buildRootsUri(
                    authority
                )

            try {

                context.contentResolver.query(
                    rootsUri,
                    null,
                    null,
                    null,
                    null
                )?.use { cursor ->

                    val titleIdx =
                        cursor.getColumnIndex(
                            DocumentsContract.Root.COLUMN_TITLE
                        )

                    val docIdIdx =
                        cursor.getColumnIndex(
                            DocumentsContract.Root.COLUMN_DOCUMENT_ID
                        )

                    val flagsIdx =
                        cursor.getColumnIndex(
                            DocumentsContract.Root.COLUMN_FLAGS
                        )

                    val summaryIdx =
                        cursor.getColumnIndex(
                            DocumentsContract.Root.COLUMN_SUMMARY
                        )

                    while (
                        cursor.moveToNext()
                    ) {

                        val flags =
                            if (flagsIdx >= 0) {
                                cursor.getInt(
                                    flagsIdx
                                )
                            } else {
                                0
                            }

                        // Serve IS_CHILD per OPEN_DOCUMENT_TREE.
                        if (
                            flags and
                            DocumentsContract.Root.FLAG_SUPPORTS_IS_CHILD ==
                            0
                        ) {
                            continue
                        }

                        val title =
                            if (titleIdx >= 0) {
                                cursor.getString(
                                    titleIdx
                                )
                            } else {
                                null
                            }?.trim()
                                .orEmpty()
                                .ifBlank {
                                    authority
                                }

                        val documentId =
                            if (docIdIdx >= 0) {
                                cursor.getString(
                                    docIdIdx
                                )
                            } else {
                                null
                            }?.takeIf {
                                it.isNotBlank()
                            } ?: continue

                        val summary =
                            if (summaryIdx >= 0) {
                                cursor.getString(
                                    summaryIdx
                                )
                            } else {
                                null
                            }?.trim()
                                ?.takeIf {
                                    it.isNotEmpty()
                                }

                        val localOnly =
                            flags and
                                DocumentsContract.Root.FLAG_LOCAL_ONLY !=
                                0

                        val subtitle =
                            summary ?: if (
                                localOnly
                            ) {
                                null
                            } else {
                                context.getString(
                                    R.string.storage_folder_network_or_cloud
                                )
                            }

                        val initialUri =
                            DocumentsContract.buildDocumentUri(
                                authority,
                                documentId
                            )

                        add(
                            title,
                            subtitle,
                            initialUri
                        )
                    }
                }
            } catch (_: Exception) {
                // Provider non interrogabile: salta.
            }
        }
    }
}
