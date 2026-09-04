package com.example.boxmanagernew.data.local

import android.content.Context
import com.example.boxmanagernew.domain.locale.DefaultArchiveLabels

/**
 * Riscrive categorie/posizioni di seed IT ↔ EN solo se il nome
 * coincide ancora con il default ufficiale (B-DEFAULT-IT-EN).
 * Idempotente: può essere richiamato dopo uno switch interrotto.
 */
object DefaultArchiveLocaleSync {

    suspend fun applyEnglishDefaultsIfNeeded(
        context: Context
    ) {
        rewriteSeeds(
            context,
            DefaultArchiveLabels.categoryItToEn,
            DefaultArchiveLabels.locationItToEn
        )
    }

    suspend fun applyItalianDefaultsIfNeeded(
        context: Context
    ) {
        rewriteSeeds(
            context,
            DefaultArchiveLabels.categoryItToEn
                .entries
                .associate { (it, en) ->
                    en to it
                },
            DefaultArchiveLabels.locationItToEn
                .entries
                .associate { (it, en) ->
                    en to it
                }
        )
    }

    private suspend fun rewriteSeeds(
        context: Context,
        categoryMap: Map<String, String>,
        locationMap: Map<String, String>
    ) {

        val db =
            DatabaseProvider.getDatabase(
                context
            )

        val categoryDao =
            db.categoryDao()

        categoryDao.getAllSync().forEach { row ->

            val target =
                categoryMap[row.name]

            if (
                target != null &&
                target != row.name
            ) {
                categoryDao.update(
                    row.copy(
                        name = target
                    )
                )
            }
        }

        val locationDao =
            db.locationDao()

        locationDao.getAllLocationsSync().forEach { row ->

            val target =
                locationMap[row.name]

            if (
                target != null &&
                target != row.name
            ) {
                locationDao.update(
                    row.copy(
                        name = target
                    )
                )
            }
        }

        val boxDao =
            db.boxDao()

        boxDao.getAllSync().forEach { box ->

            val target =
                locationMap[box.position]

            if (
                target != null &&
                target != box.position
            ) {
                boxDao.update(
                    box.copy(
                        position = target
                    )
                )
            }
        }
    }
}
