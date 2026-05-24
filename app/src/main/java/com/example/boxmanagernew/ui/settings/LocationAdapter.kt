package com.example.boxmanagernew.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.Location

class LocationAdapter(
    private var data: List<Location>,
    private val onEdit: (Location) -> Unit,
    private val onDelete: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val name:
                TextView =
            view.findViewById(
                R.id.textLocationName
            )

        val menu:
                TextView =
            view.findViewById(
                R.id.textMenu
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
                R.layout.item_location,
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
            data[position]

        holder.name.text =
            item.name

        holder.menu.setOnClickListener {

            PopupMenu(
                holder.itemView.context,
                holder.menu
            ).apply {

                menu.add("Modifica")

                menu.add("Elimina")

                setOnMenuItemClickListener {

                    when (it.title) {

                        "Modifica" ->
                            onEdit(item)

                        "Elimina" ->
                            onDelete(item)
                    }

                    true
                }

                show()
            }
        }
    }

    override fun getItemCount() =
        data.size

    fun updateData(
        list: List<Location>
    ) {

        data = list

        notifyDataSetChanged()
    }
}