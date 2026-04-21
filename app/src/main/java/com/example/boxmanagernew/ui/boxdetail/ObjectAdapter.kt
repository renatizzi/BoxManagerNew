package com.example.boxmanagernew.ui.boxdetail

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.ObjectWithType

class ObjectAdapter(
    private var items: List<ObjectWithType>,
    private val onClick: (ObjectWithType) -> Unit,
    private val onToggleSelection: (ObjectWithType) -> Unit,
    private val onEdit: (ObjectWithType) -> Unit,
    private val onDelete: (ObjectWithType) -> Unit
) : RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder>() {

    private var selectedIds: Set<Int> = emptySet()
    private var selectionMode: Boolean = false

    inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: CardView = itemView as CardView
        val contentArea: View = itemView.findViewById(R.id.contentArea)
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textQuantity: TextView = itemView.findViewById(R.id.textQuantity)
        val textMenu: TextView = itemView.findViewById(R.id.textMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_object, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        val item = items[position]

        holder.textName.text = item.typeName

        if (item.obj.description.isNullOrEmpty()) {
            holder.textDescription.visibility = View.GONE
        } else {
            holder.textDescription.visibility = View.VISIBLE
            holder.textDescription.text = item.obj.description
        }

        if (item.obj.quantity == null) {
            holder.textQuantity.visibility = View.GONE
        } else {
            holder.textQuantity.visibility = View.VISIBLE
            holder.textQuantity.text = "Quantità: ${item.obj.quantity}"
        }

        val isSelected = selectedIds.contains(item.obj.id)

        val drawable = GradientDrawable().apply {
            cornerRadius = 16f
            if (isSelected) {
                setColor(Color.parseColor("#E3F2FD"))
                setStroke(4, Color.parseColor("#2196F3"))
            } else {
                setColor(Color.WHITE)
                setStroke(0, Color.TRANSPARENT)
            }
        }

        holder.card.background = drawable

        holder.textMenu.visibility = if (selectionMode) View.GONE else View.VISIBLE

        holder.contentArea.setOnClickListener {
            onToggleSelection(item)
        }

        holder.contentArea.setOnLongClickListener {
            onToggleSelection(item)
            true
        }

        holder.textMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Modifica")
            popup.menu.add("Elimina")

            popup.setOnMenuItemClickListener {
                when (it.title) {
                    "Modifica" -> onEdit(item)
                    "Elimina" -> onDelete(item)
                }
                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ObjectWithType>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun updateSelection(selectedIds: Set<Int>, selectionMode: Boolean) {
        this.selectedIds = selectedIds
        this.selectionMode = selectionMode
        notifyDataSetChanged()
    }
}