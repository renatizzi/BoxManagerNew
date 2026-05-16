package com.example.boxmanagernew.ui.boxdetail

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.*
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.categories.CategoryViewModel
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoxDetailActivity : BaseActivity() {

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

    private var categories: List<CategoryEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_box_detail)

        setupEdgeToEdge()

        setupTopBar()

        val textTitle =
            findViewById<TextView>(R.id.textTitle)

        initViews()
        val textCategory =
            findViewById<TextView>(R.id.textCategory)

        val imageCategoryIcon =
            findViewById<ImageView>(R.id.imageCategoryIcon)

        val textPosition =
            findViewById<TextView>(R.id.textPosition)

        val textLastModified =
            findViewById<TextView>(R.id.textLastModified)

        val recycler =
            findViewById<RecyclerView>(R.id.recyclerObjects)

        val fab =
            findViewById<FloatingActionButton>(R.id.fabAddObject)

        selectionBar =
            findViewById(R.id.selectionBar)

        textSelectionCount =
            findViewById(R.id.textSelectionCount)

        buttonDeleteSelected =
            findViewById(R.id.btnDeleteSelected)

        buttonMoveSelected =
            findViewById(R.id.btnMoveSelected)

        textObjectsTitle =
            findViewById(R.id.textObjectsTitle)

        editSearch =
            findViewById(R.id.editSearchObjects)

        buttonSort =
            findViewById(R.id.buttonSortObjects)

        contextCard =
            findViewById(R.id.contextCard)

        textContextMessage =
            findViewById(R.id.textContextMessage)

        val boxId =
            intent.getIntExtra("boxId", -1)

        val boxName =
            intent.getStringExtra("boxName")
                ?: "Contenitore"

        textTitle.text =
            "Lista Oggetti"

        val base =
            "Contenuto del box "

        val full =
            base + boxName

        val spannable =
            android.text.SpannableString(full)

        val start =
            base.length

        spannable.setSpan(
            android.text.style.StyleSpan(
                android.graphics.Typeface.BOLD
            ),
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

        textSubtitle.text =
            spannable

        val db =
            AppDatabase.getDatabase(this)

        val objectRepo =
            ObjectRepositoryImpl(
                db.objectDao(),
                db.objectTypeDao()
            )

        objectViewModel =
            ViewModelProvider(
                this,
                ObjectViewModelFactory(objectRepo)
            )[ObjectViewModel::class.java]

        val boxRepo =
            BoxRepositoryImpl(db.boxDao())

        boxViewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {

                        return BoxViewModel(
                            boxRepo
                        ) as T
                    }
                }
            )[BoxViewModel::class.java]

        val categoryRepo =
            CategoryRepositoryImpl(
                db.categoryDao(),
                db.boxDao()
            )

        categoryViewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {

                        return CategoryViewModel(
                            categoryRepo
                        ) as T
                    }
                }
            )[CategoryViewModel::class.java]

        db.categoryDao().getAllCategories().observe(this) {

            categories = it
        }

        adapter =
            ObjectAdapter(
                emptyList(),

                onClick = {},

                onToggleSelection = {

                    objectViewModel.toggleSelection(it)
                },

                onEdit = { id ->

                    showEditObjectDialog(id)
                },

                onMove = { id ->

                    objectViewModel.clearSelection()

                    objectViewModel.toggleSelection(id)

                    showMoveDialog(boxId)
                },

                onDelete = { id ->

                    showDeleteObjectDialog(id)
                }
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.adapter =
            adapter

        objectViewModel.load(boxId)

        setupObservers(
            boxId,
            textCategory,
            imageCategoryIcon,
            textPosition,
            textLastModified
        )
        contextCard.setOnClickListener {

            editSearch.setText("")

            objectViewModel.filter("")

            objectViewModel.clearSelection()

            updateObjectsTitle()

            adapter.updateQuery("")

            adapter.updateFilterState(false)

            hideKeyboard(editSearch)
        }

        buttonDeleteSelected.setOnClickListener {

            val ids =
                objectViewModel.selectedItems.value
                    ?.toList()
                    ?: return@setOnClickListener

            if (
                objectViewModel
                    .hasHiddenSelections.value == true
            ) {

                contextCard.visibility =
                    View.VISIBLE

                textContextMessage.text =
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

                return@setOnClickListener
            }

            DialogUtils.showDeleteConfirmation(
                context = this
            ) {

                objectViewModel.deleteObjects(ids)
            }
        }

        buttonMoveSelected.setOnClickListener {

            val selected =
                objectViewModel.selectedItems.value
                    ?: emptySet()

            if (selected.isEmpty()) {

                return@setOnClickListener
            }

            showMoveDialog(boxId)
        }

        editSearch.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(
                    s: Editable?
                ) {

                    objectViewModel.filter(
                        s.toString()
                    )

                    adapter.updateQuery(
                        s.toString()
                    )

                    adapter.updateFilterState(
                        s.toString().isNotBlank()
                    )
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
            }
        )

        buttonSort.setOnClickListener {

            objectViewModel.toggleSort()
        }
        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_BOXES
        )

        refreshAppShell()

        fab.setOnClickListener {
            showAddObjectDialog(boxId)
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    val selected =
                        objectViewModel.selectedItems.value
                            ?: emptySet()

                    if (selected.isNotEmpty()) {

                        objectViewModel.clearSelection()

                    } else {

                        finish()
                    }
                }
            }
        )
    }

    private fun setupObservers(
        boxId: Int,
        textCategory: TextView,
        imageCategoryIcon: ImageView,
        textPosition: TextView,
        textLastModified: TextView
    ) {

        objectViewModel.objects.observe(this) {

            adapter.updateData(it)

            updateObjectsTitle()
        }

        objectViewModel.isAscending.observe(this) {

            buttonSort.text =
                if (it) "ORDINA ▲"
                else "ORDINA ▼"
        }

        objectViewModel.selectedItems.observe(this) {

            selectionBar.visibility =
                if (it.isNotEmpty())
                    View.VISIBLE
                else
                    View.GONE

            updateObjectsTitle()

            textSelectionCount.text = ""

            adapter.updateSelection(
                it,
                objectViewModel.selectionMode.value
                    ?: false
            )
        }

        objectViewModel.hasHiddenSelections.observe(this) {

            contextCard.visibility =
                if (it) View.VISIBLE
                else View.GONE

            if (it) {

                textContextMessage.text =
                    "Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
            }
        }

        boxViewModel.boxes.observe(this) {

            val box =
                it.find { item ->
                    item.id == boxId
                }
                    ?: return@observe

            currentBox = box

            updateHeader(
                textCategory,
                imageCategoryIcon,
                textPosition,
                textLastModified
            )
        }

        categoryViewModel.categories.observe(this) {

            val box =
                currentBox
                    ?: return@observe

            val category =
                it.find {
                    it.id == box.categoryId
                }

            currentCategory = category

            updateHeader(
                textCategory,
                imageCategoryIcon,
                textPosition,
                textLastModified
            )
        }
    }
    private fun initViews() {

        textSubtitle =
            findViewById(R.id.textSubtitle)


    }
    private fun updateObjectsTitle() {

        val totalObjects =
            objectViewModel.objects.value?.size
                ?: 0

        val selectedCount =
            objectViewModel.selectedItems.value?.size
                ?: 0

        textObjectsTitle.text =
            if (selectedCount > 0) {

                "N. Oggetti: $totalObjects di cui $selectedCount selezionati"

            } else {

                "N. Oggetti: $totalObjects"
            }
    }

    private fun showMoveDialog(
        currentBoxId: Int
    ) {

        val boxes =
            boxViewModel.boxes.value
                ?: emptyList()

        val availableBoxes =
            boxes.filter {
                it.id != currentBoxId
            }

        val names =
            mutableListOf<String>()

        names.add("+ Nuovo contenitore")

        availableBoxes.forEach {

            names.add(it.name)
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Scegli contenitore di destinazione"
            )
            .setItems(
                names.toTypedArray()
            ) { _, which ->

                if (which == 0) {

                    showCreateBoxAndMoveDialog()

                } else {

                    val targetBox =
                        availableBoxes[which - 1]

                    DialogUtils.showMoveConfirmation(
                        context = this
                    ) {

                        objectViewModel.moveObjects(
                            targetBox.id
                        )
                    }
                }
            }
            .show()
    }

    private fun showCreateBoxAndMoveDialog() {

        val view =
            layoutInflater.inflate(
                R.layout.dialog_add_box,
                null
            )

        val errorText =
            TextView(this).apply {

                setTextColor(
                    getColor(
                        android.R.color.holo_red_dark
                    )
                )

                visibility =
                    View.GONE

                text =
                    "Dato obbligatorio"
            }

        val name =
            view.findViewById<EditText>(
                R.id.editBoxName
            )

        val spinner =
            view.findViewById<Spinner>(
                R.id.spinnerCategory
            )

        val position =
            view.findViewById<EditText>(
                R.id.editPosition
            )

        val date =
            view.findViewById<TextView>(
                R.id.textLastModified
            )

        val container =
            view as LinearLayout

        container.addView(
            errorText,
            0
        )

        spinner.adapter =
            CategorySpinnerAdapter(
                this,
                categories
            )

        val now =
            System.currentTimeMillis()

        date.text =
            "Ultima modifica: ${
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date(now))
            }"

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Nuovo contenitore"
                )
                .setView(view)
                .setPositiveButton(
                    "Conferma",
                    null
                )
                .setNegativeButton(
                    "Annulla",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btn.setOnClickListener {

                val boxName =
                    name.text.toString().trim()

                if (boxName.isEmpty()) {

                    errorText.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                val category =
                    spinner.selectedItem
                            as CategoryEntity

                lifecycleScope.launch {

                    val newBoxId =
                        boxViewModel.addBoxAndReturnId(
                            boxName,
                            category.id,
                            position.text.toString()
                        )

                    objectViewModel.moveObjects(
                        newBoxId
                    )
                }

                dialog.dismiss()
            }

            name.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(
                        s: Editable?
                    ) {

                        errorText.visibility =
                            View.GONE
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
            )
        }

        dialog.show()
    }

    private fun updateHeader(
        textCategory: TextView,
        imageCategoryIcon: ImageView,
        textPosition: TextView,
        textLastModified: TextView
    ) {

        val box =
            currentBox
                ?: return

        textPosition.text =
            box.position

        val dateFormat =
            SimpleDateFormat(
                "dd.MM.yyyy",
                Locale.getDefault()
            )

        textLastModified.text =
            dateFormat.format(
                Date(box.lastModified)
            )

        val category =
            currentCategory

        if (category != null) {

            textCategory.text =
                category.name

            imageCategoryIcon.setImageResource(
                IconMapper.getIconRes(
                    category.icon
                )
            )

        } else {

            textCategory.text =
                ""

            imageCategoryIcon.setImageDrawable(null)
        }
    }

    private fun showDeleteObjectDialog(
        id: Int
    ) {

        DialogUtils.showDeleteConfirmation(
            context = this
        ) {

            val obj =
                objectViewModel.objects.value
                    ?.find {
                        it.obj.id == id
                    }
                    ?.obj
                    ?: return@showDeleteConfirmation

            objectViewModel.deleteObject(obj)
        }
    }
    private fun showEditObjectDialog(
        id: Int
    ) {

        val item =
            objectViewModel.objects.value
                ?.find {
                    it.obj.id == id
                }
                ?: return

        val view =
            layoutInflater.inflate(
                R.layout.dialog_edit_object,
                null
            )

        val textError =
            view.findViewById<TextView>(
                R.id.textErrorEditObject
            )

        val inputName =
            view.findViewById<EditText>(
                R.id.editObjectName
            )

        val inputDescription =
            view.findViewById<EditText>(
                R.id.editObjectDescription
            )

        val inputQuantity =
            view.findViewById<EditText>(
                R.id.editObjectQuantity
            )

        inputName.setText(
            item.typeName
        )

        inputDescription.setText(
            item.obj.description
        )

        inputQuantity.setText(
            item.obj.quantity?.toString()
                ?: ""
        )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    "Modifica oggetto"
                )
                .setView(view)
                .setPositiveButton(
                    "Salva",
                    null
                )
                .setNegativeButton(
                    "Annulla",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btn.setOnClickListener {

                val name =
                    inputName.text.toString()
                        .trim()

                if (name.isEmpty()) {

                    textError.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                objectViewModel.updateObjectWithName(
                    id,
                    name,
                    item.obj.boxId,
                    inputDescription.text.toString()
                        .ifBlank { null },
                    inputQuantity.text.toString()
                        .toIntOrNull()
                )

                dialog.dismiss()
            }

            inputName.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(
                        s: Editable?
                    ) {

                        textError.visibility =
                            View.GONE
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
            )
        }

        dialog.show()
    }

    private fun showAddObjectDialog(
        boxId: Int
    ) {

        val dialogViews =
            DialogUtils.createObjectDialog(
                context = this,
                layout = R.layout.dialog_add_object
            )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Nuovo oggetto")
                .setView(dialogViews.view)
                .setPositiveButton(
                    "Aggiungi",
                    null
                )
                .setNegativeButton(
                    "Annulla",
                    null
                )
                .create()

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val name =
                    dialogViews.name.text
                        .toString()
                        .trim()

                if (
                    !DialogUtils.validateRequiredName(
                        name,
                        dialogViews.errorText
                    )
                ) {
                    return@setOnClickListener
                }

                objectViewModel.addObject(
                    name,
                    boxId,
                    dialogViews.description.text
                        .toString()
                        .ifBlank { null },
                    dialogViews.quantity.text
                        .toString()
                        .toIntOrNull()
                )

                dialog.dismiss()
            }

            dialogViews.name.addTextChangedListener(
                object : TextWatcher {

                    override fun afterTextChanged(
                        s: Editable?
                    ) {
                        dialogViews.errorText.visibility =
                            View.GONE
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {}
                }
            )
        }

        dialog.show()
    }
}