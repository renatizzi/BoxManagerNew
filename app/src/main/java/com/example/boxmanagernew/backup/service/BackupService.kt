package com.example.boxmanagernew.backup.service

import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

/**
 * Servizio applicativo del modulo Backup.
 */
class BackupService(

    private val facade: BackupFacade =
        BackupFacade()
) {

    fun execute(
        boxes: List<BoxEntity>,
        objects: List<ObjectEntity>,
        categories: List<CategoryEntity>,
        locations: List<LocationEntity>,
        objectTypes: List<ObjectTypeEntity>
    ): String {

        return facade.export(
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes
        )
    }
}