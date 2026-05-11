package com.example.boxmanagernew.ui.categories

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.TopBarUtils
import com.example.boxmanagernew.ui.common.UiUtils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class CategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    private lateinit var contextCard: MaterialCardView
    private lateinit var layoutSearchSort: View
    private lateinit var textContextMessage: TextView

    private lateinit var editSearch: EditText
    private lateinit var buttonSort: Button
    private lateinit var textCategoryCount: TextView

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

        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        setContentView(
            R.layout.activity_categories
        )

        val root =
            findViewById<View>(
                android.R.id.content
            )

        ViewCompat.setOnApplyWindowInsetsListener(
            root
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        TopBarUtils.bindTitle(
            findViewById(R.id.textGlobalTitle)
        )

        TopBarUtils.bindSubtitle(
            this,
            findViewById(R.id.textGlobalSubtitle)
        )

        findViewById<TextView>(
            R.id.textTitle
        ).text =
            "Categorie"

        findViewById<TextView>(
            R.id.textSubtitle
        ).text =
            "Classificazione Contenitori"

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_CATEGORIES
        )

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    resetUiState()

                    finish()
                }
            }
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

        val recyclerView =
            findViewById<RecyclerView>(
                R.id.recyclerViewCategories
            )

        val fabAdd =
            findViewById<FloatingActionButton>(
                R.id.fabAddCategory
            )

        val db =
            DatabaseProvider.getDatabase(
                applicationContext
            )

        val repository =
            CategoryRepositoryImpl(
                db.categoryDao(),
                db.boxDao()
            )

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

        adapter =
            CategoryAdapter(
                items = emptyList(),

                onEdit = { category ->

                    resetUiState()

                    showEditCategoryDialog(category)
                },

                onDelete = { category ->

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
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            adapter

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

        editSearch.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(
                    s: Editable?
                ) {

                    viewModel.clearSelection()

                    showDefaultBar()

                    val query =
                        s.toString()

                    viewModel.filter(query)

                    adapter.updateQuery(query)
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

        editSearch.setOnEditorActionListener {
                _,
                actionId,
                _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                hideKeyboard()

                editSearch.clearFocus()

                true

            } else {

                false
            }
        }

        buttonSort.setOnClickListener {

            resetUiState()

            hideKeyboard()

            editSearch.clearFocus()

            viewModel.toggleSort()
        }

        fabAdd.setOnClickListener {

            resetUiState()

            showAddCategoryDialog()
        }

        contextCard.setOnClickListener {

            resetUiState()
        }

        showDefaultBar()
    }

    private fun resetUiState() {

        viewModel.clearSelection()

        hideKeyboard()

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

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Nuova categoria")
                .setView(view)
                .setNegativeButton(
                    "Annulla",
                    null
                )
                .setPositiveButton(
                    "Aggiungi",
                    null
                )
                .create()

        dialog.setOnShowListener {

            val btnAdd =
                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                )

            btnAdd.setOnClickListener {

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

                lifecycleScope.launch {

                    val success =
                        viewModel.insert(
                            Category(
                                name = name,
                                icon = selectedIcon
                            )
                        )

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

            editName.setText(category.name)

            val iconAdapter =
                IconAdapter(iconNames) {}

            iconAdapter.setSelectedIcon(
                category.icon
            )

            recyclerIcons.layoutManager =
                LinearLayoutManager(
                    this@CategoriesActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )

            recyclerIcons.adapter =
                iconAdapter

            val dialog =
                AlertDialog.Builder(this@CategoriesActivity)
                    .setTitle("Modifica categoria")
                    .setView(view)
                    .setNegativeButton(
                        "Annulla"
                    ) { _, _ ->

                        resetUiState()
                    }
                    .setPositiveButton(
                        "Salva",
                        null
                    )
                    .create()

            dialog.setOnShowListener {

                val btnSave =
                    dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                    )

                btnSave.setOnClickListener {

                    val newName =
                        editName.text
                            .toString()
                            .trim()

                    val selectedIcon =
                        iconAdapter.getSelectedIcon()

                    textError.visibility =
                        View.GONE

                    if (newName.isEmpty()) {

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

                    lifecycleScope.launch {

                        val success =
                            viewModel.update(
                                category.copy(
                                    name = newName,
                                    icon = selectedIcon
                                )
                            )

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
                }
            }

            dialog.show()
        }
    }

    private fun showDeleteDialog(
        category: Category
    ) {

        AlertDialog.Builder(this)
            .setMessage(
                "Conferma eliminazione?"
            )
            .setPositiveButton(
                "SI"
            ) { _, _ ->

                lifecycleScope.launch {

                    viewModel.delete(category)

                    resetUiState()
                }
            }
            .setNegativeButton(
                "NO"
            ) { _, _ ->

                resetUiState()
            }
            .show()
    }

    override fun dispatchTouchEvent(
        ev: MotionEvent
    ): Boolean {

        if (currentFocus != null) {

            hideKeyboard()

            currentFocus?.clearFocus()
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboard() {

        val imm =
            getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        currentFocus?.let {

            imm.hideSoftInputFromWindow(
                it.windowToken,
                0
            )
        }
    }
}