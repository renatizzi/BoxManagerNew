package com.example.boxmanagernew.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.domain.model.Location
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.common.FeedbackUtils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class LocationsActivity : BaseActivity() {

    private lateinit var viewModel: LocationViewModel
    private lateinit var adapter: LocationAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var counter: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var contextCard: MaterialCardView
    private lateinit var textContextMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_locations)

        setupEdgeToEdge()
        setupTopBar()

        setupPageHeader(
            "Posizione",
            "Luoghi abituali di custodia"
        )

        BottomNavManager.setup(
            this,
            BottomNavManager.TAB_SETTINGS
        )

        contextCard =
            findViewById(R.id.contextCard)

        textContextMessage =
            findViewById(R.id.textContextMessage)

        contextCard.setOnClickListener {
            hideWarning()
        }

        recycler =
            findViewById(R.id.recyclerViewLocations)

        counter =
            findViewById(R.id.textLocationCount)

        fab =
            findViewById(R.id.fabAddLocation)

        val db =
            DatabaseProvider.getDatabase(
                applicationContext
            )

        val repository =
            LocationRepositoryImpl(
                db.locationDao(),
                db.boxDao()
            )

        viewModel =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel>
                            create(
                        modelClass: Class<T>
                    ): T {

                        return LocationViewModel(
                            repository
                        ) as T
                    }
                }
            )[LocationViewModel::class.java]

        adapter =
            LocationAdapter(
                emptyList(),
                { showEditDialog(it) },
                { showDeleteDialog(it) }
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.adapter =
            adapter

        fab.setOnClickListener {
            hideWarning()
            showAddDialog()
        }

        viewModel.locations.observe(this) {
            adapter.updateData(it)
            counter.text =
                "N. Posizioni: ${it.size}"
        }

        hideWarning()
        refreshAppShell()
    }

    private fun hideWarning() {
        contextCard.visibility =
            View.GONE
    }

    private fun showWarning(
        text: String
    ) {
        textContextMessage.text =
            text

        contextCard.visibility =
            View.VISIBLE

        FeedbackUtils.alert(this)
    }

    private fun createErrorText() =
        TextView(this).apply {
            visibility = View.GONE
            setTextColor(Color.RED)
        }

    private fun showAddDialog() {
        val input = EditText(this)
        val error = createErrorText()

        val container =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
                addView(input)
                addView(error)
            }

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Nuova posizione")
                .setView(container)
                .setNegativeButton("Annulla", null)
                .setPositiveButton("Aggiungi", null)
                .create()

        dialog.setOnShowListener {
            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                lifecycleScope.launch {

                    val ok =
                        viewModel.insert(
                            Location(
                                name =
                                    input.text.toString().trim()
                            )
                        )

                    if (ok) {

                        dialog.dismiss()

                    } else {

                        FeedbackUtils.alert(
                            this@LocationsActivity
                        )

                        error.text =
                            "Posizione già esistente"

                        error.visibility =
                            TextView.VISIBLE
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showEditDialog(
        location: Location
    ) {
        val input = EditText(this)
        input.setText(location.name)

        val error =
            createErrorText()

        val container =
            LinearLayout(this).apply {
                orientation =
                    LinearLayout.VERTICAL
                addView(input)
                addView(error)
            }

        val dialog =
            AlertDialog.Builder(this)
                .setTitle("Modifica posizione")
                .setView(container)
                .setNegativeButton("Annulla", null)
                .setPositiveButton("Salva", null)
                .create()

        dialog.setOnShowListener {
            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                lifecycleScope.launch {

                    val ok =
                        viewModel.update(
                            location.copy(
                                name =
                                    input.text.toString().trim()
                            )
                        )

                    if (ok) {

                        dialog.dismiss()

                    } else {

                        FeedbackUtils.alert(
                            this@LocationsActivity
                        )

                        error.text =
                            "Posizione già esistente"

                        error.visibility =
                            TextView.VISIBLE
                    }
                }
            }
        }

        dialog.show()
    }

    private fun showDeleteDialog(
        location: Location
    ) {

        lifecycleScope.launch {

            val deleted =
                viewModel.delete(
                    location
                )

            if (!deleted) {

                showWarning(
                    "Posizione in uso: eliminazione non consentita.\nTocca qui per annullare."
                )

                return@launch
            }

            DialogUtils.showDeleteConfirmation(
                this@LocationsActivity
            ) {

                lifecycleScope.launch {
                    viewModel.delete(
                        location
                    )
                }
            }
        }
    }
}