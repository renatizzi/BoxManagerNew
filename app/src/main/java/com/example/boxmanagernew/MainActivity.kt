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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.ui.main.BoxAdapter
import com.example.boxmanagernew.ui.main.BoxViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: BoxViewModel
    private lateinit var adapter: BoxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editTextBox)
        val editSearch = findViewById<EditText>(R.id.editTextSearch)
        val button = findViewById<Button>(R.id.buttonAdd)
        val buttonSort = findViewById<Button>(R.id.buttonSort)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBoxes)

        val db = DatabaseProvider.getDatabase(applicationContext)
        val dao = db.boxDao()
        val repository = BoxRepositoryImpl(dao)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BoxViewModel(repository) as T
            }
        })[BoxViewModel::class.java]

        adapter = BoxAdapter(emptyList()) { box ->
            showEditDialog(box.id, box.name)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.boxes.observe(this) { boxes ->
            adapter.updateData(boxes)
        }

        viewModel.loadBoxes()

        buttonSort.setOnClickListener {
            viewModel.toggleSort()
        }

        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()

                viewModel.filter(query)

                if (query.isBlank()) {
                    button.visibility = View.VISIBLE
                    editSearch.clearFocus()
                    hideKeyboard(editSearch)
                } else {
                    button.visibility = View.GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ENTER su campo input
        editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = v.text.toString().trim()
                if (text.isNotBlank()) {
                    viewModel.addBox(text)
                    editText.text.clear()

                    // FIX: chiudi tastiera
                    editText.clearFocus()
                    hideKeyboard(editText)
                }
                true
            } else false
        }

        // Click bottone aggiungi
        button.setOnClickListener {
            val text = editText.text.toString().trim()
            if (text.isNotBlank()) {
                viewModel.addBox(text)
                editText.text.clear()

                // FIX: chiudi tastiera
                editText.clearFocus()
                hideKeyboard(editText)
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showEditDialog(id: Int, currentName: String) {
        val input = EditText(this)
        input.setText(currentName)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Modifica nome")
            .setView(input)
            .setPositiveButton("Salva") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotBlank()) {
                    viewModel.updateBox(id, newName)
                }
            }
            .setNegativeButton("Annulla", null)
            .setNeutralButton("Elimina") { _, _ ->
                showDeleteDialog(id)
            }
            .create()

        dialog.setOnShowListener {
            input.requestFocus()
        }

        dialog.show()

        // FIX: chiudi tastiera quando chiudi dialog
        dialog.setOnDismissListener {
            input.clearFocus()
            hideKeyboard(input)
        }
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