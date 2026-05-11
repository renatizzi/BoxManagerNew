package com.example.boxmanagernew.ui.categories

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Category

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

            selectedCategoryId =
                if (selectedCategoryId == category.id) {
                    null
                } else {
                    category.id
                }

            notifyDataSetChanged()
        }

        holder.textMenu.setOnClickListener { view ->

            val popup =
                PopupMenu(view.context, view)

            popup.menu.add("Modifica")
            popup.menu.add("Elimina")

            popup.setOnMenuItemClickListener {

                when (it.title) {

                    "Modifica" -> onEdit(category)

                    "Elimina" -> onDelete(category)
                }

                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int =
        items.size

    fun updateSelection(id: Int?) {

        selectedCategoryId = id

        notifyDataSetChanged()
    }

    fun updateQuery(query: String) {

        currentQuery = query

        notifyDataSetChanged()
    }

    fun updateData(
        newItems: List<Category>
    ) {

        items = newItems

        notifyDataSetChanged()
    }

    private fun highlight(
        text: String
    ): SpannableString {

        if (
            currentQuery.length < 3
        ) {
            return SpannableString(text)
        }

        val lowerText =
            text.lowercase()

        val lowerQuery =
            currentQuery.lowercase()

        val start =
            lowerText.indexOf(lowerQuery)

        if (start < 0) {
            return SpannableString(text)
        }

        val end =
            start + lowerQuery.length

        return SpannableString(text).apply {

            setSpan(
                BackgroundColorSpan(
                    Color.YELLOW
                ),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}