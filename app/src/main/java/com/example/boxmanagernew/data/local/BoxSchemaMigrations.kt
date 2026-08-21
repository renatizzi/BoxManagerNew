package com.example.boxmanagernew.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.boxmanagernew.domain.model.BoxPermanentId

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
}
