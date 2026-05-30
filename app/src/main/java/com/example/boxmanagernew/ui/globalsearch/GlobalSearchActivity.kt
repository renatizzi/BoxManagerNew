package com.example.boxmanagernew.ui.globalsearch

import android.graphics.Typeface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.search.model.SearchMessage
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

        val editQuestion =
            findViewById<EditText>(
                R.id.editQuestion
            )

        editQuestion.setTypeface(
            null,
            Typeface.ITALIC
        )

        editQuestion.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.primary_button
            )
        )

        val initialQuery =
            intent.getStringExtra(
                "dashboardSearchQuery"
            ) ?: ""

        editQuestion.setText(
            initialQuery
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

        viewModel.clear()

        editQuestion.setOnEditorActionListener {
                _, actionId, _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                val question =
                    editQuestion.text
                        .toString()
                        .trim()

                if (
                    question.isBlank()
                ) {

                    return@setOnEditorActionListener true
                }

                viewModel.addMessage(
                    SearchMessage(
                        text = question,
                        fromUser = true
                    )
                )

                editQuestion.setText("")

                true

            } else {

                false
            }
        }
    }
}