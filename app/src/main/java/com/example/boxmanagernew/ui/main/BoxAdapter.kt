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
    private val onDelete: (Box) -> Unit,
    private val onToggleSelection: (Box) -> Unit
) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    private var selectedItems: Set<Box> = emptySet()
    private var selectionMode: Boolean = false

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

        // menu visibile solo fuori selezione
        holder.textMenu.visibility = if (selectionMode) View.GONE else View.VISIBLE

        holder.contentArea.setOnClickListener {
            if (selectionMode) {
                onToggleSelection(box)
            } else {
                onClick(box)
            }
        }

        holder.contentArea.setOnLongClickListener {
            onToggleSelection(box)
            true
        }

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
        notifyDataSetChanged()
    }

    // ✅ NUOVO: stato dall’esterno
    fun updateSelection(
        selected: Set<Box>,
        selectionMode: Boolean
    ) {
        this.selectedItems = selected
        this.selectionMode = selectionMode
        notifyDataSetChanged()
    }
}