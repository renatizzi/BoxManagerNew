package com.example.boxmanagernew.ui.boxdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.ObjectWithType

class ObjectAdapter(
    private var items: List<ObjectWithType>
) : RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder>() {

    inner class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textQuantity: TextView = itemView.findViewById(R.id.textQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_object, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        val item = items[position]

        holder.textName.text = item.typeName

        if (item.obj.description.isNullOrEmpty()) {
            holder.textDescription.visibility = View.GONE
        } else {
            holder.textDescription.visibility = View.VISIBLE
            holder.textDescription.text = item.obj.description
        }

        if (item.obj.quantity == null) {
            holder.textQuantity.visibility = View.GONE
        } else {
            holder.textQuantity.visibility = View.VISIBLE
            holder.textQuantity.text = "Quantità: ${item.obj.quantity}"
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ObjectWithType>) {
        items = newItems
        notifyDataSetChanged()
    }
}