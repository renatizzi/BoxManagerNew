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

                menu.add(0, MENU_EDIT, 0, R.string.menu_edit)

                menu.add(0, MENU_DELETE, 1, R.string.menu_delete)

                setOnMenuItemClickListener {

                    when (it.itemId) {

                        MENU_EDIT ->
                            onEdit(item)

                        MENU_DELETE ->
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

    companion object {
        private const val MENU_EDIT = 1
        private const val MENU_DELETE = 2
    }
}