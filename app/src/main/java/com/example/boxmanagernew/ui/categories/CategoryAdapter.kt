package com.example.boxmanagernew.ui.categories

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Category
import com.example.boxmanagernew.ui.common.SimpleSearchHighlight

class CategoryAdapter(
    private var items: List<Category>,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategoryId: Int? = null
    private var currentQuery: String = ""

    inner class CategoryViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val rootSelectable: View =
            itemView.findViewById(R.id.rootSelectable)

        val contentArea: View =
            itemView.findViewById(R.id.contentArea)

        val textName: TextView =
            itemView.findViewById(R.id.textCategoryName)

        val imageIcon: ImageView =
            itemView.findViewById(R.id.imageCategoryIcon)

        val textMenu: TextView =
            itemView.findViewById(R.id.textMenu)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_category,
                    parent,
                    false
                )

        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        val category =
            items[position]

        holder.textName.text =
            highlight(category.name)

        holder.imageIcon.setImageResource(
            IconMapper.getIconRes(category.icon)
        )

        val isSelected =
            category.id == selectedCategoryId

        holder.rootSelectable.isSelected =
            isSelected

        holder.contentArea.setOnClickListener {

            val previousSelected =
                selectedCategoryId

            selectedCategoryId =
                if (selectedCategoryId == category.id) {
                    null
                } else {
                    category.id
                }

            previousSelected?.let { oldId ->

                val oldIndex =
                    items.indexOfFirst {
                        it.id == oldId
                    }

                if (oldIndex != -1) {
                    notifyItemChanged(oldIndex)
                }
            }

            val newIndex =
                items.indexOfFirst {
                    it.id == selectedCategoryId
                }

            if (newIndex != -1) {
                notifyItemChanged(newIndex)
            }
        }

        holder.textMenu.setOnClickListener { view ->

            val popup =
                PopupMenu(view.context, view)

            popup.menu.add(0, MENU_EDIT, 0, R.string.menu_edit)
            popup.menu.add(0, MENU_DELETE, 1, R.string.menu_delete)

            popup.setOnMenuItemClickListener {

                when (it.itemId) {

                    MENU_EDIT -> onEdit(category)

                    MENU_DELETE -> onDelete(category)
                }

                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int =
        items.size

    fun updateSelection(id: Int?) {

        val oldId =
            selectedCategoryId

        selectedCategoryId = id

        oldId?.let {

            val oldIndex =
                items.indexOfFirst { item ->
                    item.id == it
                }

            if (oldIndex != -1) {
                notifyItemChanged(oldIndex)
            }
        }

        id?.let {

            val newIndex =
                items.indexOfFirst { item ->
                    item.id == it
                }

            if (newIndex != -1) {
                notifyItemChanged(newIndex)
            }
        }
    }

    fun updateQuery(query: String) {

        currentQuery = query

        notifyDataSetChanged()
    }

    fun updateData(
        newItems: List<Category>
    ) {

        val diffCallback =
            CategoryDiffCallback(
                items,
                newItems
            )

        val diffResult =
            DiffUtil.calculateDiff(diffCallback)

        items = newItems

        diffResult.dispatchUpdatesTo(this)
    }

    private fun highlight(
        text: String
    ): SpannableString {

        return SimpleSearchHighlight.paint(
            text,
            currentQuery
        )
    }

    class CategoryDiffCallback(
        private val oldList: List<Category>,
        private val newList: List<Category>
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

            return oldList[oldItemPosition] ==
                    newList[newItemPosition]
        }
    }

    companion object {
        private const val MENU_EDIT = 1
        private const val MENU_DELETE = 2
    }
}