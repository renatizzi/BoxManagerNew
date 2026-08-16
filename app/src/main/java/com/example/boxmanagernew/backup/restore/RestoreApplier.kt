package com.example.boxmanagernew.backup.restore

import androidx.room.withTransaction
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.data.local.AppDatabase

class RestoreApplier(
    private val database: AppDatabase
) {

    suspend fun replace(
        archive: BackupArchive
    ) {

        database.withTransaction {

            database.objectDao().deleteAll()
            database.boxDao().deleteAll()
            database.categoryDao().deleteAll()
            database.locationDao().deleteAll()
            database.objectTypeDao().deleteAll()

            for (category in archive.categories) {
                database.categoryDao().insert(category)
            }
            for (location in archive.locations) {
                database.locationDao().insert(location)
            }
            for (objectType in archive.objectTypes) {
                database.objectTypeDao().insert(objectType)
            }
            for (box in archive.boxes) {
                database.boxDao().insert(box)
            }
            for (obj in archive.objects) {
                database.objectDao().insert(obj)
            }
        }
    }
}
