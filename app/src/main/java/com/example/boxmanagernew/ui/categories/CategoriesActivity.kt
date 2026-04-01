package com.example.boxmanagernew.ui.categories

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BottomNavManager

class CategoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // EDGE TO EDGE
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_categories)

        // INSETS
        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Bottom Nav
        BottomNavManager.setup(this, BottomNavManager.TAB_CATEGORIES)
    }
}