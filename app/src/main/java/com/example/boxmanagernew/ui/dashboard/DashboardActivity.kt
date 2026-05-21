package com.example.boxmanagernew.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
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

        loadKpiData()

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_DASHBOARD
        )
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