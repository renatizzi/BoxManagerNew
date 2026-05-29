package com.example.boxmanagernew.ui.globalsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R

class GlobalSearchAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<GlobalSearchAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val textMessage: TextView =
            itemView.findViewById(
                R.id.textMessage
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_global_search_card,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.textMessage.text =
            items[position]
    }

    override fun getItemCount(): Int {

        return items.size
    }
}