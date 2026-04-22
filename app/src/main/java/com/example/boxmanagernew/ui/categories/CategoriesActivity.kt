package com.example.boxmanagernew.ui.categories

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.ui.common.BottomNavManager

class CategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_categories)

        BottomNavManager.setup(this, BottomNavManager.TAB_CATEGORIES)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCategories)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = CategoryRepositoryImpl(db.categoryDao(), db.boxDao())

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(repository) as T
            }
        })[CategoryViewModel::class.java]

        adapter = CategoryAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.categories.observe(this) {
            adapter.updateData(it)
        }

        viewModel.operationResult.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        // 🔹 TEMP: click su lista per inserimento (finché non hai bottone +)
        recyclerView.setOnLongClickListener {
            showAddDialog()
            true
        }
    }

    private fun showAddDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nome categoria"
        }

        val inputIcon = EditText(this).apply {
            hint = "Nome icona (es. ic_cat_food)"
        }

        layout.addView(inputName)
        layout.addView(inputIcon)

        AlertDialog.Builder(this)
            .setTitle("Nuova categoria")
            .setView(layout)
            .setPositiveButton("Aggiungi") { _, _ ->
                val name = inputName.text.toString().trim()
                val icon = inputIcon.text.toString().trim()

                if (name.isNotEmpty() && icon.isNotEmpty()) {
                    viewModel.insert(Category(0, name, icon))
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
}