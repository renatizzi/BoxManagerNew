package com.example.boxmanagernew.backup.model

import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

data class BackupArchive(

    val formatVersion: Int = CURRENT_FORMAT_VERSION,

    val createdAt: String = "",

    val application: BackupApplicationInfo = BackupApplicationInfo(),

    val boxes: List<BoxEntity> = emptyList(),

    val objects: List<ObjectEntity> = emptyList(),

    val categories: List<CategoryEntity> = emptyList(),

    val locations: List<LocationEntity> = emptyList(),

    val objectTypes: List<ObjectTypeEntity> = emptyList(),

    val archive: BackupArchiveContent = BackupArchiveContent(
        boxes = boxes,
        objects = objects,
        categories = categories,
        locations = locations,
        objectTypes = objectTypes
    )
) {

    companion object {
        const val CURRENT_FORMAT_VERSION = 1
    }
}

data class BackupApplicationInfo(

    val name: String = "BoxManagerNew",

    val backupType: String = "FULL"

)

data class BackupArchiveContent(

    val boxes: List<BoxEntity> = emptyList(),

    val objects: List<ObjectEntity> = emptyList(),

    val categories: List<CategoryEntity> = emptyList(),

    val locations: List<LocationEntity> = emptyList(),

    val objectTypes: List<ObjectTypeEntity> = emptyList()

) {

    fun isEmpty(): Boolean {

        return boxes.isEmpty() &&
                objects.isEmpty() &&
                categories.isEmpty() &&
                locations.isEmpty() &&
                objectTypes.isEmpty()
    }

    fun totalElements(): Int {

        return boxes.size +
                objects.size +
                categories.size +
                locations.size +
                objectTypes.size
    }
}