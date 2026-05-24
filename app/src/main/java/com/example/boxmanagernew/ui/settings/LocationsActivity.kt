package com.example.boxmanagernew.ui.settings

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LocationsActivity : BaseActivity() {

    private lateinit var viewModel: LocationViewModel
    private lateinit var adapter: LocationAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var counter: TextView
    private lateinit var fab: FloatingActionButton

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_locations
        )

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

        recycler =
            findViewById(
                R.id.recyclerViewLocations
            )

        counter =
            findViewById(
                R.id.textLocationCount
            )

        fab =
            findViewById(
                R.id.fabAddLocation
            )

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
                object :
                    ViewModelProvider.Factory {

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
                {},
                {}
            )

        recycler.layoutManager =
            LinearLayoutManager(this)

        recycler.adapter =
            adapter

        viewModel.locations.observe(this) {

            adapter.updateData(it)

            counter.text =
                "N. Luoghi: ${it.size}"
        }

        refreshAppShell()
    }
}