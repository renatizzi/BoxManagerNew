package com.example.boxmanagernew.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.boxmanagernew.domain.model.BoxPermanentId
import com.example.boxmanagernew.domain.model.ObjectPermanentId

object BoxSchemaMigrations {

    val MIGRATION_5_6 = object : Migration(5, 6) {

        override fun migrate(db: SupportSQLiteDatabase) {

            db.execSQL(
                "ALTER TABLE box ADD COLUMN permanentId TEXT NOT NULL DEFAULT ''"
            )

            val cursor =
                db.query("SELECT id FROM box")

            cursor.use { rows ->

                while (rows.moveToNext()) {

                    val id = rows.getInt(0)

                    db.execSQL(
                        "UPDATE box SET permanentId = ? WHERE id = ?",
                        arrayOf(BoxPermanentId.generate(), id)
                    )
                }
            }

            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_box_permanentId ON box(permanentId)"
            )
        }
    }

    val MIGRATION_6_7 = object : Migration(6, 7) {

        override fun migrate(db: SupportSQLiteDatabase) {
            val now = System.currentTimeMillis()

            db.execSQL(
                "ALTER TABLE objects ADD COLUMN objectPermanentId TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "ALTER TABLE objects ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0"
            )

            val cursor = db.query("SELECT id FROM objects")
            cursor.use { rows ->
                while (rows.moveToNext()) {
                    val id = rows.getInt(0)
                    db.execSQL(
                        "UPDATE objects SET objectPermanentId = ?, lastModified = ? WHERE id = ?",
                        arrayOf(ObjectPermanentId.generate(), now, id)
                    )
                }
            }

            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_objects_objectPermanentId " +
                    "ON objects(objectPermanentId)"
            )
        }
    }

    val MIGRATION_7_8 = object : Migration(7, 8) {

        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE box ADD COLUMN createdBy TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "ALTER TABLE objects ADD COLUMN createdBy TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS family_deletion_tombstone (
                    entityType TEXT NOT NULL,
                    permanentId TEXT NOT NULL,
                    deletedAt INTEGER NOT NULL,
                    deletedBy TEXT NOT NULL,
                    PRIMARY KEY(entityType, permanentId)
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS " +
                    "index_family_deletion_tombstone_entityType_permanentId " +
                    "ON family_deletion_tombstone(entityType, permanentId)"
            )
        }
    }

    val MIGRATION_8_9 = object : Migration(8, 9) {

        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE box ADD COLUMN deletedAt INTEGER DEFAULT NULL"
            )
            db.execSQL(
                "ALTER TABLE objects ADD COLUMN deletedAt INTEGER DEFAULT NULL"
            )
        }
    }

    /** A3 Foto oggetto — metadati leggeri; file sotto filesDir/object_photos. */
    val MIGRATION_9_10 = object : Migration(9, 10) {

        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS object_photos (
                    objectPermanentId TEXT NOT NULL PRIMARY KEY,
                    displayFileName TEXT NOT NULL,
                    thumbFileName TEXT NOT NULL,
                    updatedAt INTEGER NOT NULL,
                    byteSize INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }
}
