package com.example.boxmanagernew.ui.globalsearch

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.model.SearchArchiveBoxRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchMessage
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalSearchActivity : BaseActivity() {

    private lateinit var viewModel: GlobalSearchViewModel
    private lateinit var editQuestion: EditText
    private lateinit var scrollSearchBody: ScrollView
    private lateinit var recyclerMessages: RecyclerView

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

        scrollSearchBody =
            findViewById(
                R.id.scrollSearchBody
            )

        recyclerMessages =
            findViewById(
                R.id.recyclerMessages
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

        viewModel =
            ViewModelProvider(this)[
                GlobalSearchViewModel::class.java
            ]

        recyclerMessages.layoutManager =
            LinearLayoutManager(this)

        viewModel.messages.observe(this) {

            recyclerMessages.adapter =
                GlobalSearchAdapter(it)

            scrollSearchBody.post {

                scrollSearchBody.fullScroll(
                    ScrollView.FOCUS_DOWN
                )
            }
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

        lifecycleScope.launch {

            val index =
                loadArchiveIndex()

            val response =
                withContext(
                    Dispatchers.Default
                ) {

                    dispatcher.dispatch(
                        question,
                        index
                    )
                }

            if (
                response.message ==
                SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE
            ) {

                showReply(
                    response.message
                )

                return@launch
            }

            if (response.requiresClarification) {

                showReply(
                    response.message
                )

                return@launch
            }

            if (
                response.success &&
                response.requestType ==
                SearchRequestType.ARCHIVE_NAVIGATION
            ) {

                openPipelineList(
                    response,
                    question
                )

                return@launch
            }

            showReply(
                response.message
            )
        }
    }

    private suspend fun loadArchiveIndex():
            SearchArchiveIndex =

        withContext(
            Dispatchers.IO
        ) {

            val db =
                DatabaseProvider.getDatabase(
                    applicationContext
                )

            val objectRows =
                db.objectDao()
                    .searchObjects()

            val boxes =
                db.boxDao()
                    .getAllSync()

            val categoryNameById =
                db.categoryDao()
                    .getAllSync()
                    .associate { category ->
                        category.id to category.name
                    }

            val categoryIdByBoxId =
                boxes.associate { box ->
                    box.id to box.categoryId
                }

            val categoryNameByBoxId =
                boxes.associate { box ->
                    box.id to
                        categoryNameById[
                            box.categoryId
                        ].orEmpty()
                }

            SearchArchiveIndex(
                locations =
                    db.locationDao()
                        .getAllLocationsSync()
                        .map { it.name },
                categories =
                    categoryNameById.values
                        .toList(),
                objects =
                    db.objectTypeDao()
                        .getAllTypesSync()
                        .map { it.name },
                boxes =
                    boxes.map { it.name },
                boxRecords =
                    boxes.map { box ->

                        SearchArchiveBoxRecord(
                            name = box.name,
                            categoryName =
                                categoryNameById[
                                    box.categoryId
                                ].orEmpty(),
                            locationName =
                                box.position
                        )
                    },
                objectRecords =
                    objectRows.map { row ->

                        SearchArchiveObjectRecord(
                            name = row.objectName,
                            description =
                                row.description
                                    .orEmpty(),
                            boxName = row.boxName,
                            boxCategory =
                                categoryNameByBoxId[
                                    row.boxId
                                ].orEmpty(),
                            boxLocation =
                                row.boxPosition,
                            categoryId =
                                categoryIdByBoxId[
                                    row.boxId
                                ] ?: 0
                        )
                    }
            )
        }

    private fun openPipelineList(
        response: SearchResponse,
        question: String
    ) {

        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                if (
                    response.objectTerms.isNotBlank()
                ) {

                    putExtra(
                        SearchConfiguration.EXTRA_OBJECT_TERMS,
                        response.objectTerms
                    )
                }

                if (
                    response.locationTerms.isNotBlank()
                ) {

                    putExtra(
                        SearchConfiguration.EXTRA_LOCATION_TERMS,
                        response.locationTerms
                    )
                }

                if (
                    response.categoryTerms.isNotBlank()
                ) {

                    putExtra(
                        SearchConfiguration.EXTRA_CATEGORY_TERMS,
                        response.categoryTerms
                    )
                }

                if (
                    response.boxTerms.isNotBlank()
                ) {

                    putExtra(
                        SearchConfiguration.EXTRA_BOX_TERMS,
                        response.boxTerms
                    )
                }

                if (
                    response.highlightTerms.isNotBlank()
                ) {

                    putExtra(
                        SearchConfiguration.EXTRA_HIGHLIGHT_TERMS,
                        response.highlightTerms
                    )
                }

                putExtra(
                    SearchConfiguration.EXTRA_SEARCH_QUESTION,
                    question
                )
            }
        )
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

        viewModel.replaceLastAssistantMessage(
            SearchMessage(
                text = visible,
                fromUser = false
            )
        )

        hideKeyboard(
            editQuestion
        )
    }
}
