package com.example.boxmanagernew.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.entity.CategoryEntity

class CategoryAdapter(
    private var items: List<CategoryEntity>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textCategoryName)
        val imageIcon: ImageView = itemView.findViewById(R.id.imageCategoryIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = items[position]

        holder.textName.text = category.name

        val iconRes = IconMapper.getIconRes(category.icon)
        holder.imageIcon.setImageResource(iconRes)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<CategoryEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}