package com.example.boxmanagernew.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

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

        findViewById<TextView>(
            R.id.textTitle
        ).text =
            "Dashboard"

        findViewById<TextView>(
            R.id.textSubtitle
        ).text =
            "Panoramica Archivio"

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
                "Filtro contenitori vuoti (test)",
                Toast.LENGTH_SHORT
            ).show()
        }

        findViewById<LinearLayout>(
            R.id.layoutUsedCategories
        ).setOnClickListener {

            Toast.makeText(
                this,
                "Filtro categorie in uso (test)",
                Toast.LENGTH_SHORT
            ).show()
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