package com.example.boxmanagernew.data.local

import android.content.Context
import com.example.boxmanagernew.domain.locale.DefaultArchiveLabels
import com.example.boxmanagernew.domain.locale.LocalePreference

/**
 * Riscrive categorie/posizioni di seed IT → EN solo se il nome
 * coincide ancora con il default ufficiale (B-DEFAULT-IT-EN).
 */
object DefaultArchiveLocaleSync {

    suspend fun applyEnglishDefaultsIfNeeded(
        context: Context
    ) {

        val prefs =
            context.getSharedPreferences(
                LocalePreference.PREFS,
                Context.MODE_PRIVATE
            )

        if (
            prefs.getBoolean(
                DefaultArchiveLabels.PREFS_FLAG,
                false
            )
        ) {
            return
        }

        val db =
            DatabaseProvider.getDatabase(
                context
            )

        val categoryDao =
            db.categoryDao()

        categoryDao.getAllSync().forEach { row ->

            val english =
                DefaultArchiveLabels.categoryItToEn[
                    row.name
                ]

            if (
                english != null &&
                english != row.name
            ) {
                categoryDao.update(
                    row.copy(
                        name = english
                    )
                )
            }
        }

        val locationDao =
            db.locationDao()

        locationDao.getAllLocationsSync().forEach { row ->

            val english =
                DefaultArchiveLabels.locationItToEn[
                    row.name
                ]

            if (
                english != null &&
                english != row.name
            ) {
                locationDao.update(
                    row.copy(
                        name = english
                    )
                )
            }
        }

        val boxDao =
            db.boxDao()

        boxDao.getAllSync().forEach { box ->

            val english =
                DefaultArchiveLabels.locationItToEn[
                    box.position
                ]

            if (
                english != null &&
                english != box.position
            ) {
                boxDao.update(
                    box.copy(
                        position = english
                    )
                )
            }
        }

        prefs.edit()
            .putBoolean(
                DefaultArchiveLabels.PREFS_FLAG,
                true
            )
            .commit()
    }
}
