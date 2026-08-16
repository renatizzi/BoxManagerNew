package com.example.boxmanagernew.backup.coordinator

import com.example.boxmanagernew.backup.export.BackupExporter
import com.example.boxmanagernew.backup.mapper.BackupMapper
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.LocationEntity
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity

class BackupCoordinator(

    private val mapper: BackupMapper = BackupMapper(),

    private val exporter: BackupExporter = BackupExporter()

) {

    fun exportArchive(
        boxes: List<BoxEntity>,
        objects: List<ObjectEntity>,
        categories: List<CategoryEntity>,
        locations: List<LocationEntity>,
        objectTypes: List<ObjectTypeEntity>
    ): String {

        val archive = mapper.buildArchive(
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes
        )

        return exporter.export(archive)
    }

    fun exportPayload(
        boxes: List<BoxEntity>,
        objects: List<ObjectEntity>,
        categories: List<CategoryEntity>,
        locations: List<LocationEntity>,
        objectTypes: List<ObjectTypeEntity>,
        applicationVersion: String
    ): Map<String, ByteArray> {

        val archive = mapper.buildArchive(
            boxes = boxes,
            objects = objects,
            categories = categories,
            locations = locations,
            objectTypes = objectTypes
        )

        return exporter.exportPayload(
            archive = archive,
            applicationVersion = applicationVersion
        )
    }
}