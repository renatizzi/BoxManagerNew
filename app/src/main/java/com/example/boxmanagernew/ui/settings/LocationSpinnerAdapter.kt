package com.example.boxmanagernew.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Location

class LocationSpinnerAdapter(
    context: Context,
    private val items: List<Location>
) : ArrayAdapter<Location>(
    context,
    0,
    items
) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        return createView(
            position,
            convertView,
            parent
        )
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        return createView(
            position,
            convertView,
            parent
        )
    }

    private fun createView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view =
            convertView
                ?: LayoutInflater
                    .from(context)
                    .inflate(
                        android.R.layout.simple_spinner_item,
                        parent,
                        false
                    )

        val text =
            view.findViewById<TextView>(
                android.R.id.text1
            )

        text.text =
            items[position].name

        return view
    }
}