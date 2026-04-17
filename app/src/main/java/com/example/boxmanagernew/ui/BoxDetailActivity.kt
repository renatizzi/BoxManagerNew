package com.example.boxmanagernew.ui.boxdetail

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl

class BoxDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ObjectViewModel
    private lateinit var adapter: ObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_box_detail)

        val root = findViewById<android.view.View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textTitle = findViewById<TextView>(R.id.textTitle)
        val textCategory = findViewById<TextView>(R.id.textCategory)
        val textPosition = findViewById<TextView>(R.id.textPosition)
        val textCount = findViewById<TextView>(R.id.textCount)
        val textLastModified = findViewById<TextView>(R.id.textLastModified)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerObjects)

        val boxId = intent.getIntExtra("boxId", -1)
        val boxName = intent.getStringExtra("boxName") ?: "Contenitore"

        textTitle.text = boxName

        // Placeholder dati (da collegare al BoxViewModel nel prossimo step)
        textCategory.text = "Categoria: -"
        textPosition.text = "Posizione: -"
        textCount.text = "Oggetti: -"
        textLastModified.text = "Ultima modifica: -"

        adapter = ObjectAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val dao = AppDatabase.getDatabase(this).objectDao()
        val repository = ObjectRepositoryImpl(dao)
        val factory = ObjectViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[ObjectViewModel::class.java]

        viewModel.getObjectsWithType(boxId).observe(this) { list ->
            adapter.updateData(list)
            textCount.text = "Oggetti: ${list.size}"
        }
    }
}