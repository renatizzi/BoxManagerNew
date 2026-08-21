package com.example.boxmanagernew.domain.repository

import androidx.lifecycle.LiveData
import com.example.boxmanagernew.domain.model.Box

interface BoxRepository {

    fun getAllBoxesLive(): LiveData<List<Box>>

    suspend fun insertBox(box: Box): Long

    suspend fun updateBox(box: Box)

    suspend fun deleteBox(id: Int)

    suspend fun getBoxByPermanentId(permanentId: String): Box?

    suspend fun getBoxById(id: Int): Box?
}