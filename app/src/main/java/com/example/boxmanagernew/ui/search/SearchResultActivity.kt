package com.example.boxmanagernew.ui.search

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.SearchResult
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.SimpleSearchHighlight
import com.example.boxmanagernew.util.CanonicalNormalizer
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshot
import com.example.boxmanagernew.viewoutput.model.ContainerViewSnapshotFactory
import com.example.boxmanagernew.viewoutput.model.ViewPrintHeader
import com.example.boxmanagernew.viewoutput.persist.ViewExportPersister
import com.example.boxmanagernew.viewoutput.ui.ViewOutputController
import kotlinx.coroutines.launch

class SearchResultActivity : BaseActivity() {

    private lateinit var outputController: ViewOutputController

    private var resultsLoaded =
        false

    private var loadedSnapshot: ContainerViewSnapshot? =
        null

    private var searchQuery =
        ""

    private var reportQuestion =
        ""

    private val exportFolderPicker =
        registerForActivityResult(
            ActivityResultContracts.OpenDocumentTree()
        ) { uri ->

            if (uri != null && ::outputController.isInitialized) {
                outputController.onFolderChosen(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_result)

        setupAppShell()

        setupPageHeader(
            title = getString(R.string.page_search_results_title),
            subtitle = getString(R.string.page_search_results_subtitle)
        )

        setupViewOutputActions()

        setupBottomNav()

        searchQuery =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_DASHBOARD_SEARCH_QUERY
            ) ?: ""

        reportQuestion =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_SEARCH_QUESTION
            ).orEmpty()
                .ifBlank {
                    searchQuery
                }

        val locationTerms =
            intent.getStringExtra(
                SearchConfiguration.EXTRA_LOCATION_TERMS
            ).orEmpty()

        val container =
            findViewById<LinearLayout>(
                R.id.resultsContainer
            )

        lifecycleScope.launch {

            val db =
                DatabaseProvider.getDatabase(
                    applicationContext
                )

            val repo =
                ObjectRepositoryImpl(
                    db.objectDao(),
                    db.objectTypeDao()
                )

            val results =
                filterByLocation(
                    repo.searchObjectsInline(searchQuery),
                    locationTerms
                )

            val grouped =
                results
                    .sortedBy {
                        it.boxName.lowercase()
                    }
                    .groupBy {
                        it.boxId
                    }

            val iconNames =
                results.mapNotNull { row ->
                    row.categoryName
                }.distinct()

            val icons =
                iconNames.associateWith { name ->
                    val category =
                        db.categoryDao()
                            .getCategoryByName(name)
                    val icon =
                        category?.icon.orEmpty()
                    if (icon.isBlank()) {
                        0
                    } else {
                        IconMapper.getIconRes(icon)
                    }
                }

            val snapshotBlocks =
                grouped.map { (_, items) ->

                    val resolved =
                        resolveCategoryForGroup(
                            items.first(),
                            db
                        )

                    ContainerViewSnapshotFactory.searchResultGroupBlock(
                        items = items,
                        categoryIconOfName = { name ->
                            icons[name] ?: 0
                        },
                        resolvedCategoryName =
                            resolved.displayName,
                        resolvedCategoryIconRes =
                            resolved.iconRes
                    )
                }

            loadedSnapshot =
                ContainerViewSnapshot(snapshotBlocks)
            resultsLoaded =
                true

            grouped.forEach { (_, items) ->

                addGroup(
                    container,
                    items,
                    searchQuery,
                    db
                )
            }
        }
    }

    private fun filterByLocation(
        rows: List<SearchResult>,
        locationTerms: String
    ): List<SearchResult> {

        if (locationTerms.isBlank()) {
            return rows
        }

        return rows.filter { row ->

            SearchConfiguration.splitLocationTerms(
                locationTerms
            ).any { name ->

                CanonicalNormalizer.allTokensMatchWords(
                    name,
                    row.boxPosition
                )
            }
        }
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
                    showOutputMessage(
                        BackupConfiguration.folderInaccessible(this)
                    )
                },
                launchFolderPicker = {
                    exportFolderPicker.launch(null)
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
    }

    private fun handlePrintView() {

        val snapshot =
            snapshotForOutput()
                ?: return

        outputController.print(
            snapshot,
            ViewPrintHeader(
                title = ViewOutputConfiguration.pageTitleFoundObjects(this),
                filterLine = ViewOutputConfiguration.filterLine(
                    this,
                    reportQuestion
                ),
                countLine = ViewOutputConfiguration.countObjects(
                    this,
                    snapshot.objectCount
                ),
                showBlockSubtotals = true
            )
        )
    }

    private fun handleExportView() {

        val snapshot =
            snapshotForOutput()
                ?: return

        outputController.export(snapshot)
    }

    private fun snapshotForOutput():
            ContainerViewSnapshot? {

        if (!resultsLoaded) {
            return null
        }

        val snapshot =
            loadedSnapshot
                ?: ContainerViewSnapshot(emptyList())

        if (snapshot.objectCount == 0) {

            showOutputMessage(
                SearchConfiguration.MSG_NO_RESULTS
            )
            return null
        }

        return snapshot
    }

    private fun showOutputMessage(
        message: String
    ) {

        findViewById<TextView>(
            R.id.textOutputMessage
        )?.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private data class ResolvedCategory(
        val entity:
            com.example.boxmanagernew.data.local.entity.CategoryEntity?,
        val displayName: String,
        val iconRes: Int
    )

    private suspend fun resolveCategoryForGroup(
        first: SearchResult,
        db: com.example.boxmanagernew.data.local.AppDatabase
    ): ResolvedCategory {

        val category =
            db.boxDao().getById(first.boxId)?.categoryId?.let { categoryId ->
                db.categoryDao().getById(categoryId)
            } ?: db.categoryDao().getCategoryByName(
                first.categoryName.orEmpty()
            )

        val displayName =
            category?.name
                ?: first.categoryName.orEmpty().ifBlank { "-" }

        val iconRes =
            if (category != null) {
                IconMapper.getIconRes(category.icon)
            } else {
                0
            }

        return ResolvedCategory(
            entity = category,
            displayName = displayName,
            iconRes = iconRes
        )
    }

    private suspend fun addGroup(
        parent: LinearLayout,
        items: List<SearchResult>,
        query: String,
        db: com.example.boxmanagernew.data.local.AppDatabase
    ) {

        val first = items.first()

        android.util.Log.d(
            "BOX_M9",
            "[M9] GROUPS=1 ITEMS=${items.size}"
        )

        val resolved =
            resolveCategoryForGroup(
                first,
                db
            )

        val card =
            CardView(this).apply {
                radius = 18f
                useCompatPadding = true
            }

        val body =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL

                setPadding(
                    24,24,24,24
                )
            }

        val top =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.HORIZONTAL

                gravity =
                    Gravity.CENTER_VERTICAL
            }

        val title =
            TextView(this).apply {

                val s =
                    SpannableString(
                        "📦 ${first.boxName}"
                    )

                s.setSpan(
                    StyleSpan(Typeface.BOLD),
                    2,
                    s.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                text = s

                layoutParams =
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
            }

        val toggle =
            FrameLayout(this).apply {

                layoutParams =
                    LinearLayout.LayoutParams(
                        96,
                        96
                    )

                addView(
                    TextView(context).apply {

                        text = "˄"
                        textSize = 22f
                        gravity =
                            Gravity.CENTER
                    }
                )
            }

        top.addView(title)
        top.addView(toggle)

        body.addView(top)

        val categoryView =
            TextView(this)

        if (resolved.iconRes != 0) {

            categoryView
                .setCompoundDrawablesWithIntrinsicBounds(
                    resolved.iconRes,
                    0,
                    0,
                    0
                )
        }

        categoryView.text =
            " ${resolved.displayName}"

        body.addView(categoryView)

        body.addView(

            TextView(this).apply {

                text =
                    "📍 ${first.boxPosition}"
            }
        )

        val objects =
            LinearLayout(this).apply {

                orientation =
                    LinearLayout.VERTICAL
            }

        toggle.setOnClickListener {

            val text =
                toggle.getChildAt(0)
                        as TextView

            objects.visibility =

                if (
                    objects.visibility ==
                    View.VISIBLE
                ) {

                    text.text = "˅"
                    View.GONE

                } else {

                    text.text = "˄"
                    View.VISIBLE
                }
        }

        items.forEach {

            objects.addView(
                createCard(
                    it,
                    query
                )
            )
        }

        body.addView(objects)

        card.addView(body)

        parent.addView(card)
    }

    private fun createCard(
        item: SearchResult,
        query: String
    ): CardView {

        return CardView(this).apply {

            android.util.Log.d(
                "BOX_M10",
                "[M10] CARDS=1 HIGHLIGHT=${
                    query.lowercase()
                        .split("\\s+".toRegex())
                        .filter { it.length >= 3 }
                        .any {
                            item.objectName.lowercase().contains(it) ||
                                    (item.description ?: "")
                                        .lowercase()
                                        .contains(it)
                        }
                }"
            )

            radius = 12f
            useCompatPadding = true

            val root =
                LinearLayout(context).apply {

                    orientation =
                        LinearLayout.HORIZONTAL

                    gravity =
                        Gravity.CENTER_VERTICAL

                    setPadding(
                        20,
                        20,
                        20,
                        20
                    )
                }

            val icon =
                FrameLayout(context).apply {

                    addView(
                        TextView(context).apply {

                            text = "🧱"
                            textSize = 18f
                        }
                    )

                    layoutParams =
                        LinearLayout.LayoutParams(
                            64,
                            64
                        )
                }

            val content =
                LinearLayout(context).apply {

                    orientation =
                        LinearLayout.VERTICAL

                    layoutParams =
                        LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                }

            content.addView(

                TextView(context).apply {

                    text =
                        highlight(
                            item.objectName,
                            query
                        )

                    textSize = 16f

                    setTypeface(
                        null,
                        Typeface.BOLD
                    )
                }
            )

            content.addView(

                TextView(context).apply {

                    text =
                        if (
                            item.description
                                .isNullOrBlank()
                        ) "-"
                        else
                            highlight(
                                item.description,
                                query
                            )

                    textSize = 13f
                    alpha = 0.65f
                }
            )

            content.addView(

                TextView(context).apply {

                    text =
                        context.getString(
                            R.string.object_quantity_label,
                            item.quantity ?: "-"
                        )

                    textSize = 13f
                    alpha = 0.65f
                }
            )

            root.addView(icon)
            root.addView(content)

            addView(root)
        }
    }

    private fun highlight(
        text: String,
        query: String
    ): SpannableString {

        return SimpleSearchHighlight.paint(
            text,
            query
        )
    }
}