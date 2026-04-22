package com.example.boxmanagernew.ui.main

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
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
    private var currentQuery: String = ""

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

        // NAME
        holder.textBoxName.text = highlight(box.name)

        // CATEGORY + POSITION
        val category = categories.find { it.id == box.categoryId }
        val categoryName = category?.name ?: "Categoria sconosciuta"
        val positionText = box.position

        holder.textSubtitle.text = buildSubtitle(categoryName, positionText)

        // ICON
        if (category != null) {
            val iconRes = IconMapper.getIconRes(category.icon)
            holder.imageCategory.setImageResource(iconRes)
        } else {
            holder.imageCategory.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // SELECTION
        val isSelected = selectedIds.contains(box.id)
        holder.rootSelectable.isSelected = isSelected
        holder.textMenu.visibility = if (selectionMode) View.GONE else View.VISIBLE

        // CLICK ICON
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

        // SELECTION
        holder.contentArea.setOnClickListener { onToggleSelection(box) }
        holder.contentArea.setOnLongClickListener {
            onToggleSelection(box)
            true
        }

        // MENU
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

    fun updateQuery(query: String) {
        currentQuery = query
        notifyDataSetChanged()
    }

    // -------------------------
    // HIGHLIGHT FUNCTIONS
    // -------------------------

    private fun highlight(text: String): SpannableString {
        if (currentQuery.length < 3) return SpannableString(text)

        val lowerText = text.lowercase()
        val lowerQuery = currentQuery.lowercase()

        val start = lowerText.indexOf(lowerQuery)
        if (start < 0) return SpannableString(text)

        val end = start + lowerQuery.length

        return SpannableString(text).apply {
            setSpan(
                BackgroundColorSpan(Color.YELLOW),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun buildSubtitle(category: String, position: String): SpannableString {

        val fullText = if (position.isBlank()) {
            category
        } else {
            "$category • $position"
        }

        val spannable = SpannableString(fullText)

        if (currentQuery.length < 3) return spannable

        val query = currentQuery.lowercase()

        // CATEGORY
        val catStart = 0
        val catEnd = category.length

        if (category.lowercase().contains(query)) {
            spannable.setSpan(
                BackgroundColorSpan(Color.YELLOW),
                catStart,
                catEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // POSITION
        if (position.isNotBlank()) {
            val posStart = category.length + 3
            val posEnd = posStart + position.length

            if (position.lowercase().contains(query)) {
                spannable.setSpan(
                    BackgroundColorSpan(Color.YELLOW),
                    posStart,
                    posEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return spannable
    }
}