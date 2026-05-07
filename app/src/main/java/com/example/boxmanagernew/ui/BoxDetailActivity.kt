package com.example.boxmanagernew.ui.boxdetail

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.*
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.ui.categories.*
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
    private lateinit var buttonMoveSelected: Button
    private lateinit var textObjectsTitle: TextView
    private lateinit var editSearch: EditText
    private lateinit var buttonSort: Button
    private lateinit var contextCard: View
    private lateinit var textContextMessage: TextView
    private lateinit var textSubtitle: TextView

    private var currentBox: Box? = null
    private var currentCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_box_detail)

        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, i ->
            val s = i.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(s.left, s.top, s.right, s.bottom)
            i
        }

        val textTitle = findViewById<TextView>(R.id.textTitle)
        textSubtitle = findViewById(R.id.textSubtitle)

        val textCategory = findViewById<TextView>(R.id.textCategory)
        val imageCategoryIcon = findViewById<ImageView>(R.id.imageCategoryIcon)
        val textPosition = findViewById<TextView>(R.id.textPosition)
        val textLastModified = findViewById<TextView>(R.id.textLastModified)

        val recycler = findViewById<RecyclerView>(R.id.recyclerObjects)
        val fab = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddObject)

        selectionBar = findViewById(R.id.selectionBar)
        textSelectionCount = findViewById(R.id.textSelectionCount)
        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)
        buttonMoveSelected = findViewById(R.id.btnMoveSelected)
        textObjectsTitle = findViewById(R.id.textObjectsTitle)
        editSearch = findViewById(R.id.editSearchObjects)
        buttonSort = findViewById(R.id.buttonSortObjects)
        contextCard = findViewById(R.id.contextCard)
        textContextMessage = findViewById(R.id.textContextMessage)

        val boxId = intent.getIntExtra("boxId", -1)
        val boxName = intent.getStringExtra("boxName") ?: "Contenitore"

        textTitle.text = "Lista Oggetti"

        val base = "Contenuto del box "
        val full = base + boxName

        val spannable = android.text.SpannableString(full)
        val start = base.length

        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            start,
            full.length,
            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            android.text.style.RelativeSizeSpan(1.1f),
            start,
            full.length,
            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textSubtitle.text = spannable

        val db = AppDatabase.getDatabase(this)

        val objectRepo = ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao())
        objectViewModel = ViewModelProvider(this, ObjectViewModelFactory(objectRepo))[ObjectViewModel::class.java]

        val boxRepo = BoxRepositoryImpl(db.boxDao())
        boxViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(boxRepo) as T
            }
        })[BoxViewModel::class.java]

        val categoryRepo = CategoryRepositoryImpl(db.categoryDao(), db.boxDao())
        categoryViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(categoryRepo) as T
            }
        })[CategoryViewModel::class.java]

        adapter = ObjectAdapter(
            emptyList(),
            onClick = {},
            onToggleSelection = { objectViewModel.toggleSelection(it) },
            onEdit = { id -> showEditObjectDialog(id) },
            onDelete = { id -> showDeleteObjectDialog(id) }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        objectViewModel.load(boxId)

        objectViewModel.objects.observe(this) {
            adapter.updateData(it)
            textObjectsTitle.text = "N. Oggetti: ${it.size}"
        }

        objectViewModel.isAscending.observe(this) { isAscending ->
            buttonSort.text =
                if (isAscending) "ORDINA ▲"
                else "ORDINA ▼"
        }

        objectViewModel.selectedItems.observe(this) {
            selectionBar.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
            textSelectionCount.text = "${it.size} selezionati"
            adapter.updateSelection(it, objectViewModel.selectionMode.value ?: false)
        }

        objectViewModel.hasHiddenSelections.observe(this) { hidden ->
            contextCard.visibility = if (hidden) View.VISIBLE else View.GONE

            if (hidden) {
                textContextMessage.text =
                    "Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
            }
        }

        contextCard.setOnClickListener {
            editSearch.setText("")
            objectViewModel.filter("")
            objectViewModel.clearSelection()
            adapter.updateQuery("")
            adapter.updateFilterState(false)
            hideKeyboard(editSearch)
        }

        buttonDeleteSelected.setOnClickListener {
            val ids = objectViewModel.selectedItems.value?.toList() ?: return@setOnClickListener

            if (objectViewModel.hasHiddenSelections.value == true) {
                contextCard.visibility = View.VISIBLE
                textContextMessage.text =
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setMessage("Conferma eliminazione?")
                .setPositiveButton("SI") { _, _ ->
                    objectViewModel.deleteObjects(ids)
                }
                .setNegativeButton("NO", null)
                .show()
        }

        buttonMoveSelected.setOnClickListener {
            val selected = objectViewModel.selectedItems.value ?: emptySet()
            if (selected.isEmpty()) return@setOnClickListener

            val boxes = boxViewModel.boxes.value ?: emptyList()
            val availableBoxes = boxes.filter { it.id != boxId }

            if (availableBoxes.isEmpty()) {
                Toast.makeText(this, "Nessun contenitore disponibile", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val names = availableBoxes.map { it.name }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("Scegli contenitore di destinazione")
                .setItems(names) { _, which ->
                    val targetBox = availableBoxes[which]

                    AlertDialog.Builder(this)
                        .setMessage("Conferma spostamento?")
                        .setPositiveButton("SI") { _, _ ->
                            objectViewModel.moveObjects(targetBox.id)
                        }
                        .setNegativeButton("NO", null)
                        .show()
                }
                .show()
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                objectViewModel.filter(s.toString())
                adapter.updateQuery(s.toString())
                adapter.updateFilterState(s.toString().isNotBlank())
            }
            override fun beforeTextChanged(s: CharSequence?, s1: Int, s2: Int, s3: Int) {}
            override fun onTextChanged(s: CharSequence?, s1: Int, s2: Int, s3: Int) {}
        })

        buttonSort.setOnClickListener {
            objectViewModel.toggleSort()
        }

        BottomNavManager.setup(this, BottomNavManager.TAB_BOXES)

        boxViewModel.boxes.observe(this) { list ->
            val box = list.find { it.id == boxId } ?: return@observe
            currentBox = box
            updateHeader(textCategory, imageCategoryIcon, textPosition, textLastModified)
        }

        categoryViewModel.categories.observe(this) { list ->
            val box = currentBox ?: return@observe
            val category = list.find { it.id == box.categoryId } ?: return@observe
            currentCategory = category
            updateHeader(textCategory, imageCategoryIcon, textPosition, textLastModified)
        }

        fab.setOnClickListener {
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

    private fun updateHeader(
        textCategory: TextView,
        imageCategoryIcon: ImageView,
        textPosition: TextView,
        textLastModified: TextView
    ) {
        val box = currentBox ?: return
        val category = currentCategory ?: return

        textCategory.text = category.name
        imageCategoryIcon.setImageResource(IconMapper.getIconRes(category.icon))
        textPosition.text = box.position

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        textLastModified.text = dateFormat.format(Date(box.lastModified))
    }

    private fun showDeleteObjectDialog(id: Int) {
        AlertDialog.Builder(this)
            .setMessage("Conferma eliminazione?")
            .setPositiveButton("SI") { _, _ ->
                val obj = objectViewModel.objects.value?.find { it.obj.id == id }?.obj ?: return@setPositiveButton
                objectViewModel.deleteObject(obj)
            }
            .setNegativeButton("NO", null)
            .show()
    }

    private fun showEditObjectDialog(id: Int) {
        val item = objectViewModel.objects.value?.find { it.obj.id == id } ?: return

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val labelName = TextView(this).apply { text = "Nome" }
        val inputName = EditText(this).apply { setText(item.typeName) }

        val labelDesc = TextView(this).apply { text = "Descrizione" }
        val inputDescription = EditText(this).apply { setText(item.obj.description) }

        val labelQty = TextView(this).apply { text = "Quantità" }
        val inputQuantity = EditText(this).apply {
            setText(item.obj.quantity?.toString() ?: "")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(labelName)
        layout.addView(inputName)
        layout.addView(labelDesc)
        layout.addView(inputDescription)
        layout.addView(labelQty)
        layout.addView(inputQuantity)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Modifica oggetto")
            .setView(layout)
            .setPositiveButton("Salva", null)
            .setNegativeButton("Annulla", null)
            .create()

        dialog.setOnShowListener {
            val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            btn.setOnClickListener {
                val name = inputName.text.toString().trim()
                if (name.isEmpty()) return@setOnClickListener

                objectViewModel.updateObjectWithName(
                    id,
                    name,
                    item.obj.boxId,
                    inputDescription.text.toString().ifBlank { null },
                    inputQuantity.text.toString().toIntOrNull()
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showAddObjectDialog(boxId: Int) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply { hint = "Nome" }
        val inputDescription = EditText(this).apply { hint = "Descrizione" }
        val inputQuantity = EditText(this).apply {
            hint = "Quantità"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        layout.addView(inputName)
        layout.addView(inputDescription)
        layout.addView(inputQuantity)

        AlertDialog.Builder(this)
            .setTitle("Nuovo oggetto")
            .setView(layout)
            .setPositiveButton("Aggiungi") { _, _ ->
                objectViewModel.addObject(
                    inputName.text.toString(),
                    boxId,
                    inputDescription.text.toString().ifBlank { null },
                    inputQuantity.text.toString().toIntOrNull()
                )
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val r = Rect()
                v.getGlobalVisibleRect(r)
                if (!r.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard(v)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}