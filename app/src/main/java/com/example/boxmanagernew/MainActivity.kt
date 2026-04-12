package com.example.boxmanagernew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BoxViewModel
    private lateinit var adapter: BoxAdapter
    private lateinit var buttonDeleteSelected: Button
    private lateinit var textSelectionCount: TextView
    private lateinit var selectionBar: View
    private lateinit var recyclerView: RecyclerView

    private var categories: List<CategoryEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editSearch = findViewById<EditText>(R.id.editTextSearch)
        val buttonSort = findViewById<Button>(R.id.buttonSort)
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerViewBoxes)

        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)
        textSelectionCount = findViewById(R.id.textSelectionCount)
        selectionBar = findViewById(R.id.selectionBar)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.boxDao()
        val categoryDao = db.categoryDao()
        val repository = BoxRepositoryImpl(dao)

        // 🔴 LOAD CATEGORIES
        categoryDao.getAllCategories().observe(this) {
            categories = it
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(repository) as T
            }
        })[BoxViewModel::class.java]

        adapter = BoxAdapter(
            items = emptyList(),
            onClick = { box -> showEditDialog(box.id, box.name) },
            onEdit = { box -> showEditDialog(box.id, box.name) },
            onDelete = { box -> showDeleteDialog(box.id) },
            onToggleSelection = { box -> viewModel.toggleSelection(box) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.boxes.observe(this) {
            adapter.updateData(it)
        }

        viewModel.selectedItems.observe(this) { selectedIds ->
            val count = selectedIds.size

            if (count > 0) {
                selectionBar.visibility = View.VISIBLE
                textSelectionCount.text =
                    if (count == 1) "1 selezionato" else "$count selezionati"
            } else {
                selectionBar.visibility = View.GONE
            }

            val mode = viewModel.selectionMode.value ?: false
            adapter.updateSelection(selectedIds, mode)
        }

        buttonDeleteSelected.setOnClickListener {
            val selectedIds = viewModel.selectedItems.value?.toList() ?: emptyList()
            if (selectedIds.isEmpty()) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Conferma eliminazione")
                .setMessage("Eliminare ${selectedIds.size} elementi?")
                .setPositiveButton("Sì") { _, _ ->
                    viewModel.deleteBoxes(selectedIds)
                }
                .setNegativeButton("No", null)
                .show()
        }

        viewModel.loadBoxes()

        buttonSort.setOnClickListener {
            hideKeyboard(it)
            editSearch.clearFocus()
            viewModel.toggleSort()
        }

        fab.setOnClickListener {
            showAddDialog()
        }

        BottomNavManager.setup(this, BottomNavManager.TAB_BOXES)

        findViewById<View>(R.id.navCategories).setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val selected = viewModel.selectedItems.value ?: emptySet()
                if (selected.isNotEmpty()) {
                    viewModel.clearSelection()
                } else {
                    finish()
                }
            }
        })
    }

    private fun showAddDialog() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nome contenitore"
        }

        val spinner = Spinner(this)

        val names = categories.map { it.name }
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
        spinner.adapter = adapterSpinner

        layout.addView(inputName)
        layout.addView(spinner)

        AlertDialog.Builder(this)
            .setTitle("Nuovo contenitore")
            .setView(layout)
            .setPositiveButton("Aggiungi") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isNotBlank() && categories.isNotEmpty()) {

                    val selectedIndex = spinner.selectedItemPosition
                    val categoryId = categories[selectedIndex].id

                    viewModel.addBox(
                        name = name,
                        categoryId = categoryId,
                        position = ""
                    )
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showEditDialog(id: Int, currentName: String) {
        val input = EditText(this)
        input.setText(currentName)

        AlertDialog.Builder(this)
            .setTitle("Modifica nome")
            .setView(input)
            .setPositiveButton("Salva") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    viewModel.updateBox(
                        id = id,
                        newName = newName,
                        categoryId = 1,
                        position = ""
                    )
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun showDeleteDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Conferma eliminazione")
            .setMessage("Vuoi eliminare questo elemento?")
            .setPositiveButton("Sì") { _, _ ->
                viewModel.deleteBox(id)
            }
            .setNegativeButton("No", null)
            .show()
    }
}