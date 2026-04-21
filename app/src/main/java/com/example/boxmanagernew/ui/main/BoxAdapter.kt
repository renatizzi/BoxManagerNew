package com.example.boxmanagernew.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.ui.categories.IconMapper

class BoxAdapter(
    private var items: List<Box>,
    private var categories: List<CategoryEntity>,
    private val onClick: (Box) -> Unit,
    private val onEdit: (Box) -> Unit,
    private val onDelete: (Box) -> Unit,
    private val onToggleSelection: (Box) -> Unit
) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    private var selectedIds: Set<Int> = emptySet()
    private var selectionMode: Boolean = false

    inner class BoxViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconArea: FrameLayout = itemView.findViewById(R.id.iconArea)
        val imageOpenBox: ImageView = itemView.findViewById(R.id.imageOpenBox)
        val contentArea: View = itemView.findViewById(R.id.contentArea)
        val rootSelectable: View = itemView.findViewById(R.id.rootSelectable)
        val textBoxName: TextView = itemView.findViewById(R.id.textBoxName)
        val textSubtitle: TextView = itemView.findViewById(R.id.textSubtitle)
        val textMenu: TextView = itemView.findViewById(R.id.textMenu)
        val imageCategory: ImageView = itemView.findViewById(R.id.imageCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoxViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_box, parent, false)
        return BoxViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoxViewHolder, position: Int) {
        val box = items[position]

        holder.textBoxName.text = box.name

        val category = categories.find { it.id == box.categoryId }
        val categoryName = category?.name ?: "Categoria sconosciuta"

        val positionText = if (box.position.isBlank()) "" else " • ${box.position}"
        holder.textSubtitle.text = categoryName + positionText

        if (category != null) {
            val iconRes = IconMapper.getIconRes(category.icon)
            holder.imageCategory.setImageResource(iconRes)
        } else {
            holder.imageCategory.setImageResource(R.drawable.ic_launcher_foreground)
        }

        val isSelected = selectedIds.contains(box.id)
        holder.rootSelectable.isSelected = isSelected

        holder.textMenu.visibility = if (selectionMode) View.GONE else View.VISIBLE

        holder.iconArea.setOnClickListener {
            holder.imageOpenBox.animate()
                .alpha(0.3f)
                .setDuration(80)
                .withEndAction {
                    holder.imageOpenBox.animate()
                        .alpha(1f)
                        .setDuration(120)
                        .start()
                }
                .start()

            onClick(box)
        }

        holder.contentArea.setOnClickListener {
            onToggleSelection(box)
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

    fun updateCategories(newCategories: List<CategoryEntity>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    fun updateSelection(selectedIds: Set<Int>, selectionMode: Boolean) {
        this.selectedIds = selectedIds
        this.selectionMode = selectionMode
        notifyDataSetChanged()
    }
}