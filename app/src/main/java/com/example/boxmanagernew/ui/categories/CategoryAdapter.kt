package com.example.boxmanagernew.ui.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Category

class CategoryAdapter(
    private var items: List<Category>,
    private val onUpdate: (Category) -> Unit,
    private val onDeleteRequest: (Category) -> Unit,
    private val onEditStart: (Category) -> Unit,
    private val onEditEnd: () -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var expandedPosition: Int = -1
    private var selectedCategoryId: Int? = null

    fun updateSelection(id: Int?) {
        selectedCategoryId = id
        notifyDataSetChanged()
    }

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

        val isSelected = category.id == selectedCategoryId
        holder.layoutHeader.isSelected = isSelected

        holder.textName.text = category.name

        if (!holder.editName.hasFocus()) {
            holder.editName.setText(category.name)
        }

        holder.editName.setSingleLine(true)
        holder.editName.imeOptions = EditorInfo.IME_ACTION_DONE

        holder.editName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val newName = holder.editName.text.toString().trim()

                if (newName.isEmpty()) {
                    holder.editName.error = "Nome obbligatorio"
                    return@setOnEditorActionListener true
                }

                val duplicate = items.any {
                    it.name.equals(newName, true) && it.id != category.id
                }

                if (duplicate) {
                    holder.editName.setText(category.name)
                    onEditEnd()
                    return@setOnEditorActionListener true
                }

                if (newName != category.name) {
                    onUpdate(category.copy(name = newName))
                }

                val imm = holder.itemView.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(holder.itemView.windowToken, 0)

                holder.editName.clearFocus()
                onEditEnd()

                true
            } else false
        }

        holder.imageIcon.setImageResource(IconMapper.getIconRes(category.icon))

        val isExpanded = position == expandedPosition
        holder.layoutExpanded.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.layoutHeader.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener

            val oldPos = expandedPosition
            expandedPosition = if (currentPos == expandedPosition) -1 else currentPos

            if (oldPos != -1) notifyItemChanged(oldPos)
            notifyItemChanged(currentPos)

            if (expandedPosition == currentPos) {
                onEditStart(items[currentPos])
            } else {
                onEditEnd()
            }
        }

        holder.layoutHeader.setOnLongClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos == RecyclerView.NO_POSITION) return@setOnLongClickListener true

            onEditEnd()
            onDeleteRequest(items[currentPos])
            true
        }

        holder.recyclerIcons.layoutManager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        holder.recyclerIcons.adapter = object : RecyclerView.Adapter<IconViewHolder>() {

            private val iconNames = listOf(
                "outline_checkroom_24",
                "outline_fastfood_24",
                "outline_handyman_24",
                "outline_carpenter_24",
                "outline_ink_pen_24",
                "outline_garage_money_24",
                "outline_passport_24",
                "outline_broadcast_on_home_24",
                "outline_tools_power_drill_24",
                "outline_photo_frame_24",
                "outline_library_music_24",
                "outline_box_24",
                "outline_menu_book_24",
                "outline_medical_services_24",
                "outline_money_bag_24",
                "outline_browse_24"
            )

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
                val img = ImageView(parent.context).apply {
                    layoutParams = LinearLayout.LayoutParams(120, 120)
                    setPadding(16, 16, 16, 16)
                }
                return IconViewHolder(img)
            }

            override fun onBindViewHolder(iconHolder: IconViewHolder, i: Int) {
                val iconName = iconNames[i]

                iconHolder.image.setImageResource(IconMapper.getIconRes(iconName))

                if (iconName == category.icon) {
                    iconHolder.image.setBackgroundResource(R.drawable.bg_selected_item)
                } else {
                    iconHolder.image.background = null
                }

                iconHolder.image.setOnClickListener {

                    val imm = holder.itemView.context
                        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(holder.itemView.windowToken, 0)

                    holder.editName.clearFocus()
                    onEditEnd()

                    if (iconName != category.icon) {
                        onUpdate(category.copy(icon = iconName))
                    }
                }
            }

            override fun getItemCount(): Int = iconNames.size
        }

        holder.editName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {

                val newName = holder.editName.text.toString().trim()

                if (newName.isEmpty()) {
                    holder.editName.error = "Nome obbligatorio"
                    holder.editName.setText(category.name)
                    onEditEnd()
                    return@setOnFocusChangeListener
                }

                val duplicate = items.any {
                    it.name.equals(newName, true) && it.id != category.id
                }

                if (duplicate) {
                    holder.editName.setText(category.name)
                    onEditEnd()
                    return@setOnFocusChangeListener
                }

                if (newName != category.name) {
                    onUpdate(category.copy(name = newName))
                }

                onEditEnd()
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