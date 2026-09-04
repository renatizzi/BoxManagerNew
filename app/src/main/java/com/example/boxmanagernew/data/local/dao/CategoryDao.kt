package com.example.boxmanagernew.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.boxmanagernew.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("""
        SELECT * FROM categories
        ORDER BY 
            CASE WHEN name = 'Miscellanea' OR name = 'Miscellaneous' THEN 1 ELSE 0 END,
            name ASC
    """)
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Query("""
        SELECT * FROM categories
        ORDER BY
            CASE WHEN name = 'Miscellanea' OR name = 'Miscellaneous' THEN 1 ELSE 0 END,
            name ASC
    """)
    suspend fun getAllSync(): List<CategoryEntity>

    @Query("""
        SELECT * FROM categories 
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun getById(id: Int): CategoryEntity?

    @Query("""
        SELECT * FROM categories 
        WHERE LOWER(name) = LOWER(:name) 
        LIMIT 1
    """)
    suspend fun getCategoryByName(name: String): CategoryEntity?

    @Query("DELETE FROM categories")
    suspend fun deleteAll()
}