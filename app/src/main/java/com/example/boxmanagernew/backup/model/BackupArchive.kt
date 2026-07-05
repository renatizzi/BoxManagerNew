package com.example.boxmanagernew.backup.model

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

/**
 * Contenitore principale dell'intero archivio.
 *
 * Rappresenta il contenuto logico del file archive.json.
 */
data class BackupArchive(

    val boxes: List<BoxEntity> = emptyList(),

    val objects: List<ObjectEntity> = emptyList(),

    val categories: List<CategoryEntity> = emptyList(),

    val locations: List<LocationEntity> = emptyList(),

    val objectTypes: List<ObjectTypeEntity> = emptyList()
)