package com.example.boxmanagernew.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.entity.BoxEntity

@Database(entities = [BoxEntity::class], version = 1)
abstract class BoxDatabase : RoomDatabase() {

    abstract fun boxDao(): BoxDao
}