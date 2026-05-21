package com.example.boxmanagernew.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.utility.UtilityActivity

class DashboardActivity : BaseActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

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

            Toast.makeText(
                this,
                "Filtro contenitori vuoti: prossimo step",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<LinearLayout>(
            R.id.layoutUsedCategories
        ).setOnClickListener {

            Toast.makeText(
                this,
                "Filtro categorie in uso: prossimo step",
                Toast.LENGTH_SHORT
            ).show()
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

        val textBoxes =
            findViewById<TextView>(
                R.id.textBoxesCount
            )

        val textCategories =
            findViewById<TextView>(
                R.id.textCategoriesCount
            )

        val textEmptyBoxes =
            findViewById<TextView>(
                R.id.textEmptyBoxes
            )

        val textUsedCategories =
            findViewById<TextView>(
                R.id.textUsedCategories
            )

        db.boxDao()
            .getAllLive()
            .observe(this) {

                textBoxes.text =
                    it.size.toString()
            }

        db.categoryDao()
            .getAllCategories()
            .observe(this) {

                textCategories.text =
                    it.size.toString()
            }

        db.boxDao()
            .getEmptyBoxesCount()
            .observe(this) {

                textEmptyBoxes.text =
                    it.toString()
            }

        db.boxDao()
            .getUsedCategoriesCount()
            .observe(this) {

                textUsedCategories.text =
                    it.toString()
            }
    }
}