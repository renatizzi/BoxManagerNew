package com.example.boxmanagernew.ui.globalsearch

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.model.SearchMessage
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.search.SearchResultActivity

class GlobalSearchActivity : BaseActivity() {

    private lateinit var viewModel: GlobalSearchViewModel

    private val dispatcher =
        GlobalSearchDispatcher()

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

                Toast.makeText(
                    this,
                    "FASE A OK - Conferma intercettata",
                    Toast.LENGTH_SHORT
                ).show()

                viewModel.addMessage(
                    SearchMessage(
                        text = question,
                        fromUser = true
                    )
                )

                Toast.makeText(
                    this,
                    "FASE B OK - Dispatcher chiamato",
                    Toast.LENGTH_SHORT
                ).show()

                val response =
                    dispatcher.dispatch(
                        question
                    )

                Toast.makeText(
                    this,
                    "DISPATCH => success=${response.success} message=${response.message}",
                    Toast.LENGTH_LONG
                ).show()

                if (
                    response.success &&
                    response.message ==
                    "ENGINE_A_RESULT"
                ) {

                    startActivity(
                        Intent(
                            this,
                            SearchResultActivity::class.java
                        ).apply {

                            putExtra(
                                "dashboardSearchQuery",
                                response.operationalQuery
                                    ?: question
                            )
                        }
                    )

                } else {

                    viewModel.addMessage(
                        SearchMessage(
                            text = response.message,
                            fromUser = false
                        )
                    )
                }

                editQuestion.setText("")

                true

            } else {

                false
            }
        }
    }
}