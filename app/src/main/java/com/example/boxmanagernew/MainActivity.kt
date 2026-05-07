package com.example.boxmanagernew

import android.content.Context
import android.content.Intent
import android.graphics.Rect
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
    private lateinit var buttonSort: Button

    private var categories: List<CategoryEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

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

        contextCard = findViewById(R.id.contextCard)
        textContextMessage = findViewById(R.id.textContextMessage)
        editSearch = findViewById(R.id.editTextSearch)
        buttonSort = findViewById(R.id.buttonSort)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBoxes)
        val fab = findViewById<FloatingActionButton>(R.id.fabAdd)

        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)
        textSelectionCount = findViewById(R.id.textSelectionCount)
        selectionBar = findViewById(R.id.selectionBar)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = BoxRepositoryImpl(db.boxDao())

        lifecycleScope.launch {

            if (db.categoryDao().getCategoryByName("Generico") == null) {
                db.categoryDao().insert(
                    CategoryEntity(
                        name = "Generico",
                        icon = "outline_box_24"
                    )
                )
            }

            if (db.objectTypeDao().getByName("Generico") == null) {
                db.objectTypeDao().insert(
                    ObjectTypeEntity(name = "Generico")
                )
            }
        }

        adapter = BoxAdapter(
            emptyList(),
            emptyList(),
            onClick = {

                if (viewModel.selectionMode.value == true) {
                    viewModel.toggleSelection(it)
                } else {

                    startActivity(
                        Intent(this, BoxDetailActivity::class.java).apply {
                            putExtra("boxId", it.id)
                            putExtra("boxName", it.name)
                        }
                    )
                }
            },
            onEdit = { showEditDialog(it) },
            onDelete = { showDeleteDialog(it.id) },
            onToggleSelection = { viewModel.toggleSelection(it) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return BoxViewModel(repository) as T
                }
            }
        )[BoxViewModel::class.java]

        db.categoryDao().getAllCategories().observe(this) {

            categories = it

            adapter.updateCategories(it)

            viewModel.setCategories(it)
        }

        viewModel.boxes.observe(this) {
            adapter.updateData(it)
        }

        viewModel.selectedItems.observe(this) {

            selectionBar.visibility =
                if (it.isNotEmpty()) View.VISIBLE else View.GONE

            textSelectionCount.text =
                "${it.size} selezionati"

            adapter.updateSelection(
                it,
                viewModel.selectionMode.value ?: false
            )
        }

        viewModel.hasHiddenSelections.observe(this) { hidden ->

            contextCard.visibility =
                if (hidden) View.VISIBLE else View.GONE

            if (hidden) {

                textContextMessage.text =
                    "Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
            }
        }

        contextCard.setOnClickListener {

            editSearch.setText("")

            viewModel.filter("")

            viewModel.clearSelection()

            hideKeyboard(editSearch)
        }

        buttonDeleteSelected.setOnClickListener {

            val ids =
                viewModel.selectedItems.value?.toList()
                    ?: return@setOnClickListener

            if (viewModel.hasHiddenSelections.value == true) {

                contextCard.visibility = View.VISIBLE

                textContextMessage.text =
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setMessage("Conferma eliminazione?")
                .setPositiveButton("SI") { _, _ ->
                    viewModel.deleteBoxes(ids)
                }
                .setNegativeButton("NO", null)
                .show()
        }

        editSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                viewModel.filter(s.toString())

                adapter.updateQuery(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                s1: Int,
                s2: Int,
                s3: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                s1: Int,
                s2: Int,
                s3: Int
            ) {
            }
        })

        updateSortButton(true)

        buttonSort.setOnClickListener {
            viewModel.toggleSort()
        }

        viewModel.isAscending.observe(this) { isAsc ->
            updateSortButton(isAsc)
        }

        fab.setOnClickListener {
            showAddDialog()
        }

        BottomNavManager.setup(this, BottomNavManager.TAB_BOXES)

        findViewById<View>(R.id.navCategories).setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if ((viewModel.selectedItems.value ?: emptySet()).isNotEmpty()) {
                        viewModel.clearSelection()
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    private fun updateSortButton(isAscending: Boolean) {
        buttonSort.text =
            if (isAscending) "ORDINA ▲" else "ORDINA ▼"
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        if (ev.action == MotionEvent.ACTION_DOWN) {

            val v = currentFocus

            if (v is EditText) {

                val outRect = Rect()

                v.getGlobalVisibleRect(outRect)

                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {

                    v.clearFocus()

                    hideKeyboard(v)
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(view: View) {

        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showAddDialog() {

        val view =
            LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_box, null)

        val errorText = TextView(this).apply {

            setTextColor(getColor(android.R.color.holo_red_dark))

            visibility = View.GONE

            text = "Dato obbligatorio"
        }

        val name =
            view.findViewById<EditText>(R.id.editBoxName)

        val spinner =
            view.findViewById<Spinner>(R.id.spinnerCategory)

        val position =
            view.findViewById<EditText>(R.id.editPosition)

        val date =
            view.findViewById<TextView>(R.id.textLastModified)

        val container = view as LinearLayout

        container.addView(errorText, 0)

        name.inputType = InputType.TYPE_CLASS_TEXT
        position.inputType = InputType.TYPE_CLASS_TEXT

        name.addTextChangedListener(
            noEnterWatcher(name, errorText)
        )

        position.addTextChangedListener(
            noEnterWatcher(position, null)
        )

        spinner.adapter =
            CategorySpinnerAdapter(this, categories)

        val now = System.currentTimeMillis()

        date.text =
            "Ultima modifica: ${formatDate(now)}"

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Conferma", null)
            .setNegativeButton("Annulla", null)
            .create()

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            btn.setOnClickListener {

                val n =
                    name.text.toString().trim()

                if (n.isEmpty()) {

                    errorText.visibility = View.VISIBLE

                    return@setOnClickListener
                }

                val cat =
                    spinner.selectedItem as CategoryEntity

                viewModel.addBox(
                    n,
                    cat.id,
                    position.text.toString()
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditDialog(box: Box) {

        val view =
            LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_box, null)

        val errorText = TextView(this).apply {

            setTextColor(getColor(android.R.color.holo_red_dark))

            visibility = View.GONE

            text = "Dato obbligatorio"
        }

        val name =
            view.findViewById<EditText>(R.id.editBoxName)

        val spinner =
            view.findViewById<Spinner>(R.id.spinnerCategory)

        val position =
            view.findViewById<EditText>(R.id.editPosition)

        val date =
            view.findViewById<TextView>(R.id.textLastModified)

        val container = view as LinearLayout

        container.addView(errorText, 0)

        name.setText(box.name)

        position.setText(box.position)

        name.addTextChangedListener(
            noEnterWatcher(name, errorText)
        )

        position.addTextChangedListener(
            noEnterWatcher(position, null)
        )

        spinner.adapter =
            CategorySpinnerAdapter(this, categories)

        val index =
            categories.indexOfFirst {
                it.id == box.categoryId
            }

        if (index >= 0) {
            spinner.setSelection(index)
        }

        date.text =
            "Ultima modifica: ${formatDate(box.lastModified)}"

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Conferma", null)
            .setNegativeButton("Annulla", null)
            .create()

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            btn.setOnClickListener {

                val n =
                    name.text.toString().trim()

                if (n.isEmpty()) {

                    errorText.visibility = View.VISIBLE

                    return@setOnClickListener
                }

                val cat =
                    spinner.selectedItem as CategoryEntity

                viewModel.updateBox(
                    box.id,
                    n,
                    cat.id,
                    position.text.toString()
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(id: Int) {

        AlertDialog.Builder(this)
            .setMessage("Conferma eliminazione?")
            .setPositiveButton("SI") { _, _ ->
                viewModel.deleteBox(id)
            }
            .setNegativeButton("NO", null)
            .show()
    }

    private fun noEnterWatcher(
        editText: EditText,
        error: TextView?
    ): TextWatcher {

        return object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if (s != null && s.contains("\n")) {

                    val cleaned =
                        s.toString().replace("\n", " ")

                    editText.setText(cleaned)

                    editText.setSelection(cleaned.length)
                }

                error?.visibility = View.GONE
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
        }
    }

    private fun formatDate(ts: Long): String {

        return SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(ts))
    }
}