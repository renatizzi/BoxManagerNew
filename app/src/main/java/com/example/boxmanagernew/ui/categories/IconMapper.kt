package com.example.boxmanagernew.ui.categories

import com.example.boxmanagernew.R

object IconMapper {

    fun getIconRes(iconName: String): Int {
        return try {
            val field = R.drawable::class.java.getField(iconName)
            field.getInt(null)
        } catch (e: Exception) {
            R.drawable.outline_browse_24
        }
    }
}