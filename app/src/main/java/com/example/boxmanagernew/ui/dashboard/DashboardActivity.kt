package com.example.boxmanagernew.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.CategoryViewModel
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.example.boxmanagernew.ui.utility.UtilityActivity

class DashboardActivity : BaseActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_dashboard
        )

        setupEdgeToEdge()

        setupTopBar()

        setupPageHeader(
            title = "Dashboard",
            subtitle = "Panoramica Archivio"
        )

        setupDashboardActions()

        setupSearch()

        loadKpiData()

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_DASHBOARD
        )
    }

    private fun setupSearch() {

        val editSearch =
            findViewById<EditText>(
                R.id.editSearch
            )

        val spinner =
            findViewById<Spinner>(
                R.id.spinnerSearchScope
            )

        editSearch.setOnEditorActionListener {
                _, actionId, _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                val query =
                    editSearch.text
                        .toString()
                        .trim()

                if (
                    query.isBlank()
                ) {

                    return@setOnEditorActionListener true
                }
                editSearch.clearFocus()

                hideKeyboard(editSearch)

                editSearch.setText("")
                when (
                    spinner.selectedItemPosition
                ) {

                    // Tutto archivio (temporaneo)
                    0 -> {

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            ).apply {

                                putExtra(
                                    "dashboardSearchQuery",
                                    query
                                )
                            }
                        )
                    }

                    // Contenitori
                    1 -> {

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            ).apply {

                                putExtra(
                                    "dashboardSearchQuery",
                                    query
                                )
                            }
                        )
                    }

                    // Oggetti
                    2 -> {

                        // Placeholder
                    }

                    // Categorie
                    3 -> {

                        startActivity(
                            Intent(
                                this,
                                CategoriesActivity::class.java
                            ).apply {

                                putExtra(
                                    "dashboardSearchQuery",
                                    query
                                )
                            }
                        )
                    }

                    // Posizione
                    4 -> {

                        startActivity(
                            Intent(
                                this,
                                MainActivity::class.java
                            ).apply {

                                putExtra(
                                    "dashboardSearchQuery",
                                    query
                                )
                            }
                        )
                    }
                }

                true

            } else {

                false
            }
        }
    }

    private fun setupDashboardActions() {

        findViewById<LinearLayout>(
            R.id.openBoxes
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(
            R.id.openCategories
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CategoriesActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(
            R.id.layoutEmptyBoxes
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).apply {

                    putExtra(
                        "dashboardFilter",
                        BoxViewModel.FILTER_EMPTY_BOXES
                    )
                }
            )
        }

        findViewById<LinearLayout>(
            R.id.layoutUsedCategories
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CategoriesActivity::class.java
                ).apply {

                    putExtra(
                        "dashboardFilter",
                        CategoryViewModel.FILTER_USED
                    )
                }
            )
        }

        findViewById<CardView>(
            R.id.cardBackup
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UtilityActivity::class.java
                )
            )
        }

        findViewById<CardView>(
            R.id.cardRestore
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UtilityActivity::class.java
                )
            )
        }

        findViewById<CardView>(
            R.id.cardQr
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UtilityActivity::class.java
                )
            )
        }

        findViewById<CardView>(
            R.id.cardTools
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    UtilityActivity::class.java
                )
            )
        }
    }

    private fun loadKpiData() {

        val db =
            DatabaseProvider.getDatabase(
                applicationContext
            )

        db.boxDao()
            .getAllLive()
            .observe(this) {

                findViewById<TextView>(
                    R.id.textBoxesCount
                ).text =
                    it.size.toString()
            }

        db.categoryDao()
            .getAllCategories()
            .observe(this) {

                findViewById<TextView>(
                    R.id.textCategoriesCount
                ).text =
                    it.size.toString()
            }

        db.boxDao()
            .getEmptyBoxesCount()
            .observe(this) {

                findViewById<TextView>(
                    R.id.textEmptyBoxes
                ).text =
                    it.toString()
            }

        db.boxDao()
            .getUsedCategoriesCount()
            .observe(this) {

                findViewById<TextView>(
                    R.id.textUsedCategories
                ).text =
                    it.toString()
            }
    }
}