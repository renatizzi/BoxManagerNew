package com.example.boxmanagernew.domain.repository

import com.example.boxmanagernew.domain.model.Box

interface BoxRepository {

    suspend fun getAllBoxes(): List<Box>

    suspend fun getAllBoxesSortedAsc(): List<Box>

    suspend fun getAllBoxesSortedDesc(): List<Box>

    suspend fun insertBox(box: Box)

    suspend fun updateBox(box: Box)

    suspend fun deleteBox(id: Int)
}