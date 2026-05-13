package com.example.boxmanagernew

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.DialogUtils
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

        setupViews()

        val db =
            DatabaseProvider.getDatabase(applicationContext)

        val repository =
            BoxRepositoryImpl(db.boxDao())

        initializeDefaultData(db)

        setupAdapter()

        setupViewModel(repository)

        observeData(db)

        setupListeners()
    }

    private fun setupViews() {

        findViewById<TextView>(R.id.textTitle).text =
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

        buttonDeleteSelected =
            findViewById(R.id.btnDeleteSelected)

        buttonMoveSelected =
            findViewById(R.id.btnMoveSelected)

        textSelectionCount =
            findViewById(R.id.textSelectionCount)

        selectionBar =
            findViewById(R.id.selectionBar)
    }

    private fun initializeDefaultData(
        db: com.example.boxmanagernew.data.local.AppDatabase
    ) {

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
    }

    private fun setupAdapter() {

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

                        openBoxDetail(it)
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

        findViewById<RecyclerView>(
            R.id.recyclerViewBoxes
        ).apply {

            layoutManager =
                LinearLayoutManager(this@MainActivity)

            adapter =
                this@MainActivity.adapter
        }
    }

    private fun setupViewModel(
        repository: BoxRepositoryImpl
    ) {

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
    }

    private fun observeData(
        db: com.example.boxmanagernew.data.local.AppDatabase
    ) {

        db.categoryDao().getAllCategories().observe(this) {

            categories = it

            adapter.updateCategories(it)

            viewModel.setCategories(it)
        }

        viewModel.boxes.observe(this) {

            adapter.updateData(it)

            updateSelectionCounter(
                viewModel.selectedItems.value?.size ?: 0,
                it.size
            )
        }

        viewModel.selectedItems.observe(this) {

            selectionBar.visibility =
                if (it.isNotEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            updateSelectionCounter(
                it.size,
                viewModel.boxes.value?.size ?: 0
            )

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

        viewModel.isAscending.observe(this) {

            UiUtils.updateSortButton(
                buttonSort,
                it
            )
        }
    }

    private fun setupListeners() {

        contextCard.setOnClickListener {

            clearFilterAndSelection()
        }

        buttonDeleteSelected.setOnClickListener {

            handleDeleteSelected()
        }

        buttonMoveSelected.setOnClickListener {

            handleMoveSelected()
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

        findViewById<FloatingActionButton>(
            R.id.fabAdd
        ).setOnClickListener {

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

    private fun openBoxDetail(
        box: Box
    ) {

        startActivity(
            Intent(
                this,
                BoxDetailActivity::class.java
            ).apply {

                putExtra("boxId", box.id)

                putExtra("boxName", box.name)
            }
        )
    }

    private fun clearFilterAndSelection() {

        editSearch.setText("")

        viewModel.filter("")

        viewModel.clearSelection()

        adapter.updateQuery("")

        hideKeyboard(editSearch)
    }

    private fun updateSelectionCounter(
        selectedCount: Int,
        totalBoxes: Int
    ) {

        textSelectionCount.text =
            if (selectedCount > 0) {

                "N. Contenitori: $totalBoxes di cui $selectedCount selezionati"

            } else {

                "N. Contenitori: $totalBoxes"
            }
    }

    private fun handleDeleteSelected() {

        val ids =
            viewModel.selectedItems.value?.toList()
                ?: return

        if (
            viewModel.hasHiddenSelections.value == true
        ) {

            contextCard.visibility =
                View.VISIBLE

            textContextMessage.text =
                "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

            return
        }

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

            var totalObjects = 0

            ids.forEach { boxId ->

                totalObjects +=
                    objectRepository.countObjectsByBox(
                        boxId
                    )
            }

            if (totalObjects > 0) {

                DialogUtils.showObjectsDeleteDialog(
                    context = this@MainActivity,

                    onDelete = {

                        viewModel.deleteBoxes(ids)
                    },

                    onMoveObjects = {

                        showDestinationBoxDialog(
                            sourceBoxIds = ids,
                            deleteAfterMove = true
                        )
                    }
                )

            } else {

                DialogUtils.showDeleteConfirmation(
                    context = this@MainActivity
                ) {

                    viewModel.deleteBoxes(ids)
                }
            }
        }
    }

    private fun handleMoveSelected() {

        val selected =
            viewModel.selectedItems.value
                ?: emptySet()

        if (selected.isEmpty()) {

            return
        }

        if (
            viewModel.hasHiddenSelections.value == true
        ) {

            contextCard.visibility =
                View.VISIBLE

            textContextMessage.text =
                "Impossibile spostare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

            return
        }

        DialogUtils.showMoveBoxesDialog(
            context = this
        ) { newPosition ->

            viewModel.moveBoxes(
                newPosition
            )
        }
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

        val dialogViews =
            DialogUtils.createBoxDialog(
                context = this,
                categories = categories,
                timestamp = System.currentTimeMillis()
            )

        val dialog =
            DialogUtils.createBoxConfirmDialog(
                context = this,
                view = dialogViews.view
            )

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btn.setOnClickListener {

                val boxName =
                    dialogViews.name.text
                        .toString()
                        .trim()

                if (
                    !DialogUtils.validateRequiredName(
                        boxName,
                        dialogViews.errorText
                    )
                ) {

                    return@setOnClickListener
                }

                val category =
                    dialogViews.spinner.selectedItem
                            as CategoryEntity

                lifecycleScope.launch {

                    val newBoxId =
                        viewModel.addBoxAndReturnId(
                            boxName,
                            category.id,
                            dialogViews.position.text.toString()
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

        val now =
            System.currentTimeMillis()

        val dialogViews =
            DialogUtils.createBoxDialog(
                context = this,
                categories = categories,
                timestamp = now
            )

        val dialog =
            DialogUtils.createBoxConfirmDialog(
                context = this,
                view = dialogViews.view
            )

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btn.setOnClickListener {

                val n =
                    dialogViews.name.text
                        .toString()
                        .trim()

                if (
                    !DialogUtils.validateRequiredName(
                        n,
                        dialogViews.errorText
                    )
                ) {

                    return@setOnClickListener
                }

                val cat =
                    dialogViews.spinner.selectedItem
                            as CategoryEntity

                viewModel.addBox(
                    n,
                    cat.id,
                    dialogViews.position.text
                        .toString()
                )

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showEditDialog(
        box: Box
    ) {

        val dialogViews =
            DialogUtils.createBoxDialog(
                context = this,
                categories = categories,
                timestamp = box.lastModified,
                box = box
            )

        val dialog =
            DialogUtils.createBoxConfirmDialog(
                context = this,
                view = dialogViews.view
            )

        dialog.setOnShowListener {

            val btn =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btn.setOnClickListener {

                val n =
                    dialogViews.name.text
                        .toString()
                        .trim()

                if (
                    !DialogUtils.validateRequiredName(
                        n,
                        dialogViews.errorText
                    )
                ) {

                    return@setOnClickListener
                }

                val cat =
                    dialogViews.spinner.selectedItem
                            as CategoryEntity

                viewModel.updateBox(
                    box.id,
                    n,
                    cat.id,
                    dialogViews.position.text
                        .toString()
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

                DialogUtils.showObjectsDeleteDialog(
                    context = this@MainActivity,

                    onDelete = {

                        viewModel.deleteBox(id)
                    },

                    onMoveObjects = {

                        showDestinationBoxDialog(
                            sourceBoxIds = listOf(id),
                            deleteAfterMove = true
                        )
                    }
                )

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