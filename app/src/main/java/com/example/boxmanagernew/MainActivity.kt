package com.example.boxmanagernew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
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
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.ui.boxdetail.BoxDetailActivity
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BoxViewModel
    private lateinit var adapter: BoxAdapter

    private lateinit var buttonDeleteSelected: Button
    private lateinit var textSelectionCount: TextView
    private lateinit var selectionBar: View

    private lateinit var contextCard: View
    private lateinit var textContextMessage: TextView
    private lateinit var editSearch: EditText

    private var categories: List<CategoryEntity> = emptyList()

    private var isBlockingDelete: Boolean = false

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
        textContextMessage = findViewById(R.id.textContextMessage)
        editSearch = findViewById(R.id.editTextSearch)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBoxes)
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)
        textSelectionCount = findViewById(R.id.textSelectionCount)
        selectionBar = findViewById(R.id.selectionBar)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = BoxRepositoryImpl(db.boxDao())

        lifecycleScope.launch {
            if (db.categoryDao().getCategoryByName("Generico") == null) {
                db.categoryDao().insert(CategoryEntity(name = "Generico", icon = "outline_box_24"))
            }
            if (db.objectTypeDao().getByName("Generico") == null) {
                db.objectTypeDao().insert(ObjectTypeEntity(name = "Generico"))
            }
        }

        adapter = BoxAdapter(
            emptyList(),
            emptyList(),
            onClick = {
                if (viewModel.selectionMode.value == true) viewModel.toggleSelection(it)
                else startActivity(Intent(this, BoxDetailActivity::class.java).apply {
                    putExtra("boxId", it.id)
                    putExtra("boxName", it.name)
                })
            },
            onEdit = { showEditDialog(it) },
            onDelete = { showDeleteDialog(it.id) },
            onToggleSelection = { viewModel.toggleSelection(it) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(repository) as T
            }
        })[BoxViewModel::class.java]

        db.categoryDao().getAllCategories().observe(this) {
            categories = it
            adapter.updateCategories(it)
            viewModel.setCategories(it)
        }

        viewModel.boxes.observe(this) {
            adapter.updateData(it)
        }

        viewModel.selectedItems.observe(this) {
            selectionBar.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            textSelectionCount.text = "${it.size} selezionati"
            adapter.updateSelection(it, viewModel.selectionMode.value ?: false)
        }

        viewModel.hasHiddenSelections.observe(this) { hidden ->

            if (hidden) {
                contextCard.visibility = View.VISIBLE

                textContextMessage.text = if (isBlockingDelete) {
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
                } else {
                    "Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
                }
            } else {
                contextCard.visibility = View.GONE
                isBlockingDelete = false
            }
        }

        contextCard.setOnClickListener {
            editSearch.setText("")
            editSearch.clearFocus()
            viewModel.filter("")
            adapter.updateQuery("")
            viewModel.clearSelection()   // 🔴 FIX
            isBlockingDelete = false
            hideKeyboard()
        }

        buttonDeleteSelected.setOnClickListener {
            val ids = viewModel.selectedItems.value?.toList() ?: return@setOnClickListener

            if (viewModel.hasHiddenSelections.value == true) {
                // 🔴 SOLO CONTEXT CARD
                isBlockingDelete = true
                contextCard.visibility = View.VISIBLE
                textContextMessage.text =
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setMessage("Conferma eliminazione?")
                .setPositiveButton("SI") { _, _ -> viewModel.deleteBoxes(ids) }
                .setNegativeButton("NO", null)
                .show()
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                viewModel.filter(query)
                adapter.updateQuery(query)
                isBlockingDelete = false
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
                if ((viewModel.selectedItems.value ?: emptySet()).isNotEmpty()) {
                    viewModel.clearSelection()
                } else finish()
            }
        })
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    private fun showAddDialog() { /* invariato */ }
    private fun showEditDialog(box: Box) { /* invariato */ }

    private fun showDeleteDialog(id: Int) {
        AlertDialog.Builder(this)
            .setMessage("Conferma eliminazione?")
            .setPositiveButton("SI") { _, _ -> viewModel.deleteBox(id) }
            .setNegativeButton("NO", null)
            .show()
    }

    private fun noEnterWatcher(editText: EditText, error: TextView?): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.contains("\n")) {
                    val cleaned = s.toString().replace("\n", " ")
                    editText.setText(cleaned)
                    editText.setSelection(cleaned.length)
                }
                error?.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    private fun formatDate(ts: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ts))
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