package com.example.boxmanagernew.ui.categories

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Category

class CategoryAdapter(
    private var items: List<Category>,
    private val onUpdate: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var expandedPosition: Int = -1

    // 🔴 FIX REALE: lista icone UNA VOLTA SOLA
    private var stableIcons: List<IconItem> = emptyList()

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

        if (!holder.editName.hasFocus()) {
            holder.editName.setText(category.name)
        }

        holder.imageIcon.setImageResource(IconMapper.getIconRes(category.icon))

        val isExpanded = position == expandedPosition
        holder.layoutExpanded.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.layoutHeader.setOnClickListener {
            val oldPos = expandedPosition
            expandedPosition = if (isExpanded) -1 else position

            if (oldPos != -1) notifyItemChanged(oldPos)
            notifyItemChanged(position)
        }

        // 🔴 inizializza UNA VOLTA (mantiene le 16 icone)
        if (stableIcons.isEmpty()) {
            stableIcons = items.map { it.icon }
                .distinct()
                .map { IconItem(it, IconMapper.getIconRes(it)) }
        }

        holder.recyclerIcons.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        holder.recyclerIcons.adapter = object : RecyclerView.Adapter<IconViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
                val img = ImageView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(120, 120)
                    setPadding(16, 16, 16, 16)
                }
                return IconViewHolder(img)
            }

            override fun onBindViewHolder(iconHolder: IconViewHolder, i: Int) {
                val item = stableIcons[i]

                iconHolder.image.setImageResource(item.resId)

                // 🔴 highlight forte
                if (item.name == category.icon) {
                    val border = GradientDrawable().apply {
                        setColor(Color.parseColor("#22007AFF"))
                        setStroke(5, Color.parseColor("#007AFF"))
                        cornerRadius = 20f
                    }
                    iconHolder.image.background = border
                } else {
                    iconHolder.image.background = null
                }

                iconHolder.image.setOnClickListener {

                    val imm = holder.itemView.context
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(holder.itemView.windowToken, 0)

                    holder.editName.clearFocus()

                    if (item.name != category.icon) {
                        onUpdate(category.copy(icon = item.name))
                    }
                }
            }

            override fun getItemCount(): Int = stableIcons.size
        }

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