package com.example.boxmanagernew.backup.facade

import com.example.boxmanagernew.backup.coordinator.BackupCoordinator
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

/**
 * Punto di ingresso pubblico del modulo Backup.
 *
 * Espone un'unica API verso il resto
 * dell'applicazione.
 */
class BackupFacade(

    private val coordinator: BackupCoordinator =
        BackupCoordinator()
) {

    fun export(
        boxes: List<BoxEntity>,
        objects: List<ObjectEntity>,
        categories: List<CategoryEntity>,
        locations: List<LocationEntity>,
        objectTypes: List<ObjectTypeEntity>
    ): String {

        return coordinator.exportArchive(
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes
        )
    }
}