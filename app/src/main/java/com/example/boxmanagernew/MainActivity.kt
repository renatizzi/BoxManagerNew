package com.example.boxmanagernew

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.local.entity.ObjectTypeEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.ui.boxdetail.BoxDetailActivity
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.DialogUtils.createRequiredFieldErrorText
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private lateinit var viewModel: BoxViewModel
    private lateinit var adapter: BoxAdapter

    private lateinit var buttonDeleteSelected: Button
    private lateinit var buttonMoveSelected: Button
    private lateinit var textSelectionCount: TextView
    private lateinit var selectionBar: View

    private lateinit var contextCard: View
    private lateinit var textContextMessage: TextView
    private lateinit var editSearch: EditText
    private lateinit var buttonSort: Button

    private var categories: List<CategoryEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupEdgeToEdge()

        setupTopBar()

        val textTitle =
            findViewById<TextView>(R.id.textTitle)

        textTitle.text =
            "Contenitori"

        findViewById<TextView>(R.id.textSubtitle).text =
            "Gestione Contenitori e loro contenuti"

        contextCard =
            findViewById(R.id.contextCard)

        textContextMessage =
            findViewById(R.id.textContextMessage)

        editSearch =
            findViewById(R.id.editTextSearch)

        buttonSort =
            findViewById(R.id.buttonSort)

        val recyclerView =
            findViewById<RecyclerView>(R.id.recyclerViewBoxes)

        val fab =
            findViewById<FloatingActionButton>(R.id.fabAdd)

        buttonDeleteSelected =
            findViewById(R.id.btnDeleteSelected)

        buttonMoveSelected =
            findViewById(R.id.btnMoveSelected)

        textSelectionCount =
            findViewById(R.id.textSelectionCount)

        selectionBar =
            findViewById(R.id.selectionBar)

        val db =
            DatabaseProvider.getDatabase(applicationContext)

        val repository =
            BoxRepositoryImpl(db.boxDao())

        lifecycleScope.launch {

            if (
                db.categoryDao()
                    .getCategoryByName("Generico") == null
            ) {

                db.categoryDao().insert(
                    CategoryEntity(
                        name = "Generico",
                        icon = "outline_box_24"
                    )
                )
            }

            if (
                db.objectTypeDao()
                    .getByName("Generico") == null
            ) {

                db.objectTypeDao().insert(
                    ObjectTypeEntity(
                        name = "Generico"
                    )
                )
            }
        }

        adapter =
            BoxAdapter(
                emptyList(),
                emptyList(),

                onClick = {

                    if (
                        viewModel.selectionMode.value == true
                    ) {

                        viewModel.toggleSelection(it)

                    } else {

                        startActivity(
                            Intent(
                                this,
                                BoxDetailActivity::class.java
                            ).apply {

                                putExtra("boxId", it.id)

                                putExtra("boxName", it.name)
                            }
                        )
                    }
                },

                onEdit = {

                    showEditDialog(it)
                },

                onDelete = {

                    showDeleteDialog(it.id)
                },

                onToggleSelection = {

                    viewModel.toggleSelection(it)
                }
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter

        viewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {

                        return BoxViewModel(
                            repository
                        ) as T
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

            val selectedCount =
                viewModel.selectedItems.value?.size ?: 0

            textSelectionCount.text =
                if (selectedCount > 0) {

                    "N. Contenitori: ${it.size} di cui $selectedCount selezionati"

                } else {

                    "N. Contenitori: ${it.size}"
                }
        }

        viewModel.selectedItems.observe(this) {

            selectionBar.visibility =
                if (it.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            val totalBoxes =
                viewModel.boxes.value?.size ?: 0

            textSelectionCount.text =
                if (it.isNotEmpty()) {

                    "N. Contenitori: $totalBoxes di cui ${it.size} selezionati"

                } else {

                    "N. Contenitori: $totalBoxes"
                }

            adapter.updateSelection(
                it,
                viewModel.selectionMode.value ?: false
            )
        }

        viewModel.hasHiddenSelections.observe(
            this
        ) { hidden ->

            contextCard.visibility =
                if (hidden) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            if (hidden) {

                textContextMessage.text =
                    "Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
            }
        }

        contextCard.setOnClickListener {

            editSearch.setText("")

            viewModel.filter("")

            viewModel.clearSelection()

            adapter.updateQuery("")

            hideKeyboard(editSearch)
        }

        buttonDeleteSelected.setOnClickListener {

            val ids =
                viewModel.selectedItems.value?.toList()
                    ?: return@setOnClickListener

            if (
                viewModel.hasHiddenSelections.value == true
            ) {

                contextCard.visibility =
                    View.VISIBLE

                textContextMessage.text =
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

                return@setOnClickListener
            }

            lifecycleScope.launch {

                val objectRepository =
                    ObjectRepositoryImpl(
                        db.objectDao(),
                        db.objectTypeDao()
                    )

                var totalObjects = 0

                ids.forEach { boxId ->

                    totalObjects +=
                        objectRepository.countObjectsByBox(
                            boxId
                        )
                }

                if (totalObjects > 0) {

                    val firstDialog =
                        AlertDialog.Builder(
                            this@MainActivity
                        )
                            .setMessage(
                                "Confermi anche l'eliminazione degli oggetti contenuti?"
                            )
                            .setPositiveButton(
                                "SI"
                            ) { _, _ ->

                                viewModel.deleteBoxes(ids)
                            }
                            .setNegativeButton(
                                "NO",
                                null
                            )
                            .create()

                    firstDialog.setOnShowListener {

                        val noButton =
                            firstDialog.getButton(
                                AlertDialog.BUTTON_NEGATIVE
                            )

                        noButton.setOnClickListener {

                            firstDialog.dismiss()

                            window.decorView.post {

                                AlertDialog.Builder(
                                    this@MainActivity
                                )
                                    .setMessage(
                                        "Vuoi spostare gli oggetti in un altro contenitore?"
                                    )
                                    .setPositiveButton(
                                        "SI"
                                    ) { _, _ ->

                                        showDestinationBoxDialog(
                                            sourceBoxIds = ids,
                                            deleteAfterMove = true
                                        )
                                    }
                                    .setNegativeButton(
                                        "ANNULLA",
                                        null
                                    )
                                    .show()
                            }
                        }
                    }

                    firstDialog.show()

                } else {

                    DialogUtils.showDeleteConfirmation(
                        context = this@MainActivity
                    ) {

                        viewModel.deleteBoxes(ids)
                    }
                }
            }
        }

        buttonMoveSelected.setOnClickListener {

            val selected =
                viewModel.selectedItems.value
                    ?: emptySet()

            if (selected.isEmpty()) {

                return@setOnClickListener
            }

            if (
                viewModel.hasHiddenSelections.value == true
            ) {

                contextCard.visibility =
                    View.VISIBLE

                textContextMessage.text =
                    "Impossibile spostare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

                return@setOnClickListener
            }

            val input =
                EditText(this)

            AlertDialog.Builder(this)
                .setTitle("Nuova posizione")
                .setView(input)
                .setPositiveButton(
                    "Conferma"
                ) { _, _ ->

                    val newPosition =
                        input.text.toString().trim()

                    viewModel.moveBoxes(
                        newPosition
                    )
                }
                .setNegativeButton(
                    "Annulla",
                    null
                )
                .show()
        }

        editSearch.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(
                    s: Editable?
                ) {

                    viewModel.filter(
                        s.toString()
                    )

                    adapter.updateQuery(
                        s.toString()
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

        UiUtils.updateSortButton(
            buttonSort,
            true
        )

        buttonSort.setOnClickListener {

            viewModel.toggleSort()
        }

        viewModel.isAscending.observe(this) {

            UiUtils.updateSortButton(
                buttonSort,
                it
            )
        }

        fab.setOnClickListener {

            showAddDialog()
        }

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_BOXES
        )

        findViewById<View>(R.id.navCategories)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        CategoriesActivity::class.java
                    )
                )
            }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (
                        (
                                viewModel.selectedItems.value
                                    ?: emptySet()
                                ).isNotEmpty()
                    ) {

                        viewModel.clearSelection()

                    } else {

                        finish()
                    }
                }
            }
        )
    }

    private fun showDestinationBoxDialog(
        sourceBoxIds: List<Int>,
        deleteAfterMove: Boolean
    ) {

        val allBoxes =
            viewModel.boxes.value ?: emptyList()

        val availableBoxes =
            allBoxes.filter {
                !sourceBoxIds.contains(it.id)
            }

        val names =
            mutableListOf<String>()

        names.add("+ Nuovo contenitore")

        availableBoxes.forEach {

            names.add(it.name)
        }

        AlertDialog.Builder(this)
            .setTitle(
                "Scegli contenitore destinazione"
            )
            .setItems(
                names.toTypedArray()
            ) { _, which ->

                if (which == 0) {

                    showCreateDestinationBoxDialog(
                        sourceBoxIds,
                        deleteAfterMove
                    )

                } else {

                    val targetBox =
                        availableBoxes[which - 1]

                    moveObjectsAndDeleteBoxes(
                        sourceBoxIds,
                        targetBox.id,
                        deleteAfterMove
                    )
                }
            }
            .show()
    }

    private fun showCreateDestinationBoxDialog(
        sourceBoxIds: List<Int>,
        deleteAfterMove: Boolean
    ) {

        val view =
            DialogUtils.inflateAddBoxDialog(this)
        val dialogViews =
            DialogUtils.bindBoxDialogViews(
                this,
                view
            )

        val errorText =
            dialogViews.errorText

        val name =
            dialogViews.name

        val spinner =
            dialogViews.spinner

        val position =
            dialogViews.position

        val date =
            dialogViews.date
        DialogUtils.setupCategorySpinner(
            this,
            spinner,
            categories
        )
        DialogUtils.setupLastModifiedText(
            date,
            System.currentTimeMillis()
        )
        val dialog =
            AlertDialog.Builder(this)
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
                        viewModel.addBoxAndReturnId(
                            boxName,
                            category.id,
                            position.text.toString()
                        )

                    moveObjectsAndDeleteBoxes(
                        sourceBoxIds,
                        newBoxId,
                        deleteAfterMove
                    )
                }

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun moveObjectsAndDeleteBoxes(
        sourceBoxIds: List<Int>,
        targetBoxId: Int,
        deleteAfterMove: Boolean
    ) {

        lifecycleScope.launch {

            val db =
                DatabaseProvider.getDatabase(
                    applicationContext
                )

            val objectRepository =
                ObjectRepositoryImpl(
                    db.objectDao(),
                    db.objectTypeDao()
                )

            val objectsToMove =
                mutableListOf<Object>()

            sourceBoxIds.forEach { boxId ->

                val objects =
                    objectRepository.getObjectsByBoxSync(
                        boxId
                    )

                objectsToMove.addAll(objects)
            }

            objectsToMove.forEach { obj ->

                objectRepository.moveObjects(
                    listOf(obj.id),
                    targetBoxId
                )
            }

            if (deleteAfterMove) {

                viewModel.deleteBoxes(
                    sourceBoxIds
                )
            }
        }
    }

    private fun showAddDialog() {

        val view =
            DialogUtils.inflateAddBoxDialog(this)
        val dialogViews =
            DialogUtils.bindBoxDialogViews(
                this,
                view
            )

        val errorText =
            dialogViews.errorText

        val name =
            dialogViews.name

        val spinner =
            dialogViews.spinner

        val position =
            dialogViews.position

        val date =
            dialogViews.date
        DialogUtils.setupBoxDialogInputs(
            name,
            position
        )

        DialogUtils.setupBoxDialogWatchers(
            name,
            position,
            errorText
        )
        DialogUtils.setupCategorySpinner(
            this,
            spinner,
            categories
        )
        val now =
            System.currentTimeMillis()
        DialogUtils.setupLastModifiedText(
            date,
            now
        )

        val dialog =
            AlertDialog.Builder(this)
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

                val n =
                    name.text.toString().trim()

                if (n.isEmpty()) {

                    errorText.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                val cat =
                    spinner.selectedItem
                            as CategoryEntity

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

    private fun showEditDialog(
        box: Box
    ) {

        val view =
            DialogUtils.inflateAddBoxDialog(this)
        val dialogViews =
            DialogUtils.bindBoxDialogViews(
                this,
                view
            )

        val errorText =
            dialogViews.errorText

        val name =
            dialogViews.name

        val spinner =
            dialogViews.spinner

        val position =
            dialogViews.position

        val date =
            dialogViews.date
        DialogUtils.setupBoxDialogInputs(
            name,
            position
        )
        name.setText(box.name)

        position.setText(box.position)
        DialogUtils.setupBoxDialogWatchers(
            name,
            position,
            errorText
        )

        DialogUtils.setupCategorySpinner(
            this,
            spinner,
            categories
        )
        val index =
            categories.indexOfFirst {

                it.id == box.categoryId
            }

        if (index >= 0) {

            spinner.setSelection(index)
        }

        DialogUtils.setupLastModifiedText(
            date,
            box.lastModified
        )
        val dialog =
            AlertDialog.Builder(this)
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

                val n =
                    name.text.toString().trim()

                if (n.isEmpty()) {

                    errorText.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                val cat =
                    spinner.selectedItem
                            as CategoryEntity

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

    private fun showDeleteDialog(
        id: Int
    ) {

        lifecycleScope.launch {

            val db =
                DatabaseProvider.getDatabase(
                    applicationContext
                )

            val objectRepository =
                ObjectRepositoryImpl(
                    db.objectDao(),
                    db.objectTypeDao()
                )

            val objectCount =
                objectRepository.countObjectsByBox(
                    id
                )

            if (objectCount > 0) {

                val firstDialog =
                    AlertDialog.Builder(
                        this@MainActivity
                    )
                        .setMessage(
                            "Confermi anche l'eliminazione degli oggetti contenuti?"
                        )
                        .setPositiveButton(
                            "SI"
                        ) { _, _ ->

                            viewModel.deleteBox(id)
                        }
                        .setNegativeButton(
                            "NO",
                            null
                        )
                        .create()

                firstDialog.setOnShowListener {

                    val noButton =
                        firstDialog.getButton(
                            AlertDialog.BUTTON_NEGATIVE
                        )

                    noButton.setOnClickListener {

                        firstDialog.dismiss()

                        window.decorView.post {

                            AlertDialog.Builder(
                                this@MainActivity
                            )
                                .setMessage(
                                    "Vuoi spostare gli oggetti in un altro contenitore?"
                                )
                                .setPositiveButton(
                                    "SI"
                                ) { _, _ ->

                                    showDestinationBoxDialog(
                                        sourceBoxIds = listOf(id),
                                        deleteAfterMove = true
                                    )
                                }
                                .setNegativeButton(
                                    "ANNULLA",
                                    null
                                )
                                .show()
                        }
                    }
                }

                firstDialog.show()

            } else {

                DialogUtils.showDeleteConfirmation(
                    context = this@MainActivity
                ) {

                    viewModel.deleteBox(id)
                }
            }
        }
    }
}
