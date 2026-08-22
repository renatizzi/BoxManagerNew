package com.example.boxmanagernew.ui.search

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
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
import com.example.boxmanagernew.ui.common.BottomNavManager
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

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            title = "Lista Oggetti Trovati",
            subtitle = "Risultati ricerca archivio"
        )

        setupViewOutputActions()

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_BOXES
        )

        searchQuery =
            intent.getStringExtra(
                "dashboardSearchQuery"
            ) ?: ""

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
                repo.searchObjects(searchQuery)

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

            loadedSnapshot =
                ContainerViewSnapshotFactory.fromSearchResults(
                    results
                ) { name ->
                    icons[name] ?: 0
                }
            resultsLoaded =
                true

            results
                .sortedBy {
                    it.boxName.lowercase()
                }
                .groupBy {
                    it.boxId
                }
                .forEach { (_, items) ->

                    addGroup(
                        container,
                        items,
                        searchQuery,
                        db
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
                        BackupConfiguration.MSG_FOLDER_INACCESSIBLE
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
                title = ViewOutputConfiguration.PAGE_TITLE_FOUND_OBJECTS,
                filterLine = ViewOutputConfiguration.filterLine(
                    searchQuery
                ),
                countLine = ViewOutputConfiguration.countObjects(
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

        val category =
            db.categoryDao()
                .getCategoryByName(
                    first.categoryName ?: ""
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

        val iconRes =
            resources.getIdentifier(
                category?.icon,
                "drawable",
                packageName
            )

        if (iconRes != 0) {

            categoryView
                .setCompoundDrawablesWithIntrinsicBounds(
                    iconRes,
                    0,
                    0,
                    0
                )
        }

        categoryView.text =
            " ${first.categoryName ?: "-"}"

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
                        "Quantità: ${item.quantity ?: "-"}"

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

        val result =
            SpannableString(text)

        val tokens =
            query.lowercase()
                .split("\\s+".toRegex())
                .filter {
                    it.length >= 3
                }

        val target =
            text.lowercase()

        tokens.forEach {

            val start =
                target.indexOf(it)

            if (start >= 0) {

                result.setSpan(
                    BackgroundColorSpan(
                        Color.YELLOW
                    ),
                    start,
                    start + it.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return result
    }
}