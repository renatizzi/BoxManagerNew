package com.example.boxmanagernew.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.boxmanagernew.data.local.dao.BoxDao
import com.example.boxmanagernew.data.local.dao.CategoryDao
import com.example.boxmanagernew.data.local.entity.BoxEntity
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BoxEntity::class,
        CategoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun boxDao(): BoxDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "box_manager_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {

                    val categoryDao = database.categoryDao()

                    val categories = listOf(
                        CategoryEntity(name = "Abbigliamento e Calzature", icon = "outline_checkroom_24"),
                        CategoryEntity(name = "Alimenti e Bevande", icon = "outline_fastfood_24"),
                        CategoryEntity(name = "Attrezzi, Strumenti e Ferramenta", icon = "outline_handyman_24"),
                        CategoryEntity(name = "Bricolage e Materiali", icon = "outline_carpenter_24"),
                        CategoryEntity(name = "Cancelleria e Scuola", icon = "outline_ink_pen_24"),
                        CategoryEntity(name = "Collezionismo", icon = "outline_garage_money_24"),
                        CategoryEntity(name = "Documenti e Archivi", icon = "outline_passport_24"),
                        CategoryEntity(name = "Elettronica e Informatica", icon = "outline_broadcast_on_home_24"),
                        CategoryEntity(name = "Fai da te", icon = "outline_tools_power_drill_24"),
                        CategoryEntity(name = "Foto e Video", icon = "outline_photo_frame_24"),
                        CategoryEntity(name = "Hobby", icon = "outline_library_music_24"),
                        CategoryEntity(name = "Imballaggi e Contenitori", icon = "outline_box_24"),
                        CategoryEntity(name = "Libri e Riviste", icon = "outline_menu_book_24"),
                        CategoryEntity(name = "Medicinali e Salute", icon = "outline_medical_services_24"),
                        CategoryEntity(name = "Oggetti di valore", icon = "outline_money_bag_24"),
                        CategoryEntity(name = "Miscellanea", icon = "outline_browse_24")
                    )

                    categories.forEach {
                        categoryDao.insert(it)
                    }
                }
            }
        }
    }
}