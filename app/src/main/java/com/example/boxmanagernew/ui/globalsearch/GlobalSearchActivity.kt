package com.example.boxmanagernew.ui.globalsearch

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class GlobalSearchActivity : BaseActivity() {

    private lateinit var viewModel: GlobalSearchViewModel

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_global_search
        )

        setupEdgeToEdge()

        setupTopBar()

        setupPageHeader(
            title = getString(
                R.string.global_search_title
            ),
            subtitle = getString(
                R.string.global_search_subtitle
            )
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_DASHBOARD
        )

        viewModel =
            ViewModelProvider(this)[
                GlobalSearchViewModel::class.java
            ]

        val recycler =
            findViewById<RecyclerView>(
                R.id.recyclerMessages
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        viewModel.messages.observe(this) {

            recycler.adapter =
                GlobalSearchAdapter(it)
        }

        viewModel.setMessages(
            listOf(
                "Dove ho messo il trapano?",
                "Il trapano si trova nel contenitore Garage 1."
            )
        )
    }
}