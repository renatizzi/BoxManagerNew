package com.example.boxmanagernew.ui.globalsearch

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.search.model.SearchMessage

class GlobalSearchAdapter(
    private val items: List<SearchMessage>
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

        val item =
            items[position]

        holder.textMessage.text =
            item.text

        if (item.fromUser) {

            holder.textMessage.setTextColor(
                holder.itemView.context.getColor(
                    R.color.primary_button
                )
            )

            holder.textMessage.setTypeface(
                null,
                Typeface.ITALIC
            )

        } else {

            holder.textMessage.setTextColor(
                holder.itemView.context.getColor(
                    R.color.text_primary
                )
            )

            holder.textMessage.setTypeface(
                null,
                Typeface.NORMAL
            )
        }
    }

    override fun getItemCount(): Int {

        return items.size
    }
}