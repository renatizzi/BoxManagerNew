package com.example.boxmanagernew.ui.boxdetail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.boxmanagernew.ui.main.BoxViewModel
import java.text.SimpleDateFormat
import java.util.*

class BoxDetailActivity : AppCompatActivity() {

    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var boxViewModel: BoxViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: ObjectAdapter

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

        selectionBar = findViewById(R.id.selectionBar)
        textSelectionCount = findViewById(R.id.textSelectionCount)
        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)

        BottomNavManager.setup(this, BottomNavManager.TAB_BOXES)

        findViewById<TextView>(R.id.navBoxes).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        findViewById<TextView>(R.id.navCategories).setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }

        val boxId = intent.getIntExtra("boxId", -1)
        val boxName = intent.getStringExtra("boxName") ?: "Contenitore"

        textTitle.text = boxName

        val db = AppDatabase.getDatabase(this)

        val objectRepository = ObjectRepositoryImpl(
            db.objectDao(),
            db.objectTypeDao()
        )
        val objectFactory = ObjectViewModelFactory(objectRepository)
        objectViewModel = ViewModelProvider(this, objectFactory)[ObjectViewModel::class.java]

        val boxRepository = BoxRepositoryImpl(db.boxDao())
        boxViewModel = BoxViewModel(boxRepository)

        val categoryRepository = CategoryRepositoryImpl(db.categoryDao())
        categoryViewModel = CategoryViewModel(categoryRepository)

        adapter = ObjectAdapter(
            items = emptyList(),
            onClick = {
                val mode = objectViewModel.selectionMode.value ?: false
                if (mode) objectViewModel.toggleSelection(it)
            },
            onToggleSelection = { objectViewModel.toggleSelection(it) },
            onEdit = { showEditDialog(it, boxId) },
            onDelete = { obj ->
                AlertDialog.Builder(this)
                    .setTitle("Conferma eliminazione")
                    .setMessage("Eliminare questo oggetto?")
                    .setPositiveButton("Sì") { _, _ ->
                        objectViewModel.deleteObject(obj.obj)
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        objectViewModel.load(boxId)

        objectViewModel.objects.observe(this) { list ->
            adapter.updateData(list)
            textObjectsTitle.text = "Lista Oggetti (${list.size})"
        }

        objectViewModel.selectedItems.observe(this) { selectedIds ->
            val count = selectedIds.size

            if (count > 0) {
                selectionBar.visibility = View.VISIBLE
                textSelectionCount.text =
                    if (count == 1) "1 selezionato" else "$count selezionati"
            } else {
                selectionBar.visibility = View.GONE
            }

            val mode = objectViewModel.selectionMode.value ?: false
            adapter.updateSelection(selectedIds, mode)
        }

        objectViewModel.selectionMode.observe(this) { mode ->
            val selected = objectViewModel.selectedItems.value ?: emptySet()
            adapter.updateSelection(selected, mode)
        }

        buttonDeleteSelected.setOnClickListener {
            val selectedIds = objectViewModel.selectedItems.value?.toList() ?: emptyList()
            if (selectedIds.isEmpty()) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Conferma eliminazione")
                .setMessage("Eliminare ${selectedIds.size} elementi?")
                .setPositiveButton("Sì") { _, _ ->
                    objectViewModel.deleteObjects(selectedIds)
                }
                .setNegativeButton("No", null)
                .show()
        }

        boxViewModel.boxes.observe(this) { boxes ->
            val box = boxes.find { it.id == boxId }
            if (box != null) {

                textPosition.text = box.position
                textLastModified.text =
                    dateFormat.format(Date(box.lastModified))

                categoryViewModel.categories.observe(this) { categories ->
                    val category = categories.find { it.id == box.categoryId }
                    if (category != null) {
                        textCategory.text = category.name
                        val iconRes = IconMapper.getIconRes(category.icon)
                        imageCategoryIcon.setImageResource(iconRes)
                    }
                }
            }
        }

        fabAdd.setOnClickListener {
            showAddObjectDialog(boxId)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val selected = objectViewModel.selectedItems.value ?: emptySet()
                if (selected.isNotEmpty()) objectViewModel.clearSelection()
                else finish()
            }
        })
    }

    private fun showEditDialog(item: ObjectWithType, boxId: Int) {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nome oggetto"
            setText(item.typeName)
        }

        val inputDescription = EditText(this).apply {
            hint = "Descrizione"
            setText(item.obj.description)
        }

        val inputQuantity = EditText(this).apply {
            hint = "Quantità"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText(item.obj.quantity?.toString() ?: "")
        }

        layout.addView(inputName)
        layout.addView(inputDescription)
        layout.addView(inputQuantity)

        AlertDialog.Builder(this)
            .setTitle("Modifica oggetto")
            .setView(layout)
            .setPositiveButton("Salva") { _, _ ->

                val desc = inputDescription.text.toString().ifBlank { null }
                val qty = inputQuantity.text.toString().toIntOrNull()

                objectViewModel.updateObject(
                    id = item.obj.id,
                    typeObjectId = item.obj.typeObjectId,
                    boxId = boxId,
                    description = desc,
                    quantity = qty
                )
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun showAddObjectDialog(boxId: Int) {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nome oggetto"
        }

        val inputDescription = EditText(this).apply {
            hint = "Descrizione (opzionale)"
        }

        val inputQuantity = EditText(this).apply {
            hint = "Quantità (opzionale)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(inputName)
        layout.addView(inputDescription)
        layout.addView(inputQuantity)

        AlertDialog.Builder(this)
            .setTitle("Nuovo oggetto")
            .setView(layout)
            .setPositiveButton("Aggiungi") { _, _ ->

                val name = inputName.text.toString()
                val desc = inputDescription.text.toString().ifBlank { null }
                val qty = inputQuantity.text.toString().toIntOrNull()

                objectViewModel.addObject(
                    name = name,
                    boxId = boxId,
                    description = desc,
                    quantity = qty
                )
            }
            .setNegativeButton("Annulla", null)
            .show()
    }
}