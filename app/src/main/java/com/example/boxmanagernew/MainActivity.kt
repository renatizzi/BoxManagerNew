package com.example.boxmanagernew

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.model.Object
import com.example.boxmanagernew.domain.model.Location
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.ui.boxdetail.BoxDetailActivity
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.common.VoiceSearchController
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.example.boxmanagernew.ui.qr.QrLabelActivity
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.csv.ViewExportCsvBuilder
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import com.example.boxmanagernew.viewoutput.model.NameListStyle
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.example.boxmanagernew.viewoutput.print.ViewPrintAdapter
import com.example.boxmanagernew.viewoutput.print.ViewPrintPdf
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private lateinit var recyclerViewBoxes: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private var categories: List<CategoryEntity> = emptyList()
    private var locations: List<Location> =
        emptyList()

    private var ignoreSearchChanges =
        false

    private lateinit var objectRepository: ObjectRepositoryImpl
    private lateinit var exportPersister: ViewExportPersister

    private var pendingCsvBytes: ByteArray? = null

    private val exportFolderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri != null) {
                onExportFolderChosen(uri)
            }
        }

    private val voiceSearch =
        VoiceSearchController(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupEdgeToEdge()

        setupTopBar()

        exportPersister =
            ViewExportPersister(this)

        setupViews()

        val db =
            DatabaseProvider.getDatabase(applicationContext)

        val repository =
            BoxRepositoryImpl(db.boxDao())
        objectRepository =
            ObjectRepositoryImpl(
                db.objectDao(),
                db.objectTypeDao()
            )
        setupAdapter()
        setupViewOutputActions()

        setupViewModel(
            repository,
            objectRepository
        )

        observeData(db)

        setupListeners()

        if (savedInstanceState == null) {

            applyIncomingSearch()

        } else {

            restoreAdvancedSearchPresentation()
        }
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {

        ignoreSearchChanges =
            true

        super.onRestoreInstanceState(
            savedInstanceState
        )

        ignoreSearchChanges =
            false

        restoreAdvancedSearchPresentation()
    }

    private fun setupViews() {

        setupPageHeader(
            title = "Contenitori",
            subtitle = "Gestione Contenitori e loro contenuti"
        )

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

        recyclerViewBoxes =
            findViewById(R.id.recyclerViewBoxes)

        fabAdd =
            findViewById(R.id.fabAdd)
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

                onShowQrLabel = { box ->

                    startActivity(
                        Intent(
                            this,
                            QrLabelActivity::class.java
                        ).apply {
                            putExtra("boxId", box.id)
                        }
                    )
                },

                onToggleSelection = {

                    viewModel.toggleSelection(it)
                }
            )

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        recyclerViewBoxes.layoutManager =
            LinearLayoutManager(this)

        recyclerViewBoxes.adapter =
            adapter
    }

    private fun setupViewModel(
        repository: BoxRepositoryImpl,
        objectRepository: ObjectRepositoryImpl
    ) {
        viewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {

                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {
                        return BoxViewModel(
                            repository,
                            objectRepository
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
        db.locationDao()
            .getAllLocations()
            .observe(this) {

                locations =
                    it.map { location ->

                        Location(
                            id = location.id,
                            name = location.name
                        )
                    }
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

            if (hidden) {

                showContextMessage(
                    "Alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
                )

            } else {

                hideContextMessage()
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

        UiUtils.setupSearchAndSort(
            search = editSearch,
            sortButton = buttonSort,
            isAscending = true,

            onSearchChanged = { query ->

                if (!ignoreSearchChanges) {

                    applyTypedSearch(query)
                }
            },

            onSortClicked = {

                viewModel.toggleSort()
            }
        )

        voiceSearch.attach(editSearch)

        setupFab()

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

    private fun applyIncomingSearch() {

        intent.getStringExtra(
            "dashboardFilter"
        )?.let { filter ->

            viewModel.filter(filter)

            adapter.updateQuery("")

            if (
                filter ==
                BoxViewModel.FILTER_EMPTY_BOXES
            ) {

                showContextMessage(
                    "Filtro attivo: contenitori vuoti. Tocca qui per rimuovere."
                )
            }
        }

        applyAdvancedArchiveFilter()
        applySimpleDashboardQuery()
    }

    private fun applyAdvancedArchiveFilter() {

        val objectTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_OBJECT_TERMS
            )

        val locationTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_LOCATION_TERMS
            )

        val categoryTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_CATEGORY_TERMS
            )

        val boxTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_BOX_TERMS
            )

        val highlightTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_HIGHLIGHT_TERMS
            )

        if (
            objectTerms.isNullOrBlank() &&
            locationTerms.isNullOrBlank() &&
            categoryTerms.isNullOrBlank() &&
            boxTerms.isNullOrBlank()
        ) {

            return
        }

        val highlightQuery =
            highlightTerms
                ?.takeIf { terms ->
                    terms.isNotBlank()
                }
                ?: SearchConfiguration.locationHighlightQuery(
                    objectTerms
                        ?: categoryTerms
                        ?: boxTerms
                        ?: locationTerms
                        ?: ""
                )

        if (
            !objectTerms.isNullOrBlank()
        ) {

            adapter.updateQuery(
                highlightQuery
            )

            viewModel.filterByContainedObjects(
                objectTerms
            )

        } else if (
            !categoryTerms.isNullOrBlank()
        ) {

            adapter.updateQuery(
                highlightQuery
            )

            viewModel.filterByCategory(
                categoryTerms,
                locationTerms.orEmpty()
            )

        } else if (
            !boxTerms.isNullOrBlank()
        ) {

            adapter.updateQuery(
                highlightQuery,
                inflect = true
            )

            viewModel.filterByBoxNames(
                boxTerms
            )

        } else if (
            !locationTerms.isNullOrBlank()
        ) {

            adapter.updateQuery(
                highlightQuery
            )

            viewModel.filterByLocation(
                locationTerms
            )
        }
    }

    private fun hasAdvancedArchiveExtras(): Boolean {

        return !intent.getStringExtra(
            SearchConfiguration.EXTRA_OBJECT_TERMS
        ).isNullOrBlank() ||
            !intent.getStringExtra(
                SearchConfiguration.EXTRA_LOCATION_TERMS
            ).isNullOrBlank() ||
            !intent.getStringExtra(
                SearchConfiguration.EXTRA_CATEGORY_TERMS
            ).isNullOrBlank() ||
            !intent.getStringExtra(
                SearchConfiguration.EXTRA_BOX_TERMS
            ).isNullOrBlank()
    }

    private fun applySimpleDashboardQuery() {

        if (
            intent.hasExtra("dashboardFilter")
        ) {
            return
        }

        if (hasAdvancedArchiveExtras()) {
            return
        }

        if (
            !intent.getStringExtra(
                SearchConfiguration.EXTRA_INVENTORY_LIST
            ).isNullOrBlank()
        ) {
            return
        }

        if (
            !intent.hasExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            )
        ) {
            return
        }

        val query =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            ) ?: ""

        ignoreSearchChanges =
            true

        editSearch.setText(query)

        ignoreSearchChanges =
            false

        applyTypedSearch(query)
    }

    private fun setupViewOutputActions() {

        val container =
            findViewById<FrameLayout>(
                R.id.headerActionContainer
            ) ?: return

        val actions =
            LayoutInflater.from(this)
                .inflate(
                    R.layout.layout_header_print_export,
                    container,
                    false
                )

        container.addView(actions)

        actions.findViewById<View>(
            R.id.btnPrintView
        ).setOnClickListener {

            handlePrintView()
        }

        val exportButton =
            actions.findViewById<View>(
                R.id.btnExportView
            )

        val inventoryDrive =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_INVENTORY_LIST
            ).orEmpty()

        if (
            inventoryDrive ==
            SearchConfiguration.INVENTORY_CATEGORY ||
            inventoryDrive ==
            SearchConfiguration.INVENTORY_LOCATION
        ) {

            exportButton.visibility =
                View.GONE

        } else {

            exportButton.setOnClickListener {

                handleExportView()
            }
        }
    }

    private fun handlePrintView() {

        lifecycleScope.launch {

            val snapshot =
                loadViewSnapshot()
                    ?: return@launch

            val header =
                printHeader(snapshot)

            val result =
                withContext(
                    Dispatchers.Default
                ) {

                    ViewPrintPdf.toBytes(
                        this@MainActivity,
                        snapshot,
                        header
                    )
                }

            val printManager =
                getSystemService(
                    Context.PRINT_SERVICE
                ) as? PrintManager
                    ?: return@launch

            try {
                printManager.print(
                    "Stampa",
                    ViewPrintAdapter(
                        result.bytes,
                        result.pageCount
                    ),
                    PrintAttributes.Builder()
                        .setMediaSize(
                            PrintAttributes.MediaSize.ISO_A4
                        )
                        .setColorMode(
                            PrintAttributes.COLOR_MODE_MONOCHROME
                        )
                        .build()
                )
            } catch (_: Exception) {
                return@launch
            }
        }
    }

    private fun handleExportView() {

        lifecycleScope.launch {

            val snapshot =
                loadViewSnapshot()
                    ?: return@launch

            val bytes =
                withContext(
                    Dispatchers.Default
                ) {

                    ViewExportCsvBuilder().build(
                        snapshot
                    )
                }

            pendingCsvBytes =
                bytes

            val saved =
                exportPersister.rememberedFolderUri()

            if (
                saved != null &&
                exportPersister.folderDisplayName(saved) != null
            ) {
                onExportFolderChosen(saved)
            } else {
                exportFolderPicker.launch(null)
            }
        }
    }

    private fun onExportFolderChosen(
        uri: Uri
    ) {

        val bytes = pendingCsvBytes
        if (bytes == null) {
            return
        }

        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: Exception) {
        }

        if (exportPersister.folderDisplayName(uri) == null) {
            pendingCsvBytes = null
            FeedbackUtils.alert(this)
            showContextMessage(
                BackupConfiguration.MSG_FOLDER_INACCESSIBLE
            )
            return
        }

        exportPersister.rememberFolder(uri)

        DialogUtils.showExportFileName(
            this,
            ViewOutputConfiguration.proposedFileName(),
            exists = { fileName ->
                exportPersister.existingFile(uri, fileName) != null
            },
            onSave = { fileName, overwrite ->
                writeExport(
                    uri,
                    bytes,
                    fileName,
                    overwrite
                )
            }
        )
    }

    private fun writeExport(
        uri: Uri,
        bytes: ByteArray,
        fileName: String,
        overwrite: Boolean
    ) {

        val result =
            exportPersister.persist(
                uri,
                fileName,
                bytes,
                overwrite
            )

        pendingCsvBytes = null

        if (result.folderInaccessible) {
            FeedbackUtils.alert(this)
            showContextMessage(
                BackupConfiguration.MSG_FOLDER_INACCESSIBLE
            )
        }
    }

    private fun printHeader(
        snapshot: ContainerViewSnapshot
    ): ViewPrintHeader {

        val inventoryDrive =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_INVENTORY_LIST
            ).orEmpty()

        val filterQuery =
            if (
                hasAdvancedArchiveExtras() ||
                inventoryDrive.isNotBlank()
            ) {
                intent.getStringExtra(
                    SearchConfiguration.EXTRA_SEARCH_QUESTION
                ).orEmpty()
            } else {
                editSearch.text.toString().trim()
            }

        return when (inventoryDrive) {

            SearchConfiguration.INVENTORY_CATEGORY ->
                ViewPrintHeader(
                    title =
                        ViewOutputConfiguration.PAGE_TITLE_CATEGORIES,
                    filterLine =
                        ViewOutputConfiguration.filterLine(
                            filterQuery
                        ),
                    countLine =
                        ViewOutputConfiguration.countCategories(
                            snapshot.boxes.size
                        ),
                    nameListStyle =
                        NameListStyle.CATEGORY_GROUPS
                )

            SearchConfiguration.INVENTORY_LOCATION ->
                ViewPrintHeader(
                    title =
                        ViewOutputConfiguration.PAGE_TITLE_LOCATIONS,
                    filterLine =
                        ViewOutputConfiguration.filterLine(
                            filterQuery
                        ),
                    countLine =
                        ViewOutputConfiguration.countLocations(
                            snapshot.boxes.size
                        ),
                    nameListStyle =
                        NameListStyle.PLACE_GROUPS
                )

            else ->
                ViewPrintHeader(
                    title =
                        ViewOutputConfiguration.PAGE_TITLE,
                    filterLine =
                        ViewOutputConfiguration.filterLine(
                            filterQuery
                        ),
                    countLine =
                        ViewOutputConfiguration.countBoxes(
                            snapshot.boxes.size
                        )
                )
        }
    }

    private suspend fun loadViewSnapshot():
            ContainerViewSnapshot? {

        val boxes =
            viewModel.boxes.value
                ?: emptyList()

        if (boxes.isEmpty()) {

            showContextMessage(
                SearchConfiguration.MSG_NO_RESULTS
            )
            return null
        }

        val inventoryDrive =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_INVENTORY_LIST
            ).orEmpty()

        if (
            inventoryDrive ==
            SearchConfiguration.INVENTORY_CATEGORY
        ) {

            return ContainerViewSnapshotFactory.fromBoxesGroupedByCategory(
                boxes,
                { categoryId ->
                    categories.find { category ->
                        category.id == categoryId
                    }?.name.orEmpty()
                },
                { categoryId ->
                    val icon =
                        categories.find { category ->
                            category.id == categoryId
                        }?.icon.orEmpty()
                    if (icon.isBlank()) {
                        0
                    } else {
                        IconMapper.getIconRes(icon)
                    }
                }
            )
        }

        if (
            inventoryDrive ==
            SearchConfiguration.INVENTORY_LOCATION
        ) {

            return ContainerViewSnapshotFactory.fromBoxesGroupedByLocation(
                boxes
            )
        }

        val objects =
            withContext(
                Dispatchers.IO
            ) {

                objectRepository.objectsInBoxes(
                    boxes.map { box ->
                        box.id
                    }.toSet()
                )
            }

        return ContainerViewSnapshotFactory.from(
            boxes,
            { categoryId ->
                categories.find { category ->
                    category.id == categoryId
                }?.name.orEmpty()
            },
            { categoryId ->
                val icon =
                    categories.find { category ->
                        category.id == categoryId
                    }?.icon.orEmpty()
                if (icon.isBlank()) {
                    0
                } else {
                    IconMapper.getIconRes(icon)
                }
            },
            objects
        )
    }

    private fun restoreAdvancedSearchPresentation() {

        applyAdvancedArchiveFilter()
    }

    private fun applyTypedSearch(
        query: String
    ) {

        if (
            query.isBlank()
        ) {

            val hadAdvanced =
                !intent.getStringExtra(
                    SearchConfiguration.EXTRA_OBJECT_TERMS
                ).isNullOrBlank() ||
                    !intent.getStringExtra(
                        SearchConfiguration.EXTRA_LOCATION_TERMS
                    ).isNullOrBlank() ||
                    !intent.getStringExtra(
                        SearchConfiguration.EXTRA_CATEGORY_TERMS
                    ).isNullOrBlank() ||
                    !intent.getStringExtra(
                        SearchConfiguration.EXTRA_BOX_TERMS
                    ).isNullOrBlank()

            if (hadAdvanced) {

                applyAdvancedArchiveFilter()

            } else {

                viewModel.filter("")

                adapter.updateQuery("", inline = true)
            }

            return
        }

        viewModel.filter(query)

        adapter.updateQuery(query, inline = true)
    }

    private fun setupFab() {

        fabAdd.setOnClickListener {

            showAddDialog()
        }
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

                if (
                    viewModel.isContainedObjectsFilter()
                ) {

                    putExtra(
                        SearchConfiguration.EXTRA_SEARCH_QUERY,
                        viewModel.currentQuery.value ?: ""
                    )

                    putExtra(
                        SearchConfiguration.EXTRA_SEARCH_QUESTION,
                        intent.getStringExtra(
                            SearchConfiguration.EXTRA_SEARCH_QUESTION
                        )
                    )

                    putExtra(
                        SearchConfiguration.EXTRA_ADVANCED_OBJECT_MATCH,
                        true
                    )
                }
            }
        )
    }

    private fun clearFilterAndSelection() {

        editSearch.setText("")

        viewModel.filter("")

        viewModel.clearSelection()

        adapter.updateQuery("")

        hideKeyboard(editSearch)

        hideContextMessage()
    }

    private fun showContextMessage(
        message: String
    ) {

        UiUtils.showContextMessage(
            contextCard,
            textContextMessage,
            message
        )
    }

    private fun hideContextMessage() {

        UiUtils.hideContextMessage(
            contextCard
        )
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

            FeedbackUtils.alert(this)

            showContextMessage(
                "Impossibile eliminare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
            )

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

                DialogUtils.showBoxQrDeleteConfirmation(
                    context = this@MainActivity
                ) {

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
                }

            } else {

                DialogUtils.showBoxQrDeleteConfirmation(
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

            FeedbackUtils.alert(this)

            showContextMessage(
                "Impossibile spostare: alcuni elementi selezionati non sono visibili. Tocca qui per rimuovere il filtro."
            )

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
                locations = locations,
                timestamp = System.currentTimeMillis()
            )
        showBoxDialog(
            dialogViews = dialogViews,
            onConfirm = {

                val boxName =
                    dialogViews.name.text
                        .toString()
                        .trim()

                val category =
                    dialogViews.spinner.selectedItem
                            as CategoryEntity

                lifecycleScope.launch {

                    val newBoxId =
                        viewModel.addBoxAndReturnId(
                            boxName,
                            category.id,
                            (dialogViews.position.selectedItem as Location)
                                .name
                        )

                    moveObjectsAndDeleteBoxes(
                        sourceBoxIds,
                        newBoxId,
                        deleteAfterMove
                    )
                }
            }
        )
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

        val dialogViews =
            DialogUtils.createBoxDialog(
                context = this,
                categories = categories,
                locations = locations,
                timestamp = System.currentTimeMillis()
            )

        showBoxDialog(
            dialogViews = dialogViews,
            onConfirm = {

                val n =
                    dialogViews.name.text
                        .toString()
                        .trim()

                val cat =
                    dialogViews.spinner.selectedItem
                            as CategoryEntity

                viewModel.addBox(
                    n,
                    cat.id,
                    (dialogViews.position.selectedItem as Location)
                        .name
                )
            }
        )
    }

    private fun showEditDialog(
        box: Box
    ) {

        val dialogViews =
            DialogUtils.createBoxDialog(
                context = this,
                categories = categories,
                locations = locations,
                timestamp = box.lastModified,
                box = box
            )

        showBoxDialog(
            dialogViews = dialogViews,
            onConfirm = {

                val n =
                    dialogViews.name.text
                        .toString()
                        .trim()

                val cat =
                    dialogViews.spinner.selectedItem
                            as CategoryEntity

                viewModel.updateBox(
                    box.id,
                    n,
                    cat.id,
                    (dialogViews.position.selectedItem as Location)
                        .name
                 )
            }
        )
    }

    private fun showBoxDialog(
        dialogViews: DialogUtils.BoxDialogViews,
        onConfirm: () -> Unit
    ) {

        val dialog =
            DialogUtils.createBoxConfirmDialog(
                context = this,
                view = dialogViews.view
            )

        DialogUtils.setupDialogConfirmButton(
            dialog = dialog
        ) {

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

                return@setupDialogConfirmButton
            }

            onConfirm()

            dialog.dismiss()
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

                DialogUtils.showBoxQrDeleteConfirmation(
                    context = this@MainActivity
                ) {

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
                }

            } else {

                DialogUtils.showBoxQrDeleteConfirmation(
                    context = this@MainActivity
                ) {

                    viewModel.deleteBox(id)
                }
            }
        }
    }
}