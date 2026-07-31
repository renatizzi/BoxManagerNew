package com.example.boxmanagernew.backup.mapper

import com.example.boxmanagernew.backup.model.BackupApplicationInfo
import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.backup.model.BackupArchiveContent
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupMapper {

    fun buildArchive(
        boxes: List<BoxEntity>,
        objects: List<ObjectEntity>,
        categories: List<CategoryEntity>,
        locations: List<LocationEntity>,
        objectTypes: List<ObjectTypeEntity>
    ): BackupArchive {

        return BackupArchive(
            createdAt = createTimestamp(),
            application = BackupApplicationInfo(),
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes,
            archive = BackupArchiveContent(
                boxes = boxes,
                objects = objects,
                categories = categories,
                locations = locations,
                objectTypes = objectTypes
            )
        )
    }

    private fun createTimestamp(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
    }
}