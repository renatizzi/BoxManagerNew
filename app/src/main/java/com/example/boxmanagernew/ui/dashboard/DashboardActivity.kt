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
import com.example.boxmanagernew.ui.backup.BackupActivity
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.categories.CategoryViewModel
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.VoiceSearchController
import com.example.boxmanagernew.ui.globalsearch.GlobalSearchActivity
import com.example.boxmanagernew.ui.importdata.ImportActivity
import com.example.boxmanagernew.ui.main.BoxViewModel
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import com.example.boxmanagernew.ui.qr.QRActivity
import com.example.boxmanagernew.ui.restore.RestoreActivity
import com.example.boxmanagernew.ui.search.SearchResultActivity

class DashboardActivity : BaseActivity() {

    private val voiceSearch =
        VoiceSearchController(this)

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_dashboard
        )

        setupAppShell()

        setupPageHeader(
            title = getString(R.string.page_dashboard_title),
            subtitle = getString(R.string.page_dashboard_subtitle)
        )

        setupDashboardActions()

        setupSearch()

        loadKpiData()

        setupBottomNav()
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

        spinner.post {
            spinner.setSelection(0)
        }

        editSearch.setOnEditorActionListener {
                _, actionId, _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                submitDashboardSearch(
                    editSearch,
                    spinner
                )
                true

            } else {

                false
            }
        }

        voiceSearch.attach(editSearch) { _ ->

            submitDashboardSearch(
                editSearch,
                spinner
            )
        }
    }

    private fun submitDashboardSearch(
        editSearch: EditText,
        spinner: Spinner
    ) {

        val query =
            editSearch.text
                .toString()
                .trim()

        val selected =
            spinner.selectedItemPosition

        if (
            query.isBlank() &&
            selected != 1 &&
            selected != 4
        ) {

            return
        }

        editSearch.clearFocus()

        hideKeyboard(editSearch)

        editSearch.setText("")

        spinner.setSelection(0)

        when (selected) {

            0 -> {

                ArchivioCompletoNav.start(
                    this,
                    PremiumFeature.ADVANCED_SEARCH,
                    Intent(
                        this,
                        GlobalSearchActivity::class.java
                    ).apply {

                        putExtra(
                            "dashboardSearchQuery",
                            query
                        )
                    }
                )
            }

            1, 4 -> {

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

            2 -> {

                startActivity(
                    Intent(
                        this,
                        SearchResultActivity::class.java
                    ).apply {

                        putExtra(
                            "dashboardSearchQuery",
                            query
                        )
                    }
                )
            }

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
                    BackupActivity::class.java
                )
            )
        }

        findViewById<CardView>(
            R.id.cardRestore
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    RestoreActivity::class.java
                )
            )
        }

        findViewById<CardView>(
            R.id.cardQr
        ).setOnClickListener {

            ArchivioCompletoNav.start(
                this,
                PremiumFeature.QR_SCAN,
                Intent(
                    this,
                    QRActivity::class.java
                )
            )
        }

        findViewById<CardView>(
            R.id.cardTools
        ).setOnClickListener {

            ArchivioCompletoNav.start(
                this,
                PremiumFeature.IMPORT,
                Intent(
                    this,
                    ImportActivity::class.java
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