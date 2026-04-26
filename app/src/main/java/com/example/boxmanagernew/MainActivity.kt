package com.example.boxmanagernew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BoxViewModel
    private lateinit var adapter: BoxAdapter
    private lateinit var buttonDeleteSelected: Button
    private lateinit var textSelectionCount: TextView
    private lateinit var selectionBar: View
    private lateinit var recyclerView: RecyclerView

    private lateinit var contextCard: MaterialCardView
    private lateinit var layoutSearchSort: View
    private lateinit var textContextMessage: TextView
    private lateinit var editSearch: EditText

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

        contextCard = findViewById(R.id.contextCard)
        layoutSearchSort = findViewById(R.id.layoutSearchSort)
        textContextMessage = findViewById(R.id.textContextMessage)

        editSearch = findViewById(R.id.editTextSearch)
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
                "Abbigliamento e Calzature","Alimenti e Bevande","Attrezzi, Strumenti e Ferramenta",
                "Bricolage e Materiali","Cancelleria e Scuola","Collezionismo","Documenti e Archivi",
                "Elettronica e Informatica","Fai da te","Foto e Video","Hobby",
                "Imballaggi e Contenitori","Libri e Riviste","Medicinali e Salute",
                "Oggetti di valore","Miscellanea"
            )

            val icons = listOf(
                "outline_checkroom_24","outline_fastfood_24","outline_handyman_24",
                "outline_carpenter_24","outline_ink_pen_24","outline_garage_money_24",
                "outline_passport_24","outline_broadcast_on_home_24","outline_tools_power_drill_24",
                "outline_photo_frame_24","outline_library_music_24","outline_box_24",
                "outline_menu_book_24","outline_medical_services_24","outline_money_bag_24",
                "outline_browse_24"
            )

            names.forEachIndexed { i, name ->
                if (categoryDao.getCategoryByName(name) == null) {
                    categoryDao.insert(CategoryEntity(name = name, icon = icons[i]))
                }
            }

            listOf("Generico","Documento","Accessorio","Componente","Ricambio","Strumento","Altro")
                .forEach {
                    if (objectTypeDao.getByName(it) == null) {
                        objectTypeDao.insert(ObjectTypeEntity(name = it))
                    }
                }
        }

        adapter = BoxAdapter(emptyList(), emptyList(),
            onClick = { box ->
                if (viewModel.selectionMode.value == true) viewModel.toggleSelection(box)
                else startActivity(Intent(this, BoxDetailActivity::class.java).apply {
                    putExtra("boxId", box.id)
                    putExtra("boxName", box.name)
                })
            },
            onEdit = { showEditDialog(it) },
            onDelete = { showDeleteDialog(it.id) },
            onToggleSelection = { viewModel.toggleSelection(it) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        categoryDao.getAllCategories().observe(this) {
            categories = it
            adapter.updateCategories(it)
            viewModel.setCategories(it)
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(repository) as T
            }
        })[BoxViewModel::class.java]

        viewModel.boxes.observe(this) { adapter.updateData(it) }

        viewModel.isAscending.observe(this) {
            UiUtils.updateSortButton(buttonSort, it)
        }

        viewModel.selectedItems.observe(this) {
            selectionBar.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            textSelectionCount.text =
                if (it.size == 1) "1 selezionato" else "${it.size} selezionati"
            adapter.updateSelection(it, viewModel.selectionMode.value ?: false)
        }

        viewModel.selectionMode.observe(this) {
            adapter.updateSelection(viewModel.selectedItems.value ?: emptySet(), it)
        }

        viewModel.hasHiddenSelections.observe(this) { hidden ->
            if (hidden) {
                showWarning("Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro.")
            } else {
                showDefaultBar()
            }
        }

        contextCard.setOnClickListener {
            val hasHidden = viewModel.hasHiddenSelections.value == true

            if (hasHidden) {

                // 🔴 FIX DEFINITIVO tastiera
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
                editSearch.clearFocus()

                editSearch.setText("")
                viewModel.filter("")
                adapter.updateQuery("")

                viewModel.clearSelection()

                showDefaultBar()
            }
        }

        buttonDeleteSelected.setOnClickListener {
            val ids = viewModel.selectedItems.value?.toList() ?: emptyList()
            if (ids.isEmpty()) return@setOnClickListener

            val hasHidden = viewModel.hasHiddenSelections.value == true

            if (hasHidden) {
                showWarning("Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro.")
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Conferma eliminazione")
                .setMessage("Eliminare ${ids.size} elementi?")
                .setPositiveButton("Sì") { _, _ -> viewModel.deleteBoxes(ids) }
                .setNegativeButton("No", null)
                .show()
        }

        buttonSort.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            editSearch.clearFocus()
            viewModel.toggleSort()
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString()
                viewModel.filter(q)
                adapter.updateQuery(q)
            }
            override fun beforeTextChanged(s: CharSequence?, s1: Int, s2: Int, s3: Int) {}
            override fun onTextChanged(s: CharSequence?, s1: Int, s2: Int, s3: Int) {}
        })

        fab.setOnClickListener { showAddDialog() }

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

        showDefaultBar()
    }

    private fun showDefaultBar() {
        layoutSearchSort.visibility = View.VISIBLE
        textContextMessage.visibility = View.GONE
        contextCard.strokeColor = getColor(android.R.color.transparent)
    }

    private fun showWarning(text: String) {
        layoutSearchSort.visibility = View.GONE
        textContextMessage.visibility = View.VISIBLE
        textContextMessage.text = text
        contextCard.strokeColor = getColor(android.R.color.holo_red_dark)
    }

    private fun showAddDialog() {}
    private fun showEditDialog(box: com.example.boxmanagernew.domain.model.Box) {}

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
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}