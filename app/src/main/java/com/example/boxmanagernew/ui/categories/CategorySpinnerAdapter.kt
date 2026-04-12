package com.example.boxmanagernew.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.content.Context
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.entity.CategoryEntity

class CategorySpinnerAdapter(
    context: Context,
    private val items: List<CategoryEntity>
) : ArrayAdapter<CategoryEntity>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_category_spinner, parent, false)

        val item = items[position]

        val text = view.findViewById<TextView>(R.id.textName)
        val icon = view.findViewById<ImageView>(R.id.imageIcon)

        text.text = item.name
        icon.setImageResource(IconMapper.getIconRes(item.icon))

        return view
    }
}