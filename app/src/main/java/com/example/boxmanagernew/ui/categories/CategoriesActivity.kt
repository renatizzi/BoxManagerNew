package com.example.boxmanagernew.ui.categories

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    private val iconNames = listOf(
        "outline_checkroom_24",
        "outline_fastfood_24",
        "outline_handyman_24",
        "outline_carpenter_24",
        "outline_ink_pen_24",
        "outline_garage_money_24",
        "outline_passport_24",
        "outline_broadcast_on_home_24",
        "outline_tools_power_drill_24",
        "outline_photo_frame_24",
        "outline_library_music_24",
        "outline_box_24",
        "outline_menu_book_24",
        "outline_medical_services_24",
        "outline_money_bag_24",
        "outline_browse_24"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_categories)

        BottomNavManager.setup(this, BottomNavManager.TAB_CATEGORIES)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.clearSelection()
                hideKeyboard()
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
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddCategory)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = CategoryRepositoryImpl(db.categoryDao(), db.boxDao())

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(repository) as T
            }
        })[CategoryViewModel::class.java]

        adapter = CategoryAdapter(
            emptyList(),
            onUpdate = { updated ->
                lifecycleScope.launch {
                    viewModel.update(updated)
                }
            },
            onDeleteRequest = { category ->
                lifecycleScope.launch {
                    val isUsed = viewModel.isCategoryUsed(category.id)

                    if (isUsed) {
                        Toast.makeText(
                            this@CategoriesActivity,
                            "Categoria in uso: eliminazione non consentita",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.selectCategory(category.id)
                        showDeleteDialog(category)
                    }
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.categories.observe(this) {
            adapter.updateData(it)
        }

        viewModel.selectedCategory.observe(this) { id ->
            adapter.updateSelection(id)
        }

        viewModel.operationResult.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        fabAdd.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_category, null)

        val editName = view.findViewById<EditText>(R.id.editCategoryName)
        val recyclerIcons = view.findViewById<RecyclerView>(R.id.recyclerIcons)
        val textError = view.findViewById<TextView>(R.id.textError)

        val iconAdapter = IconAdapter(iconNames) {}
        recyclerIcons.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerIcons.adapter = iconAdapter

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nuova categoria")
            .setView(view)
            .setNegativeButton("Annulla", null)
            .setPositiveButton("Aggiungi", null)
            .create()

        dialog.setOnShowListener {

            val btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            btnAdd.setOnClickListener {

                val name = editName.text.toString().trim()
                val selectedIcon = iconAdapter.getSelectedIcon()

                textError.visibility = View.GONE

                if (name.isEmpty()) {
                    textError.text = "Nome obbligatorio"
                    textError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                if (selectedIcon == null) {
                    textError.text = "Seleziona un'icona"
                    textError.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    val success = viewModel.insert(
                        Category(
                            name = name,
                            icon = selectedIcon
                        )
                    )

                    if (success) {
                        hideKeyboard()
                        dialog.dismiss()
                    } else {
                        textError.text = "Categoria già esistente"
                        textError.visibility = View.VISIBLE
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(category: Category) {

        AlertDialog.Builder(this)
            .setTitle("Elimina categoria")
            .setMessage("Sei sicuro di voler eliminare \"${category.name}\"?")
            .setNegativeButton("Annulla") { _, _ ->
                viewModel.clearSelection()
            }
            .setPositiveButton("Elimina") { _, _ ->
                lifecycleScope.launch {
                    viewModel.delete(category)
                    viewModel.clearSelection()
                }
            }
            .setOnCancelListener {
                viewModel.clearSelection()
            }
            .show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            hideKeyboard()
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}