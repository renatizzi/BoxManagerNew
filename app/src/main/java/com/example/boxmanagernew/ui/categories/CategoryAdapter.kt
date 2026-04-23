package com.example.boxmanagernew.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Category

class CategoryAdapter(
    private var items: List<Category>,
    private val onUpdate: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var expandedPosition: Int = -1

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textCategoryName)
        val imageIcon: ImageView = itemView.findViewById(R.id.imageCategoryIcon)

        val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutHeader)
        val layoutExpanded: LinearLayout = itemView.findViewById(R.id.layoutExpanded)

        val editName: EditText = itemView.findViewById(R.id.editCategoryName)
        val recyclerIcons: RecyclerView = itemView.findViewById(R.id.recyclerIcons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val category = items[position]

        holder.textName.text = category.name

        // 🔴 FIX: evita reset continuo mentre scrivi
        if (!holder.editName.hasFocus()) {
            holder.editName.setText(category.name)
        }

        val iconRes = IconMapper.getIconRes(category.icon)
        holder.imageIcon.setImageResource(iconRes)

        val isExpanded = position == expandedPosition
        holder.layoutExpanded.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.layoutHeader.setOnClickListener {
            val oldPos = expandedPosition
            expandedPosition = if (isExpanded) -1 else position

            if (oldPos != -1) notifyItemChanged(oldPos)
            notifyItemChanged(position)
        }

        val icons = IconMapper.getAvailableIcons(items.map { it.icon })

        holder.recyclerIcons.layoutManager = GridLayoutManager(holder.itemView.context, 4)
        holder.recyclerIcons.adapter = object : RecyclerView.Adapter<IconViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
                val img = ImageView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(120, 120)
                    setPadding(16, 16, 16, 16)
                }
                return IconViewHolder(img)
            }

            override fun onBindViewHolder(iconHolder: IconViewHolder, i: Int) {
                val item = icons[i]
                iconHolder.image.setImageResource(item.resId)
            }

            override fun getItemCount(): Int = icons.size
        }

        // 🔴 FIX REALE: salvataggio su perdita focus + confronto corretto
        holder.editName.setOnFocusChangeListener { v, hasFocus ->

            if (!hasFocus) {

                val newName = holder.editName.text.toString().trim()

                if (newName.isEmpty()) {
                    holder.editName.error = "Nome obbligatorio"
                    holder.editName.setText(category.name)
                    return@setOnFocusChangeListener
                }

                val duplicate = items.any {
                    it.name.equals(newName, true) && it.id != category.id
                }

                if (duplicate) {
                    Toast.makeText(v.context, "Categoria già esistente", Toast.LENGTH_SHORT).show()
                    holder.editName.setText(category.name)
                    return@setOnFocusChangeListener
                }

                if (newName != category.name) {
                    onUpdate(category.copy(name = newName))
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Category>) {
        items = newItems
        notifyDataSetChanged()
    }

    class IconViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)
}