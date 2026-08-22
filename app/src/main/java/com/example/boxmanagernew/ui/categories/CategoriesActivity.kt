package com.example.boxmanagernew.ui.categories

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.ui.common.*
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import com.example.boxmanagernew.viewoutput.model.NameListStyle
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.example.boxmanagernew.viewoutput.ui.ViewOutputController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class CategoriesActivity : BaseActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter
    private lateinit var contextCard: MaterialCardView
    private lateinit var layoutSearchSort: View
    private lateinit var textContextMessage: TextView
    private lateinit var editSearch: EditText
    private lateinit var buttonSort: Button
    private lateinit var textCategoryCount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton

    private lateinit var outputController: ViewOutputController

    private val voiceSearch =
        VoiceSearchController(this)

    private var ignoreSearchChanges =
        false

    private val iconNames = listOf(
        "outline_checkroom_24","outline_fastfood_24","outline_handyman_24",
        "outline_carpenter_24","outline_ink_pen_24","outline_garage_money_24",
        "outline_passport_24","outline_broadcast_on_home_24","outline_tools_power_drill_24",
        "outline_photo_frame_24","outline_library_music_24","outline_box_24",
        "outline_menu_book_24","outline_medical_services_24","outline_money_bag_24",
        "outline_browse_24"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        setupEdgeToEdge()
        setupTopBar()
        setupViews()
        setupPrintAction()

        BottomNavManager.setup(this, BottomNavManager.TAB_CATEGORIES)
        setupBackNavigation()

        val db =
            DatabaseProvider.getDatabase(applicationContext)

        val repository =
            CategoryRepositoryImpl(
                db.categoryDao(),
                db.boxDao()
            )

        setupViewModel(repository)
        setupAdapter()
        observeData()
        setupListeners()
        showDefaultBar()

        if (savedInstanceState == null) {

            applyIncomingSearch()

        } else {

            restoreIncomingSearch()
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

        restoreIncomingSearch()
    }

    private fun setupViews() {
        setupPageHeader(
            "Categorie",
            "Classificazione Contenitori"
        )

        contextCard =
            findViewById(R.id.contextCard)

        layoutSearchSort =
            findViewById(R.id.layoutSearchSort)

        textContextMessage =
            findViewById(R.id.textContextMessage)

        editSearch =
            findViewById(R.id.editTextSearchCategory)

        voiceSearch.attach(editSearch)

        buttonSort =
            findViewById(R.id.buttonSortCategory)

        textCategoryCount =
            findViewById(R.id.textCategoryCount)

        recyclerView =
            findViewById(R.id.recyclerViewCategories)

        fabAdd =
            findViewById(R.id.fabAddCategory)
    }

    private fun setupPrintAction() {

        val container =
            findViewById<FrameLayout>(
                R.id.headerActionContainer
            ) ?: return

        outputController =
            ViewOutputController(
                this,
                ViewExportPersister(this),
                showFolderInaccessible = {},
                launchFolderPicker = {}
            )

        outputController.inflatePrintOnly(
            container
        ) {
            handlePrintView()
        }
    }

    private fun handlePrintView() {

        val categories =
            viewModel.categories.value
                ?: emptyList()

        if (categories.isEmpty()) {
            showWarningMessage(
                SearchConfiguration.MSG_NO_RESULTS
            )
            return
        }

        val snapshot =
            ContainerViewSnapshotFactory.fromCategories(
                categories
            ) { icon ->
                if (icon.isBlank()) {
                    0
                } else {
                    IconMapper.getIconRes(icon)
                }
            }

        outputController.print(
            snapshot,
            ViewPrintHeader(
                title = ViewOutputConfiguration.PAGE_TITLE_CATEGORIES,
                filterLine = ViewOutputConfiguration.filterLine(
                    editSearch.text.toString().trim()
                ),
                countLine = ViewOutputConfiguration.countCategories(
                    snapshot.boxes.size
                ),
                nameListStyle = NameListStyle.CATEGORY_ICON
            )
        )
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    resetUiState()
                    finish()
                }
            })
    }

    private fun setupViewModel(
        repository: CategoryRepositoryImpl
    ) {

        viewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {

                        return CategoryViewModel(
                            repository
                        ) as T
                    }
                }
            )[CategoryViewModel::class.java]
    }

    private fun setupAdapter() {

        adapter =
            CategoryAdapter(
                emptyList(),
                onEdit = {
                    resetUiState()
                    showEditCategoryDialog(it)
                },
                onDelete = {
                    handleDeleteCategory(it)
                }
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter
    }

    private fun observeData() {

        viewModel.categories.observe(this) {
            adapter.updateData(it)
        }

        viewModel.allCategoriesCount.observe(this) {
            textCategoryCount.text =
                "N. Categorie: $it"
        }

        viewModel.selectedCategory.observe(this) {
            adapter.updateSelection(it)
        }

        viewModel.isAscending.observe(this) {
            UiUtils.updateSortButton(
                buttonSort,
                it
            )
        }
    }

    private fun setupListeners() {

        UiUtils.setupSearchAndSort(
            editSearch,
            buttonSort,
            true,
            { q ->

                if (!ignoreSearchChanges) {

                    applyTypedSearch(q)
                }
            },
            {
                resetUiState()
                viewModel.toggleSort()
            }
        )

        editSearch.setOnEditorActionListener {
                _, id, _ ->

            if (
                id ==
                EditorInfo.IME_ACTION_DONE
            ) {

                hideKeyboardAndClearFocus()
                true

            } else false
        }

        fabAdd.setOnClickListener {
            resetUiState()
            showAddCategoryDialog()
        }

        contextCard.setOnClickListener {
            resetUiState()
        }
    }

    private fun handleDeleteCategory(
        category: Category
    ) {

        lifecycleScope.launch {

            val isUsed =
                viewModel.isCategoryUsed(
                    category.id
                )

            if (isUsed) {

                FeedbackUtils.alert(
                    this@CategoriesActivity
                )

                viewModel.selectCategory(
                    category.id
                )

                showWarningMessage(
                    "Categoria in uso. Eliminazione non consentita"
                )

            } else {

                resetUiState()

                showDeleteDialog(category)
            }
        }
    }

    private fun applyIncomingSearch() {

        intent.getStringExtra(
            "dashboardFilter"
        )?.let { filter ->

            if (
                filter ==
                CategoryViewModel.FILTER_USED
            ) {

                viewModel.filter(filter)
            }
        }

        val locationTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_LOCATION_TERMS
            )

        val originalQuestion =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            )

        if (
            !locationTerms.isNullOrBlank() &&
            !originalQuestion.isNullOrBlank()
        ) {

            ignoreSearchChanges =
                true

            editSearch.setText(
                originalQuestion
            )

            ignoreSearchChanges =
                false

            adapter.updateQuery(
                SearchConfiguration.locationHighlightQuery(
                    locationTerms
                )
            )

            viewModel.filterByBoxLocation(
                locationTerms
            )

            return
        }

        originalQuestion?.let { query ->

            viewModel.filter(query)

            adapter.updateQuery(query)

            ignoreSearchChanges =
                true

            editSearch.setText(query)

            ignoreSearchChanges =
                false
        }
    }

    private fun restoreIncomingSearch() {

        val locationTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_LOCATION_TERMS
            )

        val originalQuestion =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            )

        if (
            locationTerms.isNullOrBlank() ||
            originalQuestion.isNullOrBlank()
        ) {
            return
        }

        ignoreSearchChanges =
            true

        if (
            editSearch.text.toString() !=
            originalQuestion
        ) {

            editSearch.setText(
                originalQuestion
            )
        }

        ignoreSearchChanges =
            false

            adapter.updateQuery(
                SearchConfiguration.locationHighlightQuery(
                    locationTerms
                )
            )

        viewModel.filterByBoxLocation(
            locationTerms
        )
    }

    private fun applyTypedSearch(
        query: String
    ) {

        val locationTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_LOCATION_TERMS
            )

        val originalQuestion =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            )

        if (
            !locationTerms.isNullOrBlank() &&
            query == originalQuestion
        ) {

            viewModel.filterByBoxLocation(
                locationTerms
            )

            adapter.updateQuery(
                SearchConfiguration.locationHighlightQuery(
                    locationTerms
                )
            )

        } else {

            viewModel.filter(query)

            adapter.updateQuery(query)
        }
    }

    private fun resetUiState() {

        viewModel.clearSelection()

        hideKeyboardAndClearFocus()

        showDefaultBar()
    }

    private fun showDefaultBar() {

        layoutSearchSort.visibility =
            View.VISIBLE

        textContextMessage.visibility =
            View.GONE

        contextCard.strokeColor =
            getColor(
                android.R.color.transparent
            )
    }

    private fun showWarningMessage(
        text: String
    ) {

        layoutSearchSort.visibility =
            View.GONE

        textContextMessage.visibility =
            View.VISIBLE

        textContextMessage.text =
            text

        contextCard.strokeColor =
            getColor(
                android.R.color.holo_red_dark
            )
    }

    private fun showAddCategoryDialog() {

        val d =
            createCategoryDialog()

        val dialog =
            createCategoryAlertDialog(
                "Nuova categoria",
                d.view,
                "Aggiungi"
            )

        setupCategoryDialogConfirm(
            dialog,
            d.editName,
            d.iconAdapter,
            d.textError
        ) { name, icon ->

            lifecycleScope.launch {

                handleCategoryDialogResult(
                    viewModel.insert(
                        Category(
                            name = name,
                            icon = icon
                        )
                    ),
                    dialog,
                    d.textError
                )
            }
        }

        dialog.show()
    }

    private fun showEditCategoryDialog(
        category: Category
    ) {

        lifecycleScope.launch {

            if (
                viewModel.isCategoryUsed(
                    category.id
                )
            ) {

                FeedbackUtils.alert(
                    this@CategoriesActivity
                )

                showWarningMessage(
                    "Categoria in uso: modificandola, i contenitori verranno aggiornati. Tocca qui per annullare."
                )
            }

            val d =
                createCategoryDialog(
                    category
                )

            val dialog =
                createCategoryAlertDialog(
                    "Modifica categoria",
                    d.view,
                    "Salva"
                )

            dialog.setOnDismissListener {
                resetUiState()
            }

            setupCategoryDialogConfirm(
                dialog,
                d.editName,
                d.iconAdapter,
                d.textError
            ) { name, icon ->

                lifecycleScope.launch {

                    handleCategoryDialogResult(
                        viewModel.update(
                            category.copy(
                                name = name,
                                icon = icon
                            )
                        ),
                        dialog,
                        d.textError
                    )
                }
            }

            dialog.show()
        }
    }

    private fun createCategoryDialog(
        category: Category? = null
    ): CategoryDialogViews {

        val view =
            layoutInflater.inflate(
                R.layout.dialog_add_category,
                null
            )

        val edit =
            view.findViewById<EditText>(
                R.id.editCategoryName
            )

        val recycler =
            view.findViewById<RecyclerView>(
                R.id.recyclerIcons
            )

        val error =
            view.findViewById<TextView>(
                R.id.textError
            )

        val iconAdapter =
            IconAdapter(iconNames){}

        recycler.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recycler.adapter =
            iconAdapter

        category?.let {

            edit.setText(it.name)

            iconAdapter.setSelectedIcon(
                it.icon
            )
        }

        return CategoryDialogViews(
            view,
            edit,
            error,
            iconAdapter
        )
    }

    private fun createCategoryAlertDialog(
        title:String,
        view:View,
        positive:String
    ): AlertDialog =
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton(
                "Annulla",
                null
            )
            .setPositiveButton(
                positive,
                null
            )
            .create()

    private fun setupCategoryDialogConfirm(
        dialog: AlertDialog,
        editName: EditText,
        iconAdapter: IconAdapter,
        textError: TextView,
        onValid:(String,String)->Unit
    ) {

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val name =
                    editName.text
                        .toString()
                        .trim()

                val icon =
                    iconAdapter.getSelectedIcon()

                textError.visibility =
                    View.GONE

                if (
                    name.isEmpty()
                ) {

                    FeedbackUtils.alert(this)

                    textError.text =
                        "Dato obbligatorio"

                    textError.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                if (
                    icon == null
                ) {

                    FeedbackUtils.alert(this)

                    textError.text =
                        "Seleziona un'icona"

                    textError.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                onValid(
                    name,
                    icon
                )
            }
        }
    }

    private fun handleCategoryDialogResult(
        success:Boolean,
        dialog:AlertDialog,
        textError:TextView
    ){

        if(success){

            resetUiState()

            dialog.dismiss()

        } else {

            textError.text =
                "Categoria già esistente"

            textError.visibility =
                View.VISIBLE
        }
    }

    private fun showDeleteDialog(
        category: Category
    ) {

        DialogUtils.showDeleteConfirmation(
            this
        ) {

            lifecycleScope.launch {

                viewModel.delete(category)

                resetUiState()
            }
        }
    }

    private data class CategoryDialogViews(
        val view: View,
        val editName: EditText,
        val textError: TextView,
        val iconAdapter: IconAdapter
    )
}