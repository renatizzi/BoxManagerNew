package com.example.boxmanagernew.ui.boxdetail

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.MainActivity
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.AppDatabase
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.ui.categories.CategoryViewModel
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.main.BoxViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoxDetailActivity : AppCompatActivity() {

    private lateinit var objectViewModel: ObjectViewModel
    private lateinit var boxViewModel: BoxViewModel
    private lateinit var categoryViewModel: CategoryViewModel
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
        val imageCategoryIcon = findViewById<ImageView>(R.id.imageCategoryIcon)
        val textPosition = findViewById<TextView>(R.id.textPosition)
        val textLastModified = findViewById<TextView>(R.id.textLastModified)
        val textObjectsTitle = findViewById<TextView>(R.id.textObjectsTitle)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerObjects)

        // NAV BAR
        val navDashboard = findViewById<TextView>(R.id.navDashboard)
        val navBoxes = findViewById<TextView>(R.id.navBoxes)
        val navCategories = findViewById<TextView>(R.id.navCategories)
        val navUtility = findViewById<TextView>(R.id.navUtility)
        val navSettings = findViewById<TextView>(R.id.navSettings)

        navBoxes.setTextColor(Color.BLACK)

        navBoxes.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        navDashboard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tab", "dashboard")
            startActivity(intent)
        }

        navCategories.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tab", "categories")
            startActivity(intent)
        }

        navUtility.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tab", "utility")
            startActivity(intent)
        }

        navSettings.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("tab", "settings")
            startActivity(intent)
        }

        val boxId = intent.getIntExtra("boxId", -1)
        val boxName = intent.getStringExtra("boxName") ?: "Contenitore"

        textTitle.text = boxName

        adapter = ObjectAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val db = AppDatabase.getDatabase(this)

        val objectRepository = ObjectRepositoryImpl(db.objectDao())
        val objectFactory = ObjectViewModelFactory(objectRepository)
        objectViewModel = ViewModelProvider(this, objectFactory)[ObjectViewModel::class.java]

        val boxRepository = BoxRepositoryImpl(db.boxDao())
        boxViewModel = BoxViewModel(boxRepository)

        val categoryRepository = CategoryRepositoryImpl(db.categoryDao())
        categoryViewModel = CategoryViewModel(categoryRepository)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        objectViewModel.getObjectsWithType(boxId).observe(this) { list ->
            adapter.updateData(list)
            textObjectsTitle.text = "Lista Oggetti (${list.size})"
        }

        boxViewModel.boxes.observe(this) { boxes ->
            val box = boxes.find { it.id == boxId }
            if (box != null) {

                textPosition.text = "📍 ${box.position}"
                textLastModified.text =
                    "📅 ${dateFormat.format(Date(box.lastModified))}"

                categoryViewModel.categories.observe(this) { categories ->
                    val category = categories.find { it.id == box.categoryId }
                    if (category != null) {
                        textCategory.text = category.name
                        val iconRes = IconMapper.getIconRes(category.icon)
                        imageCategoryIcon.setImageResource(iconRes)
                    }
                }
            }
        }
    }
}