package com.example.boxmanagernew.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: BoxDatabase? = null

    fun getDatabase(context: Context): BoxDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                BoxDatabase::class.java,
                "box_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}