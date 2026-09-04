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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.util.CanonicalNormalizer
import com.example.boxmanagernew.util.SimpleSearch

class BoxAdapter(
    private var items: List<Box>,
    private var categories: List<CategoryEntity>,
    private val onClick: (Box) -> Unit,
    private val onEdit: (Box) -> Unit,
    private val onDelete: (Box) -> Unit,
    private val onShowQrLabel: (Box) -> Unit,
    private val onToggleSelection: (Box) -> Unit
) : RecyclerView.Adapter<BoxAdapter.BoxViewHolder>() {

    private var selectedIds: Set<Int> = emptySet()
    private var selectionMode: Boolean = false
    private var currentQuery: String = ""
    private var highlightInflect: Boolean = false
    private var highlightInline: Boolean = false

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

        holder.textBoxName.text = highlight(box.name)

        val category = categories.find { it.id == box.categoryId }

        val categoryName = category?.name
            ?: holder.itemView.context.getString(R.string.category_unknown)
        val positionText = box.position
        val modified =
            UiUtils.formatDate(
                box.lastModified
            )

        holder.textSubtitle.text =
            buildSubtitle(
                categoryName,
                positionText,
                modified
            )

        if (category != null) {

            val iconRes =
                IconMapper.getIconRes(category.icon)

            holder.imageCategory.setImageResource(iconRes)

        } else {

            holder.imageCategory.setImageResource(
                R.drawable.outline_browse_24
            )
        }

        val isSelected =
            selectedIds.contains(box.id)

        holder.rootSelectable.isSelected = isSelected

        val selectedCount = selectedIds.size

        holder.textMenu.visibility = when {

            selectedCount == 0 ->
                View.VISIBLE

            selectedCount == 1 && isSelected ->
                View.VISIBLE

            else ->
                View.GONE
        }

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

        holder.textMenu.setOnClickListener { view ->

            val popup =
                PopupMenu(view.context, view)

            popup.menu.add(0, MENU_EDIT, 0, R.string.menu_edit)
            popup.menu.add(0, MENU_DELETE, 1, R.string.menu_delete)
            popup.menu.add(0, MENU_VIEW_QR, 2, R.string.menu_view_qr_label)

            popup.setOnMenuItemClickListener {

                when (it.itemId) {

                    MENU_EDIT -> onEdit(box)

                    MENU_VIEW_QR ->
                        onShowQrLabel(box)

                    MENU_DELETE -> onDelete(box)
                }

                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Box>) {

        val diffCallback =
            BoxDiffCallback(items, newItems)

        val diffResult =
            DiffUtil.calculateDiff(diffCallback)

        items = newItems

        diffResult.dispatchUpdatesTo(this)

        if (currentQuery.isNotBlank()) {

            notifyItemRangeChanged(
                0,
                items.size
            )
        }
    }

    fun updateCategories(newCategories: List<CategoryEntity>) {

        categories = newCategories

        notifyDataSetChanged()
    }

    fun updateSelection(
        selectedIds: Set<Int>,
        selectionMode: Boolean
    ) {

        this.selectedIds = selectedIds
        this.selectionMode = selectionMode

        notifyDataSetChanged()
    }

    fun updateQuery(
        query: String,
        inflect: Boolean = false,
        inline: Boolean = false
    ) {

        currentQuery = query
        highlightInflect = inflect
        highlightInline = inline

        notifyDataSetChanged()
    }

    class BoxDiffCallback(
        private val oldList: List<Box>,
        private val newList: List<Box>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int =
            oldList.size

        override fun getNewListSize(): Int =
            newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {

            return oldList[oldItemPosition].id ==
                    newList[newItemPosition].id
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int
        ): Boolean {

            val oldItem =
                oldList[oldItemPosition]

            val newItem =
                newList[newItemPosition]

            return oldItem == newItem
        }
    }

    private fun highlight(text: String): SpannableString {

        return highlightRanges(text)
    }

    private fun buildSubtitle(
        category: String,
        position: String,
        modified: String
    ): SpannableString {

        val parts =
            listOf(
                category,
                position,
                modified
            ).filter { part ->
                part.isNotBlank()
            }

        val fullText =
            parts.joinToString(
                " • "
            )

        return highlightRanges(fullText)
    }

    private fun highlightRanges(
        text: String
    ): SpannableString {

        val result =
            SpannableString(text)

        if (
            currentQuery.isBlank() ||
            text.isBlank()
        ) {
            return result
        }

        val ranges =
            if (highlightInline) {

                SimpleSearch.highlightRanges(text, currentQuery)

            } else {

                CanonicalNormalizer
                    .matchingWordRanges(
                        text,
                        currentQuery,
                        inflect = highlightInflect
                    )
            }

        ranges
            .forEach { range ->

                result.setSpan(
                    BackgroundColorSpan(
                        Color.YELLOW
                    ),
                    range.first,
                    range.last + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

        return result
    }

    companion object {
        private const val MENU_EDIT = 1
        private const val MENU_DELETE = 2
        private const val MENU_VIEW_QR = 3
    }
}