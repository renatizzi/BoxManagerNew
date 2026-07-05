package com.example.boxmanagernew.backup.mapper

import com.example.boxmanagernew.backup.model.BackupArchive
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

/**
 * Mapper centrale del modulo Backup.
 *
 * Converte i dati provenienti dal database
 * nel modello BackupArchive.
 */
class BackupMapper {

    fun buildArchive(
        boxes: List<BoxEntity>,
        objects: List<ObjectEntity>,
        categories: List<CategoryEntity>,
        locations: List<LocationEntity>,
        objectTypes: List<ObjectTypeEntity>
    ): BackupArchive {

        return BackupArchive(
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes
        )
    }
}