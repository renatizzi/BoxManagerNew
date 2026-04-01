package com.example.boxmanagernew.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Box

class BoxAdapter(
    private var items: List<Box>,
    private val onClick: (Box) -> Unit,
    private val onEdit: (Box) -> Unit,
    private val onDelete: (Box) -> Unit
) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    private val selectedItems = mutableSetOf<Box>()
    private var selectionMode = false

    var onSelectionChanged: ((Int) -> Unit)? = null

    inner class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contentArea: View = itemView.findViewById(R.id.contentArea)
        val textBoxName: TextView = itemView.findViewById(R.id.textBoxName)
        val textSubtitle: TextView = itemView.findViewById(R.id.textSubtitle)
        val textMenu: TextView = itemView.findViewById(R.id.textMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_box, parent, false)
        return BoxViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val box = items[position]

        holder.textBoxName.text = box.name
        holder.textSubtitle.text = "Categoria - Posizione"

        val isSelected = selectedItems.contains(box)
        holder.itemView.alpha = if (isSelected) 0.5f else 1.0f

        // 🔴 CLICK SOLO AREA CONTENUTO
        holder.contentArea.setOnClickListener {
            if (selectionMode) {
                toggleSelection(box)
            } else {
                onClick(box)
            }
        }

        holder.contentArea.setOnLongClickListener {
            selectionMode = true
            toggleSelection(box)
            true
        }

        // 🔴 MENU ISOLATO
        holder.textMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Modifica")
            popup.menu.add("Elimina")

            popup.setOnMenuItemClickListener {
                when (it.title) {
                    "Modifica" -> onEdit(box)
                    "Elimina" -> onDelete(box)
                }
                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Box>) {
        items = newItems
        clearSelection()
        notifyDataSetChanged()
    }

    private fun toggleSelection(box: Box) {
        if (selectedItems.contains(box)) {
            selectedItems.remove(box)
        } else {
            selectedItems.add(box)
        }

        if (selectedItems.isEmpty()) {
            selectionMode = false
        }

        notifyDataSetChanged()
        onSelectionChanged?.invoke(selectedItems.size)
    }

    fun getSelectedItems(): List<Box> = selectedItems.toList()

    fun clearSelection() {
        selectedItems.clear()
        selectionMode = false
        notifyDataSetChanged()
        onSelectionChanged?.invoke(0)
    }
}