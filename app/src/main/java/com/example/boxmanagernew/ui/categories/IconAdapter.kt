package com.example.boxmanagernew.ui.categories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class IconAdapter(
    private val iconNames: List<String>,
    private val onIconSelected: (String) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedIcon: String? = null

    fun setSelectedIcon(iconName: String?) {
        selectedIcon = iconName
        notifyDataSetChanged()
    }

    fun getSelectedIcon(): String? = selectedIcon

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val image = ImageView(parent.context).apply {
            layoutParams = LinearLayout.LayoutParams(120, 120)
            setPadding(16, 16, 16, 16)
        }
        return IconViewHolder(image)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconName = iconNames[position]

        holder.image.setImageResource(
            IconMapper.getIconRes(iconName)
        )

        // Highlight selezione
        if (iconName == selectedIcon) {
            val border = GradientDrawable().apply {
                setColor(Color.parseColor("#22007AFF"))
                setStroke(5, Color.parseColor("#007AFF"))
                cornerRadius = 20f
            }
            holder.image.background = border
        } else {
            holder.image.background = null
        }

        holder.image.setOnClickListener {
            selectedIcon = iconName
            notifyDataSetChanged()
            onIconSelected(iconName)
        }
    }

    override fun getItemCount(): Int = iconNames.size

    class IconViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)
}