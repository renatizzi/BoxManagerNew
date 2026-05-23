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
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.domain.model.SearchResult
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import kotlinx.coroutines.launch

class SearchResultActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_search_result
        )

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            title = "Lista Oggetti Trovati",
            subtitle = "Risultati ricerca archivio"
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_DASHBOARD
        )

        val query =
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

            repo.searchObjects(query)
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
                        query,
                        db
                    )
                }
        }
    }

    private suspend fun addGroup(
        parent: LinearLayout,
        items: List<SearchResult>,
        query: String,
        db: com.example.boxmanagernew.data.local.AppDatabase
    ) {

        val first = items.first()

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

                setPadding(24,24,24,24)
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

                foreground =
                    getDrawable(
                        android.R.drawable.list_selector_background
                    )

                addView(
                    TextView(context).apply {

                        text = "˄"
                        textSize = 22f
                        gravity =
                            Gravity.CENTER
                    },
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                )
            }

        top.addView(title)
        top.addView(toggle)

        body.addView(top)

        first.boxDescription?.let {

            body.addView(
                TextView(this).apply {
                    text = it
                }
            )
        }

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
                    iconRes,0,0,0
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

            radius = 12f
            useCompatPadding = true

            val root =
                LinearLayout(context).apply {

                    orientation =
                        LinearLayout.HORIZONTAL

                    gravity =
                        Gravity.CENTER_VERTICAL

                    setPadding(
                        20,20,20,20
                    )
                }

            val icon =
                FrameLayout(context).apply {

                    addView(
                        TextView(context).apply {

                            text = "🧱"
                            textSize = 18f
                            gravity =
                                Gravity.CENTER
                        }
                    )

                    layoutParams =
                        LinearLayout.LayoutParams(
                            64,64
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

                    setTypeface(
                        null,
                        Typeface.BOLD
                    )
                }
            )

            item.description?.let {

                content.addView(
                    TextView(context).apply {

                        text =
                            highlight(
                                it,
                                query
                            )

                        textSize = 11f
                    }
                )
            }

            item.quantity?.let {

                content.addView(
                    TextView(context).apply {

                        text =
                            "Quantità: $it"

                        textSize = 11f
                    }
                )
            }

            val menu =
                TextView(context).apply {

                    text = "⋮"
                    textSize = 18f
                }

            root.addView(icon)
            root.addView(content)
            root.addView(menu)

            addView(root)
        }
    }

    private fun highlight(
        text: String,
        query: String
    ): SpannableString {

        if (
            query.length < 3
        ) return SpannableString(text)

        val start =
            text.lowercase()
                .indexOf(
                    query.lowercase()
                )

        if (
            start < 0
        ) return SpannableString(text)

        return SpannableString(text)
            .apply {

                setSpan(
                    BackgroundColorSpan(
                        Color.YELLOW
                    ),
                    start,
                    start + query.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
    }
}