package com.example.boxmanagernew.ui.globalsearch

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ScrollView
import android.widget.TextView
import com.example.boxmanagernew.domain.premium.ArchivioCompletoAccess
import com.example.boxmanagernew.domain.premium.ArchivioCompletoCopy
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.search.GlobalSearchDispatcher
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.domain.search.SearchLocale
import com.example.boxmanagernew.domain.search.SearchLocaleContext
import com.example.boxmanagernew.ui.common.LocaleManager
import com.example.boxmanagernew.domain.search.model.SearchArchiveBoxRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveIndex
import com.example.boxmanagernew.domain.search.model.SearchArchiveObjectRecord
import com.example.boxmanagernew.domain.search.model.SearchArchiveTransformation
import com.example.boxmanagernew.domain.search.model.SearchMessage
import com.example.boxmanagernew.domain.search.model.SearchRequestType
import com.example.boxmanagernew.domain.search.model.SearchResponse
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.example.boxmanagernew.viewoutput.ui.ViewOutputController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalSearchActivity : BaseActivity() {

    private lateinit var viewModel: GlobalSearchViewModel
    private lateinit var editQuestion: EditText
    private lateinit var scrollSearchBody: ScrollView
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var outputController: ViewOutputController

    private val dispatcher =
        GlobalSearchDispatcher()

    private var printableQuestion =
        ""

    private var printableBoxNames:
            List<String> =
        emptyList()

    private var printableObjectNames:
            List<String> =
        emptyList()

    private val exportFolderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (
                uri != null &&
                ::outputController.isInitialized
            ) {
                outputController.onFolderChosen(
                    uri
                )
            }
        }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        if (
            !ArchivioCompletoNav.allowActivity(
                this,
                PremiumFeature.ADVANCED_SEARCH
            )
        ) {
            return
        }

        setContentView(
            R.layout.activity_global_search
        )

        setupAppShell()

        setupPageHeader(
            title = getString(
                R.string.global_search_title
            ),
            subtitle = getString(
                R.string.global_search_subtitle
            )
        )

        setupViewOutputActions()

        setupBottomNav()

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

            refreshSearchTrial()

            if (initialQuery.isNotBlank()) {
                submitQuestion()
            }
        } else {
            refreshSearchTrial()
        }
    }

    private fun refreshSearchTrial() {

        val trialView =
            findViewById<TextView>(R.id.textSearchTrial)

        val access =
            ArchivioCompletoAccess(this)

        if (access.isPermanentUnlock()) {
            trialView.visibility = View.GONE
            return
        }

        trialView.visibility = View.VISIBLE
        trialView.text =
            ArchivioCompletoCopy.trialStatusLine(
                this,
                access.remainingDays(),
                access.accessUntil()
            )
    }

    private fun submitQuestion() {

        val question =
            editQuestion.text
                .toString()
                .trim()

        if (question.isBlank()) {
            return
        }

        val access =
            ArchivioCompletoAccess(this)

        if (!access.isOpen()) {
            ArchivioCompletoNav.run(
                this,
                PremiumFeature.ADVANCED_SEARCH
            ) {}
            finish()
            return
        }

        hidePrintActions()

        lifecycleScope.launch {

            val index =
                loadArchiveIndex()

            val locale =
                SearchLocale.fromTag(
                    LocaleManager.storedTag(
                        this@GlobalSearchActivity
                    )
                )

            val response =
                withContext(
                    Dispatchers.Default
                ) {

                    dispatcher.dispatch(
                        question,
                        index,
                        locale
                    )
                }

            val unavailable =
                SearchLocaleContext.run(
                    locale
                ) {
                    SearchConfiguration.MSG_INTERROGATION_UNAVAILABLE
                }

            if (
                response.message ==
                unavailable
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

            if (
                response.success &&
                response.requestType ==
                SearchRequestType.ARCHIVE_QUERY &&
                response.resultBoxNames.isNotEmpty()
            ) {

                printableQuestion =
                    question

                printableBoxNames =
                    response.resultBoxNames

                printableObjectNames =
                    response.resultObjectNames

                showPrintActions()
            }
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

                if (
                    response.objectTerms.isBlank() &&
                    response.locationTerms.isBlank() &&
                    response.categoryTerms.isBlank() &&
                    response.boxTerms.isBlank()
                ) {

                    val inventoryDrive =
                        when (
                            response.archiveTransformation
                        ) {

                            SearchArchiveTransformation.CATEGORY_TO_BOX ->
                                SearchConfiguration.INVENTORY_CATEGORY

                            SearchArchiveTransformation.LOCATION_TO_BOX ->
                                SearchConfiguration.INVENTORY_LOCATION

                            else ->
                                SearchConfiguration.INVENTORY_BOX
                        }

                    putExtra(
                        SearchConfiguration.EXTRA_INVENTORY_LIST,
                        inventoryDrive
                    )
                }
            }
        )
    }

    private fun showReply(
        text: String
    ) {

        val locale =
            SearchLocale.fromTag(
                LocaleManager.storedTag(this)
            )

        val visible =
            SearchLocaleContext.run(locale) {
                if (
                    text.startsWith("[") ||
                    text.contains("LOOKUP") ||
                    text.contains("ENGINE_")
                ) {
                    SearchConfiguration.MSG_NOT_UNDERSTOOD
                } else {
                    text
                }
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

    private fun setupViewOutputActions() {

        val container =
            findViewById<FrameLayout>(
                R.id.headerActionContainer
            ) ?: return

        outputController =
            ViewOutputController(
                this,
                ViewExportPersister(this),
                showFolderInaccessible = {
                    FeedbackUtils.alert(
                        this
                    )
                    viewModel.addMessage(
                        SearchMessage(
                            text =
                                BackupConfiguration.folderInaccessible(this),
                            fromUser = false
                        )
                    )
                },
                launchFolderPicker = {
                    exportFolderPicker.launch(
                        null
                    )
                }
            )

        outputController.inflateActions(
            container,
            onPrint = {
                handlePrintView()
            },
            onExport = {
                handleExportView()
            }
        )

        hidePrintActions()
    }

    private fun showPrintActions() {

        findViewById<FrameLayout>(
            R.id.headerActionContainer
        )?.visibility =
            View.VISIBLE
    }

    private fun hidePrintActions() {

        printableQuestion =
            ""

        printableBoxNames =
            emptyList()

        printableObjectNames =
            emptyList()

        findViewById<FrameLayout>(
            R.id.headerActionContainer
        )?.visibility =
            View.GONE
    }

    private fun handlePrintView() {

        lifecycleScope.launch {

            val snapshot =
                loadPrintableSnapshot()
                    ?: return@launch

            outputController.print(
                snapshot,
                ViewPrintHeader(
                    title =
                        ViewOutputConfiguration.pageTitle(this@GlobalSearchActivity),
                    filterLine =
                        ViewOutputConfiguration.filterLine(
                            this@GlobalSearchActivity,
                            printableQuestion
                        ),
                    countLine =
                        ViewOutputConfiguration.countBoxes(
                            this@GlobalSearchActivity,
                            snapshot.boxes.size
                        )
                )
            )
        }
    }

    private fun handleExportView() {

        lifecycleScope.launch {

            val snapshot =
                loadPrintableSnapshot()
                    ?: return@launch

            outputController.export(
                snapshot
            )
        }
    }

    private suspend fun loadPrintableSnapshot():
            ContainerViewSnapshot? {

        val boxNames =
            printableBoxNames

        val objectNames =
            printableObjectNames

        if (boxNames.isEmpty()) {

            showOutputNotice(
                noResultsMessage()
            )

            return null
        }

        val snapshot =
            withContext(
                Dispatchers.IO
            ) {

                val db =
                    DatabaseProvider.getDatabase(
                        applicationContext
                    )

                val allowedBoxes =
                    boxNames
                        .map { name ->
                            name.lowercase()
                        }
                        .toSet()

                val nameOrder =
                    boxNames
                        .mapIndexed { index, name ->
                            name.lowercase() to
                                index
                        }
                        .toMap()

                val boxes =
                    db.boxDao()
                        .getAllSync()
                        .filter { entity ->
                            entity.name.lowercase() in
                                allowedBoxes
                        }
                        .sortedBy { entity ->
                            nameOrder[
                                entity.name.lowercase()
                            ] ?: Int.MAX_VALUE
                        }
                        .map { entity ->

                            Box(
                                id = entity.id,
                                name = entity.name,
                                categoryId =
                                    entity.categoryId,
                                position =
                                    entity.position,
                                lastModified =
                                    entity.lastModified,
                                permanentId =
                                    entity.permanentId
                            )
                        }

                if (boxes.isEmpty()) {
                    return@withContext null
                }

                val categories =
                    db.categoryDao()
                        .getAllSync()

                val objects =
                    ObjectRepositoryImpl(
                        db.objectDao(),
                        db.objectTypeDao()
                    ).objectsInBoxes(
                        boxes.map { box ->
                            box.id
                        }.toSet()
                    ).let { rows ->

                        if (objectNames.isEmpty()) {
                            rows
                        } else {

                            val allowedObjects =
                                objectNames
                                    .map { name ->
                                        name.lowercase()
                                    }
                                    .toSet()

                            rows.filter { row ->
                                row.objectName.lowercase() in
                                    allowedObjects
                            }
                        }
                    }

                ContainerViewSnapshotFactory.from(
                    boxes,
                    { categoryId ->
                        categories.find { category ->
                            category.id == categoryId
                        }?.name.orEmpty()
                    },
                    { categoryId ->
                        val icon =
                            categories.find { category ->
                                category.id ==
                                    categoryId
                            }?.icon.orEmpty()

                        if (icon.isBlank()) {
                            0
                        } else {
                            IconMapper.getIconRes(
                                icon
                            )
                        }
                    },
                    objects
                )
            }

        if (snapshot == null) {

            showOutputNotice(
                noResultsMessage()
            )
        }

        return snapshot
    }

    private fun noResultsMessage(): String {

        val locale =
            SearchLocale.fromTag(
                LocaleManager.storedTag(this)
            )

        return SearchLocaleContext.run(locale) {
            SearchConfiguration.MSG_NO_RESULTS
        }
    }

    private fun showOutputNotice(
        text: String
    ) {

        viewModel.addMessage(
            SearchMessage(
                text = text,
                fromUser = false
            )
        )
    }
}
