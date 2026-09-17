package com.example.boxmanagernew.data.trash

import android.content.Context
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.photo.ObjectPhotoStoreProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.family.deletion.FamilyDeleteProvider

object TrashStoreProvider {

    fun create(
        database: AppDatabase,
        boxRepository: BoxRepositoryImpl,
        objectRepository: ObjectRepositoryImpl,
        context: Context? = null
    ): TrashStore {
        return TrashStore(
            boxDao = database.boxDao(),
            objectDao = database.objectDao(),
            boxRepository = boxRepository,
            objectRepository = objectRepository,
            familyHardDelete = FamilyDeleteProvider.create(
                database,
                boxRepository,
                objectRepository
            ),
            photoStore = context?.let { ObjectPhotoStoreProvider.get(it) }
        )
    }
}
