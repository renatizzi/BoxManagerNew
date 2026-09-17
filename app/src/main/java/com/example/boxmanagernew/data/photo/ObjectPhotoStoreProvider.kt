package com.example.boxmanagernew.data.photo

import android.content.Context
import com.example.boxmanagernew.data.local.AppDatabase

object ObjectPhotoStoreProvider {

    @Volatile
    private var instance: ObjectPhotoStore? = null

    fun get(context: Context): ObjectPhotoStore {
        return instance ?: synchronized(this) {
            instance ?: ObjectPhotoStore(
                appContext = context.applicationContext,
                photoDao = AppDatabase.getDatabase(context).objectPhotoDao()
            ).also { instance = it }
        }
    }
}
