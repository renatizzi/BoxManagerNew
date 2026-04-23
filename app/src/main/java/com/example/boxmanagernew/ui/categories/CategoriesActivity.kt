package com.example.boxmanagernew.ui.categories

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_categories)

        BottomNavManager.setup(this, BottomNavManager.TAB_CATEGORIES)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        val root = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewCategories)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddCategory)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = CategoryRepositoryImpl(db.categoryDao(), db.boxDao())

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CategoryViewModel(repository) as T
            }
        })[CategoryViewModel::class.java]

        adapter = CategoryAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.categories.observe(this) {
            adapter.updateData(it)
        }

        viewModel.operationResult.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }

        fabAdd.setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 10)
        }

        val inputName = EditText(this).apply {
            hint = "Nome categoria"
        }

        val iconGrid = RecyclerView(this).apply {
            layoutManager = GridLayoutManager(context, 4)
        }

        val drawableIds = mutableListOf<Int>()
        val fields = R.drawable::class.java.fields

        for (field in fields) {
            val id = field.getInt(null)
            val name = resources.getResourceEntryName(id)

            if (name.startsWith("ic_cat")) {
                drawableIds.add(id)
            }
        }

        var selectedIcon: Int? = null

        val iconAdapter = object : RecyclerView.Adapter<IconViewHolder>() {

            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): IconViewHolder {
                val img = ImageView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(140, 140)
                    setPadding(16, 16, 16, 16)
                }
                return IconViewHolder(img)
            }

            override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
                val res = drawableIds[position]
                holder.image.setImageResource(res)

                if (res == selectedIcon) {
                    holder.image.setBackgroundResource(android.R.drawable.alert_light_frame)
                } else {
                    holder.image.background = null
                }

                holder.image.setOnClickListener {
                    selectedIcon = res
                    notifyDataSetChanged()
                }
            }

            override fun getItemCount(): Int = drawableIds.size
        }

        iconGrid.adapter = iconAdapter

        layout.addView(inputName)
        layout.addView(iconGrid)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Nuova categoria")
            .setView(layout)
            .setPositiveButton("Aggiungi", null)
            .setNegativeButton("Annulla", null)
            .create()

        dialog.setOnShowListener {

            val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            btn.setOnClickListener {

                val name = inputName.text.toString().trim()

                if (name.isEmpty()) {
                    inputName.error = "Inserisci un nome"
                    return@setOnClickListener
                }

                if (selectedIcon == null) {
                    Toast.makeText(this, "Seleziona un'icona", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val iconName = resources.getResourceEntryName(selectedIcon!!)

                viewModel.insert(Category(0, name, iconName))

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    class IconViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)
}