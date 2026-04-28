package com.example.boxmanagernew.ui.boxdetail

import android.app.AlertDialog
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.CategoryViewModel
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.main.BoxViewModel
import java.text.SimpleDateFormat
import java.util.*

class BoxDetailActivity : AppCompatActivity() {

    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var boxViewModel: BoxViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: ObjectAdapter

    private lateinit var editSearch: EditText
    private lateinit var buttonSort: Button

    private lateinit var selectionBar: View
    private lateinit var textSelectionCount: TextView
    private lateinit var buttonDeleteSelected: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_box_detail)

        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textTitle = findViewById<TextView>(R.id.textTitle)
        val textCategory = findViewById<TextView>(R.id.textCategory)
        val imageCategoryIcon = findViewById<ImageView>(R.id.imageCategoryIcon)
        val textPosition = findViewById<TextView>(R.id.textPosition)
        val textLastModified = findViewById<TextView>(R.id.textLastModified)
        val textObjectsTitle = findViewById<TextView>(R.id.textObjectsTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerObjects)
        val fabAdd = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddObject)

        editSearch = findViewById(R.id.editSearchObjects)
        buttonSort = findViewById(R.id.buttonSortObjects)

        selectionBar = findViewById(R.id.selectionBar)
        textSelectionCount = findViewById(R.id.textSelectionCount)
        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)

        BottomNavManager.setup(this, BottomNavManager.TAB_BOXES)

        findViewById<TextView>(R.id.navBoxes).setOnClickListener { finish() }
        findViewById<TextView>(R.id.navCategories).setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        val boxId = intent.getIntExtra("boxId", -1)
        val boxName = intent.getStringExtra("boxName") ?: "Contenitore"
        textTitle.text = boxName

        val db = AppDatabase.getDatabase(this)

        val objectRepository = ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao())
        objectViewModel = ViewModelProvider(this, ObjectViewModelFactory(objectRepository))[ObjectViewModel::class.java]

        val boxRepository = BoxRepositoryImpl(db.boxDao())
        boxViewModel = BoxViewModel(boxRepository)

        val categoryRepository = CategoryRepositoryImpl(db.categoryDao(), db.boxDao())
        categoryViewModel = CategoryViewModel(categoryRepository)

        adapter = ObjectAdapter(
            items = emptyList(),
            onClick = {},
            onToggleSelection = { id -> objectViewModel.toggleSelection(id) },
            onEdit = { id ->
                val item = objectViewModel.objects.value?.find { it.obj.id == id }
                if (item != null) showEditDialog(item)
            },
            onDelete = { id -> showDeleteDialog(id) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        objectViewModel.load(boxId)

        objectViewModel.objects.observe(this) {
            adapter.updateData(it)
            textObjectsTitle.text = "Lista Oggetti (${it.size})"
        }

        objectViewModel.isAscending.observe(this) {
            UiUtils.updateSortButton(buttonSort, it)
        }

        objectViewModel.selectedItems.observe(this) {
            val count = it.size
            selectionBar.visibility = if (count > 0) View.VISIBLE else View.GONE
            textSelectionCount.text =
                if (count == 1) "1 selezionato" else "$count selezionati"

            val mode = objectViewModel.selectionMode.value ?: false
            adapter.updateSelection(it, mode)
        }

        objectViewModel.selectionMode.observe(this) {
            val selected = objectViewModel.selectedItems.value ?: emptySet()
            adapter.updateSelection(selected, it)
        }

        // 🔴 FIX: eliminazione semplice multiselezione
        buttonDeleteSelected.setOnClickListener {
            val ids = objectViewModel.selectedItems.value?.toList() ?: emptyList()
            if (ids.isNotEmpty()) {
                objectViewModel.deleteObjects(ids)
            }
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                objectViewModel.filter(query)
                adapter.updateFilterState(query.isNotBlank())
                adapter.updateQuery(query)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        buttonSort.setOnClickListener {
            hideKeyboard(it)
            editSearch.clearFocus()
            objectViewModel.toggleSort()
        }

        fabAdd.setOnClickListener {
            showAddObjectDialog(boxId)
        }

        boxViewModel.boxes.observe(this) { boxes ->
            val box = boxes.find { it.id == boxId } ?: return@observe
            textPosition.text = box.position
            textLastModified.text = dateFormat.format(Date(box.lastModified))

            categoryViewModel.categories.observe(this) { categories ->
                val category = categories.find { it.id == box.categoryId } ?: return@observe
                textCategory.text = category.name
                imageCategoryIcon.setImageResource(IconMapper.getIconRes(category.icon))
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val selected = objectViewModel.selectedItems.value ?: emptySet()
                if (selected.isNotEmpty()) objectViewModel.clearSelection()
                else finish()
            }
        })
    }

    private fun showAddObjectDialog(boxId: Int) {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val errorText = TextView(this).apply {
            setTextColor(getColor(android.R.color.holo_red_dark))
            visibility = View.GONE
            text = "Dato obbligatorio"
        }

        val inputName = EditText(this).apply {
            hint = "Nome oggetto"
        }

        val inputDescription = createSingleLineDescriptionInput(this, "Descrizione (opzionale)")

        val inputQuantity = EditText(this).apply {
            hint = "Quantità (opzionale)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(errorText)
        layout.addView(inputName)
        layout.addView(inputDescription)
        layout.addView(inputQuantity)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nuovo oggetto")
            .setView(layout)
            .setNegativeButton("Annulla", null)
            .setPositiveButton("Aggiungi", null)
            .create()

        dialog.setOnShowListener {
            val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            inputName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    errorText.visibility = View.GONE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            btn.setOnClickListener {
                val name = inputName.text.toString().trim()

                if (name.isEmpty()) {
                    errorText.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                objectViewModel.addObject(
                    name,
                    boxId,
                    inputDescription.text.toString().ifBlank { null },
                    inputQuantity.text.toString().toIntOrNull()
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditDialog(item: ObjectWithType) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val errorText = TextView(this).apply {
            setTextColor(getColor(android.R.color.holo_red_dark))
            visibility = View.GONE
            text = "Dato obbligatorio"
        }

        val inputName = EditText(this).apply {
            hint = "Nome oggetto"
            setText(item.typeName)
        }

        val inputDescription = createSingleLineDescriptionInput(this, "Descrizione").apply {
            setText(item.obj.description)
        }

        val inputQuantity = EditText(this).apply {
            hint = "Quantità"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(item.obj.quantity?.toString() ?: "")
        }

        layout.addView(errorText)
        layout.addView(inputName)
        layout.addView(inputDescription)
        layout.addView(inputQuantity)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Modifica oggetto")
            .setView(layout)
            .setNegativeButton("Annulla", null)
            .setPositiveButton("Salva", null)
            .create()

        dialog.setOnShowListener {
            val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            inputName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    errorText.visibility = View.GONE
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            btn.setOnClickListener {
                val name = inputName.text.toString().trim()

                if (name.isEmpty()) {
                    errorText.visibility = View.VISIBLE
                    return@setOnClickListener
                }

                objectViewModel.updateObjectWithName(
                    id = item.obj.id,
                    name = name,
                    boxId = item.obj.boxId,
                    description = inputDescription.text.toString().ifBlank { null },
                    quantity = inputQuantity.text.toString().toIntOrNull()
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Conferma eliminazione")
            .setMessage("Vuoi eliminare questo elemento?")
            .setPositiveButton("Sì") { _, _ ->
                val obj = objectViewModel.objects.value
                    ?.find { it.obj.id == id }?.obj
                if (obj != null) objectViewModel.deleteObject(obj)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun createSingleLineDescriptionInput(context: Context, hintText: String): EditText {
        return EditText(context).apply {
            hint = hintText
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            maxLines = 1

            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.contains("\n")) {
                        val cleaned = s.toString().replace("\n", " ")
                        setText(cleaned)
                        setSelection(cleaned.length)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            hideKeyboard(currentFocus!!)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}