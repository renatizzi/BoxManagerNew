package com.example.boxmanagernew.ui.categories

import com.example.boxmanagernew.R

data class IconItem(
    val name: String,
    val resId: Int
)

object IconMapper {

    // 🔹 lista derivata dinamicamente SOLO dalle icone realmente usate
    fun getAvailableIcons(existingCategories: List<String>): List<IconItem> {
        return existingCategories.distinct().map { name ->
            IconItem(
                name = name,
                resId = getIconRes(name)
            )
        }
    }

    fun getIconRes(iconName: String): Int {
        return try {
            val field = R.drawable::class.java.getField(iconName)
            field.getInt(null)
        } catch (e: Exception) {
            R.drawable.outline_browse_24
        }
    }
}