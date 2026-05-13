package com.example.boxmanagernew.ui.categories

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
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
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.UiUtils
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

    private val iconNames = listOf(
        "outline_checkroom_24",
        "outline_fastfood_24",
        "outline_handyman_24",
        "outline_carpenter_24",
        "outline_ink_pen_24",
        "outline_garage_money_24",
        "outline_passport_24",
        "outline_broadcast_on_home_24",
        "outline_tools_power_drill_24",
        "outline_photo_frame_24",
        "outline_library_music_24",
        "outline_box_24",
        "outline_menu_book_24",
        "outline_medical_services_24",
        "outline_money_bag_24",
        "outline_browse_24"
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_categories
        )

        setupEdgeToEdge()

        setupTopBar()

        setupViews()

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_CATEGORIES
        )

        setupBackNavigation()

        val db =
            DatabaseProvider.getDatabase(
                applicationContext
            )

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
    }

    private fun setupViews() {

        setupPageHeader(
            title = "Categorie",
            subtitle = "Classificazione Contenitori"
        )

        contextCard =
            findViewById(R.id.contextCard)

        layoutSearchSort =
            findViewById(R.id.layoutSearchSort)

        textContextMessage =
            findViewById(R.id.textContextMessage)

        editSearch =
            findViewById(R.id.editTextSearchCategory)

        buttonSort =
            findViewById(R.id.buttonSortCategory)

        textCategoryCount =
            findViewById(R.id.textCategoryCount)

        recyclerView =
            findViewById(R.id.recyclerViewCategories)

        fabAdd =
            findViewById(R.id.fabAddCategory)
    }

    private fun setupBackNavigation() {

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    resetUiState()

                    finish()
                }
            }
        )
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
                items = emptyList(),

                onEdit = { category ->

                    resetUiState()

                    showEditCategoryDialog(category)
                },

                onDelete = { category ->

                    handleDeleteCategory(category)
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
            search = editSearch,
            sortButton = buttonSort,
            isAscending = true,

            onSearchChanged = { query ->

                viewModel.filter(query)

                adapter.updateQuery(query)
            },

            onSortClicked = {

                resetUiState()

                viewModel.toggleSort()
            }
        )

        editSearch.setOnEditorActionListener {
                _,
                actionId,
                _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                hideKeyboardAndClearFocus()

                true

            } else {

                false
            }
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

                viewModel.selectCategory(
                    category.id
                )

                showWarningMessage(
                    "Categoria in uso: eliminazione non consentita. Tocca qui per annullare."
                )

            } else {

                resetUiState()

                showDeleteDialog(category)
            }
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
            getColor(android.R.color.transparent)
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

        val dialogViews =
            createCategoryDialog()

        val dialog =
            createCategoryAlertDialog(
                title = "Nuova categoria",
                view = dialogViews.view,
                positiveText = "Aggiungi"
            )

        setupCategoryDialogConfirm(
            dialog = dialog,
            editName = dialogViews.editName,
            iconAdapter = dialogViews.iconAdapter,
            textError = dialogViews.textError
        ) { name, icon ->

            lifecycleScope.launch {

                val success =
                    viewModel.insert(
                        Category(
                            name = name,
                            icon = icon
                        )
                    )

                handleCategoryDialogResult(
                    success = success,
                    dialog = dialog,
                    textError = dialogViews.textError
                )
            }
        }

        dialog.show()
    }

    private fun showEditCategoryDialog(
        category: Category
    ) {

        lifecycleScope.launch {

            val isUsed =
                viewModel.isCategoryUsed(
                    category.id
                )

            if (isUsed) {

                showWarningMessage(
                    "Categoria in uso: modificandola, i contenitori verranno aggiornati. Tocca qui per annullare."
                )
            }

            val dialogViews =
                createCategoryDialog(
                    category
                )

            val dialog =
                createCategoryAlertDialog(
                    title = "Modifica categoria",
                    view = dialogViews.view,
                    positiveText = "Salva"
                )

            dialog.setOnDismissListener {

                resetUiState()
            }

            setupCategoryDialogConfirm(
                dialog = dialog,
                editName = dialogViews.editName,
                iconAdapter = dialogViews.iconAdapter,
                textError = dialogViews.textError
            ) { name, icon ->

                lifecycleScope.launch {

                    val success =
                        viewModel.update(
                            category.copy(
                                name = name,
                                icon = icon
                            )
                        )

                    handleCategoryDialogResult(
                        success = success,
                        dialog = dialog,
                        textError = dialogViews.textError
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

        val editName =
            view.findViewById<EditText>(
                R.id.editCategoryName
            )

        val recyclerIcons =
            view.findViewById<RecyclerView>(
                R.id.recyclerIcons
            )

        val textError =
            view.findViewById<TextView>(
                R.id.textError
            )

        val iconAdapter =
            IconAdapter(iconNames) {}

        recyclerIcons.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        recyclerIcons.adapter =
            iconAdapter

        if (category != null) {

            editName.setText(
                category.name
            )

            iconAdapter.setSelectedIcon(
                category.icon
            )
        }

        return CategoryDialogViews(
            view = view,
            editName = editName,
            textError = textError,
            iconAdapter = iconAdapter
        )
    }

    private fun createCategoryAlertDialog(
        title: String,
        view: View,
        positiveText: String
    ): AlertDialog {

        return AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton(
                "Annulla",
                null
            )
            .setPositiveButton(
                positiveText,
                null
            )
            .create()
    }

    private fun setupCategoryDialogConfirm(
        dialog: AlertDialog,
        editName: EditText,
        iconAdapter: IconAdapter,
        textError: TextView,
        onValid: (
            name: String,
            icon: String
        ) -> Unit
    ) {

        dialog.setOnShowListener {

            val button =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            button.setOnClickListener {

                val name =
                    editName.text
                        .toString()
                        .trim()

                val selectedIcon =
                    iconAdapter.getSelectedIcon()

                textError.visibility =
                    View.GONE

                if (name.isEmpty()) {

                    textError.text =
                        "Dato obbligatorio"

                    textError.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                if (selectedIcon == null) {

                    textError.text =
                        "Seleziona un'icona"

                    textError.visibility =
                        View.VISIBLE

                    return@setOnClickListener
                }

                onValid(
                    name,
                    selectedIcon
                )
            }
        }
    }

    private fun handleCategoryDialogResult(
        success: Boolean,
        dialog: AlertDialog,
        textError: TextView
    ) {

        if (success) {

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
            context = this
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