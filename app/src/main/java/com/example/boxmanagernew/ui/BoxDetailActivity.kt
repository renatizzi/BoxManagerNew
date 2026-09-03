package com.example.boxmanagernew.ui.boxdetail

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.*
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.ui.categories.CategorySpinnerAdapter
import com.example.boxmanagernew.ui.categories.CategoryViewModel
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.family.deletion.FamilyDeleteProvider
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.CreatedByResolver
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.common.VoiceSearchController
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.example.boxmanagernew.viewoutput.ui.ViewOutputController
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

    private var ignoreObjectSearchChanges =
        false

    private var displaySearchText =
        ""

    private var advancedObjectMatch =
        false

    private lateinit var outputController: ViewOutputController

    private val exportFolderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri != null && ::outputController.isInitialized) {
                outputController.onFolderChosen(uri)
            }
        }

    private val voiceSearch =
        VoiceSearchController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_box_detail)

        setupAppShell()

        val textTitle =
            findViewById<TextView>(R.id.textTitle)

        initViews()
        setupViewOutputActions()
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

        editSearch.isSaveEnabled =
            false

        voiceSearch.attach(editSearch)

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

        val initialSearchQuery =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUERY
            ) ?: ""

        advancedObjectMatch =
            intent.getBooleanExtra(
                SearchConfiguration.EXTRA_ADVANCED_OBJECT_MATCH,
                false
            )

        displaySearchText =
            SearchConfiguration.locationHighlightQuery(
                initialSearchQuery
            ).ifBlank {
                initialSearchQuery
            }

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

        val boxRepo =
            BoxRepositoryImpl(db.boxDao())

        val familyDelete =
            FamilyDeleteProvider.create(
                db,
                boxRepo,
                objectRepo
            )

        objectViewModel =
            ViewModelProvider(
                this,
                ObjectViewModelFactory(objectRepo, familyDelete)
            )[ObjectViewModel::class.java]

        boxViewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {

                        return BoxViewModel(
                            boxRepo,
                            objectRepo,
                            familyDelete
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

        applyIncomingObjectSearch()
        contextCard.setOnClickListener {

            editSearch.setText("")

            objectViewModel.filter("")

            objectViewModel.clearSelection()

            updateObjectsTitle()

            adapter.updateQuery("")

            adapter.updateFilterState(false)

            hideKeyboard(editSearch)

            contextCard.visibility =
                View.GONE
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

                FeedbackUtils.alert(this)

                contextCard.visibility =
                    View.VISIBLE

                textContextMessage.text =
                    "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."

                return@setOnClickListener
            }

            DialogUtils.showDeleteConfirmation(
                context = this
            ) {

                objectViewModel.deleteObjects(
                    ids,
                    CreatedByResolver.current(this@BoxDetailActivity)
                )
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

                    if (ignoreObjectSearchChanges) {
                        return
                    }

                    applyObjectSearch(
                        s.toString(),
                        intent.getBooleanExtra(
                            SearchConfiguration.EXTRA_ADVANCED_OBJECT_MATCH,
                            false
                        )
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
        setupBottomNav()

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

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {

        ignoreObjectSearchChanges =
            true

        super.onRestoreInstanceState(
            savedInstanceState
        )

        ignoreObjectSearchChanges =
            false

        applyIncomingObjectSearch()
    }

    private fun applyIncomingObjectSearch() {

        if (displaySearchText.isBlank()) {
            return
        }

        ignoreObjectSearchChanges =
            true

        editSearch.setText(
            displaySearchText
        )

        ignoreObjectSearchChanges =
            false

        applyObjectSearch(
            displaySearchText,
            advancedObjectMatch
        )
    }

    private fun applyObjectSearch(
        query: String,
        advancedObjectMatch: Boolean
    ) {

        val incomingTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUERY
            ) ?: ""

        val originalQuestion =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            ) ?: ""

        val visibleTerms =
            SearchConfiguration.locationHighlightQuery(
                incomingTerms
            )

        val keepAdvanced =
            advancedObjectMatch &&
                    incomingTerms.isNotBlank() &&
                    (
                            query == incomingTerms ||
                                    query == visibleTerms ||
                                    query == originalQuestion
                            )

        if (keepAdvanced) {

            objectViewModel.filterByWholeWords(
                incomingTerms
            )

            adapter.updateQuery(
                incomingTerms,
                wholeWord = true
            )

        } else {

            objectViewModel.filter(
                query
            )

            adapter.updateQuery(
                query,
                wholeWord = false
            )
        }

        adapter.updateFilterState(
            query.isNotBlank()
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
                getString(R.string.common_sort) +
                    if (it) " ▲" else " ▼"
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

            refreshHeader(
                textCategory,
                imageCategoryIcon,
                textPosition,
                textLastModified
            )
        }

        categoryViewModel.categories.observe(this) {

            if (currentBox == null) {
                return@observe
            }

            refreshHeader(
                textCategory,
                imageCategoryIcon,
                textPosition,
                textLastModified
            )
        }
    }

    private fun refreshHeader(
        textCategory: TextView,
        imageCategoryIcon: ImageView,
        textPosition: TextView,
        textLastModified: TextView
    ) {

        val box =
            currentBox
                ?: return

        currentCategory =
            categoryViewModel.categories.value?.find {
                it.id == box.categoryId
            }

        updateHeader(
            textCategory,
            imageCategoryIcon,
            textPosition,
            textLastModified
        )
    }
    private fun initViews() {

        textSubtitle =
            findViewById(R.id.textSubtitle)


    }

    private fun setupViewOutputActions() {

        val container =
            findViewById<FrameLayout>(
                R.id.headerActionContainer
            ) ?: return

        outputController =
            ViewOutputController(
                this,
                ViewExportPersister(this),
                showFolderInaccessible = {
                    showOutputMessage(
                        BackupConfiguration.folderInaccessible(this)
                    )
                },
                launchFolderPicker = {
                    exportFolderPicker.launch(null)
                }
            )

        outputController.inflateActions(
            container,
            onPrint = {
                handlePrintView()
            },
            onExport = {
                handleExportView()
            }
        )
    }

    private fun handlePrintView() {

        val snapshot =
            loadObjectSnapshot()
                ?: return

        outputController.print(
            snapshot,
            objectPrintHeader(snapshot)
        )
    }

    private fun handleExportView() {

        val snapshot =
            loadObjectSnapshot()
                ?: return

        outputController.export(snapshot)
    }

    private fun loadObjectSnapshot():
            ContainerViewSnapshot? {

        val box =
            currentBox
                ?: return null

        val objects =
            objectViewModel.objects.value
                ?: emptyList()

        if (objects.isEmpty()) {

            showOutputMessage(
                SearchConfiguration.MSG_NO_RESULTS
            )
            return null
        }

        val category =
            currentCategory

        val iconRes =
            if (category == null || category.icon.isBlank()) {
                0
            } else {
                IconMapper.getIconRes(category.icon)
            }

        return ContainerViewSnapshotFactory.fromBoxContents(
            box,
            category?.name.orEmpty(),
            iconRes,
            objects
        )
    }

    private fun objectPrintHeader(
        snapshot: ContainerViewSnapshot
    ): ViewPrintHeader {

        val boxName =
            currentBox?.name.orEmpty()

        return ViewPrintHeader(
            title = ViewOutputConfiguration.objectsInBoxTitle(
                this,
                boxName
            ),
            filterLine = ViewOutputConfiguration.filterLine(
                this,
                editSearch.text.toString().trim()
            ),
            countLine = ViewOutputConfiguration.countObjects(
                this,
                snapshot.objectCount
            ),
            showBlockSubtotals = false
        )
    }

    private fun showOutputMessage(
        message: String
    ) {

        UiUtils.showContextMessage(
            contextCard,
            textContextMessage,
            message
        )
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

        val dialogViews =
            DialogUtils.createBoxDialog(
                context = this,
                categories = categories,
                timestamp =
                    System.currentTimeMillis()
            )

        val dialog =
            DialogUtils.createBoxConfirmDialog(
                context = this,
                view = dialogViews.view
            )

        DialogUtils.setupDialogConfirmButton(
            dialog = dialog
        ) {

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

                return@setupDialogConfirmButton
            }

            val category =
                dialogViews.spinner.selectedItem
                        as CategoryEntity

            lifecycleScope.launch {

                val newBoxId =
                    boxViewModel.addBoxAndReturnId(
                        boxName,
                        category.id,
                        dialogViews.position.selectedItem
                            .toString(),
                        CreatedByResolver.current(this@BoxDetailActivity)
                    )

                objectViewModel.moveObjects(
                    newBoxId
                )
            }

            dialog.dismiss()
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

        val dialogViews =
            DialogUtils.createObjectDialog(
                context = this,
                layout = R.layout.dialog_edit_object,
                nameValue = item.typeName,
                descriptionValue =
                    item.obj.description,
                quantityValue =
                    item.obj.quantity
            )

        val dialog =
            AlertDialog.Builder(this)
                .setTitle(
                    getString(R.string.dialog_edit_object)
                )
                .setView(
                    dialogViews.view
                )
                .setPositiveButton(
                    getString(R.string.common_save),
                    null
                )
                .setNegativeButton(
                    getString(R.string.common_cancel),
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

                objectViewModel.updateObjectWithName(
                    id,
                    name,
                    item.obj.boxId,
                    dialogViews.description.text
                        .toString()
                        .ifBlank { null },
                    dialogViews.quantity.text
                        .toString()
                        .toIntOrNull()
                )

                dialog.dismiss()
            }

            dialogViews.name
                .addTextChangedListener(

                    object : TextWatcher {

                        override fun afterTextChanged(
                            s: Editable?
                        ) {

                            dialogViews.errorText
                                .visibility =
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
                .setTitle(getString(R.string.dialog_new_object))
                .setView(dialogViews.view)
                .setPositiveButton(
                    getString(R.string.common_add),
                    null
                )
                .setNegativeButton(
                    getString(R.string.common_cancel),
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
                        .toIntOrNull(),
                    CreatedByResolver.current(this@BoxDetailActivity)
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