package com.example.boxmanagernew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.ui.boxdetail.BoxDetailActivity
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

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
        val objectTypeDao = db.objectTypeDao()
        val repository = BoxRepositoryImpl(dao)

        lifecycleScope.launch {

            val names = listOf(
                "Abbigliamento e Calzature",
                "Alimenti e Bevande",
                "Attrezzi, Strumenti e Ferramenta",
                "Bricolage e Materiali",
                "Cancelleria e Scuola",
                "Collezionismo",
                "Documenti e Archivi",
                "Elettronica e Informatica",
                "Fai da te",
                "Foto e Video",
                "Hobby",
                "Imballaggi e Contenitori",
                "Libri e Riviste",
                "Medicinali e Salute",
                "Oggetti di valore",
                "Miscellanea"
            )

            val icons = listOf(
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

            names.forEachIndexed { i, name ->
                val existing = categoryDao.getCategoryByName(name)
                if (existing == null) {
                    categoryDao.insert(
                        CategoryEntity(
                            name = name,
                            icon = icons[i]
                        )
                    )
                }
            }

            val objectTypes = listOf(
                "Generico",
                "Documento",
                "Accessorio",
                "Componente",
                "Ricambio",
                "Strumento",
                "Altro"
            )

            objectTypes.forEach { name ->
                val existing = objectTypeDao.getByName(name)
                if (existing == null) {
                    objectTypeDao.insert(
                        ObjectTypeEntity(
                            name = name
                        )
                    )
                }
            }
        }

        adapter = BoxAdapter(
            items = emptyList(),
            categories = emptyList(),
            onClick = { box ->
                val mode = viewModel.selectionMode.value ?: false
                if (mode) {
                    viewModel.toggleSelection(box)
                } else {
                    val intent = Intent(this, BoxDetailActivity::class.java)
                    intent.putExtra("boxId", box.id)
                    intent.putExtra("boxName", box.name)
                    startActivity(intent)
                }
            },
            onEdit = { box -> showEditDialog(box) },
            onDelete = { box -> showDeleteDialog(box.id) },
            onToggleSelection = { box -> viewModel.toggleSelection(box) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        categoryDao.getAllCategories().observe(this) { list ->
            categories = list
            adapter.updateCategories(list)
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(repository) as T
            }
        })[BoxViewModel::class.java]

        viewModel.boxes.observe(this) { list ->
            adapter.updateData(list)
        }

        viewModel.isAscending.observe(this) { asc ->
            UiUtils.updateSortButton(buttonSort, asc)
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

        viewModel.selectionMode.observe(this) { mode ->
            val selected = viewModel.selectedItems.value ?: emptySet()
            adapter.updateSelection(selected, mode)
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

        buttonSort.setOnClickListener {
            hideKeyboard(it)
            editSearch.clearFocus()
            viewModel.toggleSort()
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                viewModel.filter(query)
                adapter.updateQuery(query) // 🔴 FIX
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

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
                if (selected.isNotEmpty()) viewModel.clearSelection()
                else finish()
            }
        })
    }

    private fun showAddDialog() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply { hint = "Nome contenitore" }

        val spinner = Spinner(this)
        spinner.adapter = CategorySpinnerAdapter(this, categories)

        val positionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val icon = ImageView(this).apply {
            setImageResource(R.drawable.ic_place)
            layoutParams = LinearLayout.LayoutParams(60, 60)
        }

        val inputPosition = EditText(this).apply {
            hint = "Posizione (opzionale)"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        positionRow.addView(icon)
        positionRow.addView(inputPosition)

        layout.addView(inputName)
        layout.addView(spinner)
        layout.addView(positionRow)

        AlertDialog.Builder(this)
            .setTitle("Nuovo contenitore")
            .setView(layout)
            .setPositiveButton("Aggiungi") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isNotBlank() && categories.isNotEmpty()) {
                    val category = categories[spinner.selectedItemPosition]
                    viewModel.addBox(name, category.id, inputPosition.text.toString())
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun showEditDialog(box: com.example.boxmanagernew.domain.model.Box) {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nome contenitore"
            setText(box.name)
        }

        val spinner = Spinner(this)
        spinner.adapter = CategorySpinnerAdapter(this, categories)

        val index = categories.indexOfFirst { it.id == box.categoryId }
        if (index >= 0) spinner.setSelection(index)

        val positionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val icon = ImageView(this).apply {
            setImageResource(R.drawable.ic_place)
            layoutParams = LinearLayout.LayoutParams(60, 60)
        }

        val inputPosition = EditText(this).apply {
            hint = "Posizione (opzionale)"
            setText(box.position)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        positionRow.addView(icon)
        positionRow.addView(inputPosition)

        layout.addView(inputName)
        layout.addView(spinner)
        layout.addView(positionRow)

        AlertDialog.Builder(this)
            .setTitle("Modifica contenitore")
            .setView(layout)
            .setPositiveButton("Salva") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isNotBlank() && categories.isNotEmpty()) {
                    val category = categories[spinner.selectedItemPosition]
                    viewModel.updateBox(
                        id = box.id,
                        newName = name,
                        categoryId = category.id,
                        position = inputPosition.text.toString()
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

    private fun showDeleteDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Conferma eliminazione")
            .setMessage("Vuoi eliminare questo elemento?")
            .setPositiveButton("Sì") { _, _ -> viewModel.deleteBox(id) }
            .setNegativeButton("No", null)
            .show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            hideKeyboard(currentFocus!!)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}