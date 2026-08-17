package com.example.boxmanagernew.ui.globalsearch

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.model.SearchFulcrum
import com.example.boxmanagernew.domain.search.model.SearchMessage
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.ui.categories.CategoriesActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager

class GlobalSearchActivity : BaseActivity() {

    private lateinit var viewModel: GlobalSearchViewModel
    private lateinit var editQuestion: EditText

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

        editQuestion =
            findViewById(R.id.editQuestion)

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

        editQuestion.setOnEditorActionListener {
                _, actionId, _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_DONE
            ) {

                submitQuestion()
                true

            } else {

                false
            }
        }

        if (savedInstanceState == null) {

            editQuestion.setText(
                initialQuery
            )

            viewModel.clear()

            if (initialQuery.isNotBlank()) {
                submitQuestion()
            }
        }
    }

    private fun submitQuestion() {

        val question =
            editQuestion.text
                .toString()
                .trim()

        if (question.isBlank()) {
            return
        }

        viewModel.addMessage(
            SearchMessage(
                text = question,
                fromUser = true
            )
        )

        val response =
            dispatcher.dispatch(
                question
            )

        when {

            response.requiresClarification -> {
                showReply(response.message)
            }

            response.success &&
                    response.dominantFulcrum != null -> {

                openPredefinedList(
                    response,
                    question
                )
            }

            else -> {
                showReply(response.message)
            }
        }

        editQuestion.setText("")
    }

    private fun showReply(
        text: String
    ) {

        val visible =
            if (
                text.startsWith("[") ||
                text.contains("LOOKUP") ||
                text.contains("ENGINE_")
            ) {
                SearchConfiguration.MSG_NOT_UNDERSTOOD
            } else {
                text
            }

        viewModel.addMessage(
            SearchMessage(
                text = visible,
                fromUser = false
            )
        )
    }

    private fun openPredefinedList(
        response: SearchResponse,
        originalQuestion: String
    ) {

        val objectTerms =
            response.operationalQuery
                ?.trim()
                .orEmpty()

        if (
            objectTerms.isBlank()
        ) {

            showReply(
                SearchConfiguration.MSG_NO_RESULTS
            )

            return
        }

        val target =
            when (response.dominantFulcrum) {

                SearchFulcrum.OBJECT,
                SearchFulcrum.BOX,
                SearchFulcrum.LOCATION ->
                    MainActivity::class.java

                SearchFulcrum.CATEGORY ->
                    CategoriesActivity::class.java

                null ->
                    return
            }

        startActivity(
            Intent(this, target).apply {

                val isObjectList =
                    response.dominantFulcrum ==
                            SearchFulcrum.OBJECT ||
                            response.dominantFulcrum ==
                            SearchFulcrum.BOX

                putExtra(
                    SearchConfiguration.EXTRA_SEARCH_QUESTION,
                    if (isObjectList) {
                        originalQuestion
                    } else {
                        objectTerms
                    }
                )

                if (isObjectList) {

                    putExtra(
                        SearchConfiguration.EXTRA_OBJECT_TERMS,
                        objectTerms
                    )
                }
            }
        )

        finish()
    }
}
