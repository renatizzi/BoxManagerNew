package com.example.boxmanagernew.family.deletion

import com.example.boxmanagernew.BuildConfig
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl

object FamilyDeleteProvider {

    fun create(
        database: AppDatabase,
        boxRepository: BoxRepositoryImpl,
        objectRepository: ObjectRepositoryImpl
    ): FamilyPropagatingDelete? {
        if (!BuildConfig.FAMILY_BETA) {
            return null
        }
        return FamilyPropagatingDelete(
            boxRepository = boxRepository,
            objectRepository = objectRepository,
            recorder = FamilyDeletionRecorder(
                database.familyDeletionTombstoneDao()
            )
        )
    }
}
