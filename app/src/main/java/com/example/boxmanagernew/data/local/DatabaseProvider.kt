package com.example.boxmanagernew.data.local

import android.content.Context

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = AppDatabase.getDatabase(context)
            INSTANCE = instance
            instance
        }
    }
}