package com.example.boxmanagernew

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.ui.common.BottomNavManager
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BoxViewModel
    private lateinit var adapter: BoxAdapter
    private lateinit var buttonDeleteSelected: Button
    private lateinit var textSelectionCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

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

        val editText = findViewById<EditText>(R.id.editTextBox)
        val editSearch = findViewById<EditText>(R.id.editTextSearch)
        val button = findViewById<Button>(R.id.buttonAdd)
        val buttonSort = findViewById<Button>(R.id.buttonSort)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBoxes)

        buttonDeleteSelected = findViewById(R.id.btnDeleteSelected)
        textSelectionCount = findViewById(R.id.textSelectionCount)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.boxDao()
        val repository = BoxRepositoryImpl(dao)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(repository) as T
            }
        })[BoxViewModel::class.java]

        // ✅ ADAPTER STATELESS
        adapter = BoxAdapter(
            items = emptyList(),
            onClick = { box -> showEditDialog(box.id, box.name) },
            onEdit = { box -> showEditDialog(box.id, box.name) },
            onDelete = { box -> showDeleteDialog(box.id) },
            onToggleSelection = { box ->
                viewModel.toggleSelection(box)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // ✅ OSSERVA LISTA
        viewModel.boxes.observe(this) {
            adapter.updateData(it)
        }

        // ✅ OSSERVA SELEZIONE (CUORE DEL FIX)
        viewModel.selectedItems.observe(this) { selected ->
            val mode = viewModel.selectionMode.value ?: false
            adapter.updateSelection(selected, mode)

            val count = selected.size
            if (count > 0) {
                buttonDeleteSelected.visibility = View.VISIBLE
                textSelectionCount.visibility = View.VISIBLE

                textSelectionCount.text =
                    if (count == 1) "1 selezionato" else "$count selezionati"
            } else {
                buttonDeleteSelected.visibility = View.GONE
                textSelectionCount.visibility = View.GONE
            }
        }

        viewModel.selectionMode.observe(this) { mode ->
            val selected = viewModel.selectedItems.value ?: emptySet()
            adapter.updateSelection(selected, mode)
        }

        buttonDeleteSelected.setOnClickListener {
            val selectedItems = viewModel.selectedItems.value?.toList() ?: emptyList()
            if (selectedItems.isEmpty()) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Conferma eliminazione")
                .setMessage("Eliminare ${selectedItems.size} elementi?")
                .setPositiveButton("Sì") { _, _ ->
                    viewModel.deleteBoxes(selectedItems)
                }
                .setNegativeButton("No", null)
                .show()
        }

        viewModel.loadBoxes()

        buttonSort.setOnClickListener {
            hideKeyboard(it)
            editSearch.clearFocus()
            viewModel.toggleSort()
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                viewModel.filter(query)

                if (query.isBlank()) {
                    editSearch.clearFocus()
                    hideKeyboard(editSearch)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text.toString().trim()
                if (text.isNotBlank()) {
                    viewModel.addBox(text)

                    editSearch.setText("")

                    editText.text.clear()
                    editText.clearFocus()
                    hideKeyboard(editText)
                }
                true
            } else false
        }

        button.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotBlank()) {
                viewModel.addBox(text)

                editSearch.setText("")

                editText.text.clear()
                editText.clearFocus()
                hideKeyboard(editText)
            }
        }

        BottomNavManager.setup(this, BottomNavManager.TAB_BOXES)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val selected = viewModel.selectedItems.value ?: emptySet()
                if (selected.isNotEmpty()) {
                    viewModel.clearSelection()
                } else {
                    finish()
                }
            }
        })
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showEditDialog(id: Int, currentName: String) {
        val input = EditText(this)
        input.setText(currentName)

        AlertDialog.Builder(this)
            .setTitle("Modifica nome")
            .setView(input)
            .setPositiveButton("Salva") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    viewModel.updateBox(id, newName)
                }
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun showDeleteDialog(id: Int) {
        AlertDialog.Builder(this)
            .setTitle("Conferma eliminazione")
            .setMessage("Vuoi eliminare questo elemento?")
            .setPositiveButton("Sì") { _, _ ->
                viewModel.deleteBox(id)
            }
            .setNegativeButton("No", null)
            .show()
    }
}