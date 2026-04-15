package com.example.boxmanagernew.domain.repository

import androidx.lifecycle.LiveData
import com.example.boxmanagernew.domain.model.Object

interface ObjectRepository {

    fun getObjectsByBox(boxId: Int): LiveData<List<Object>>

    suspend fun insert(obj: Object)

    suspend fun update(obj: Object)

    suspend fun delete(obj: Object)
}