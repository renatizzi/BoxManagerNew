package com.example.boxmanagernew.data.trash

import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.family.deletion.FamilyDeleteProvider

object TrashStoreProvider {

    fun create(
        database: AppDatabase,
        boxRepository: BoxRepositoryImpl,
        objectRepository: ObjectRepositoryImpl
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
            )
        )
    }
}
