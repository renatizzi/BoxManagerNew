package com.example.boxmanagernew.ui.boxdetail

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.ObjectWithType

class ObjectAdapter(
    private var items: List<ObjectWithType>,
    private val onClick: (Int) -> Unit,
    private val onToggleSelection: (Int) -> Unit,
    private val onEdit: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder>() {

    private var selectedIds: Set<Int> = emptySet()
    private var selectionMode: Boolean = false
    private var isFilterActive: Boolean = false
    private var currentQuery: String = ""

    inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rootSelectable: View = itemView.findViewById(R.id.rootSelectable)
        val iconArea: FrameLayout = itemView.findViewById(R.id.iconArea)
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
        val id = item.obj.id

        holder.textName.text = highlight(item.typeName)

        if (item.obj.description.isNullOrEmpty()) {
            holder.textDescription.visibility = View.GONE
        } else {
            holder.textDescription.visibility = View.VISIBLE
            holder.textDescription.text = highlight(item.obj.description ?: "")
            holder.textDescription.maxLines = if (isFilterActive) 2 else 1
        }

        if (item.obj.quantity == null) {
            holder.textQuantity.visibility = View.GONE
        } else {
            holder.textQuantity.visibility = View.VISIBLE
            holder.textQuantity.text = "Quantità: ${item.obj.quantity}"
        }

        val isSelected = selectedIds.contains(id)
        holder.rootSelectable.isSelected = isSelected

        holder.textMenu.visibility = if (selectionMode) View.GONE else View.VISIBLE

        holder.contentArea.setOnClickListener {
            onToggleSelection(id)
        }

        holder.contentArea.setOnLongClickListener {
            onToggleSelection(id)
            true
        }

        holder.textMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menu.add("Modifica")
            popup.menu.add("Elimina")

            popup.setOnMenuItemClickListener {
                when (it.title) {
                    "Modifica" -> onEdit(id)
                    "Elimina" -> onDelete(id)
                }
                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ObjectWithType>) {
        val diffCallback = ObjectDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateSelection(selectedIds: Set<Int>, selectionMode: Boolean) {
        this.selectedIds = selectedIds
        this.selectionMode = selectionMode
        notifyDataSetChanged()
    }

    fun updateFilterState(isFilterActive: Boolean) {
        this.isFilterActive = isFilterActive
        notifyDataSetChanged()
    }

    fun updateQuery(query: String) {
        currentQuery = query
        notifyDataSetChanged()
    }

    private fun highlight(text: String): SpannableString {
        if (currentQuery.isBlank()) return SpannableString(text)

        val start = text.lowercase().indexOf(currentQuery.lowercase())
        if (start < 0) return SpannableString(text)

        val end = start + currentQuery.length

        return SpannableString(text).apply {
            setSpan(
                BackgroundColorSpan(Color.YELLOW),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    class ObjectDiffCallback(
        private val oldList: List<ObjectWithType>,
        private val newList: List<ObjectWithType>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].obj.id == newList[newItemPosition].obj.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return oldItem.obj == newItem.obj &&
                    oldItem.typeName == newItem.typeName
        }
    }
}