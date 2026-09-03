package com.example.boxmanagernew.ui.boxdetail

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.domain.model.ObjectWithType
import com.example.boxmanagernew.domain.search.SearchConfiguration
import com.example.boxmanagernew.ui.common.SimpleSearchHighlight
import com.example.boxmanagernew.util.CanonicalNormalizer

class ObjectAdapter(
    private var items: List<ObjectWithType>,
    private val onClick: (Int) -> Unit,
    private val onToggleSelection: (Int) -> Unit,
    private val onEdit: (Int) -> Unit,
    private val onMove: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder>() {

    private var selectedIds: Set<Int> = emptySet()
    private var selectionMode = false
    private var isFilterActive = false
    private var currentQuery = ""
    private var matchWholeWords = false

    inner class ObjectViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val rootSelectable: View =
            itemView.findViewById(
                R.id.rootSelectable
            )

        val iconArea: FrameLayout =
            itemView.findViewById(
                R.id.iconArea
            )

        val contentArea: View =
            itemView.findViewById(
                R.id.contentArea
            )

        val textName: TextView =
            itemView.findViewById(
                R.id.textName
            )

        val textDescription: TextView =
            itemView.findViewById(
                R.id.textDescription
            )

        val textQuantity: TextView =
            itemView.findViewById(
                R.id.textQuantity
            )

        val textMenu: TextView =
            itemView.findViewById(
                R.id.textMenu
            )
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ObjectViewHolder {

        val view =
            LayoutInflater.from(
                parent.context
            ).inflate(
                R.layout.item_object,
                parent,
                false
            )

        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ObjectViewHolder,
        position: Int
    ) {

        val item =
            items[position]

        val id =
            item.obj.id

        holder.textName.text =
            highlight(item.typeName)

        if (
            item.obj.description
                .isNullOrEmpty()
        ) {

            holder.textDescription.visibility =
                View.GONE

        } else {

            holder.textDescription.visibility =
                View.VISIBLE

            holder.textDescription.text =
                highlight(
                    item.obj.description ?: ""
                )

            holder.textDescription.maxLines =
                if (isFilterActive) 2
                else 1
        }

        if (
            item.obj.quantity == null
        ) {

            holder.textQuantity.visibility =
                View.GONE

        } else {

            holder.textQuantity.visibility =
                View.VISIBLE

            holder.textQuantity.text =
                "Quantità: ${item.obj.quantity}"
        }

        val isSelected =
            selectedIds.contains(id)

        holder.rootSelectable.isSelected =
            isSelected

        val selectedCount =
            selectedIds.size

        holder.textMenu.visibility =
            when {

                selectedCount == 0 ->
                    View.VISIBLE

                selectedCount == 1 &&
                        isSelected ->
                    View.VISIBLE

                else ->
                    View.GONE
            }

        holder.contentArea
            .setOnClickListener {

                onToggleSelection(id)
            }

        holder.textMenu
            .setOnClickListener { view ->

                val popup =
                    PopupMenu(
                        view.context,
                        view
                    )

                popup.menu.add(0, MENU_EDIT, 0, R.string.menu_edit)
                popup.menu.add(0, MENU_MOVE, 1, R.string.menu_move)
                popup.menu.add(0, MENU_DELETE, 2, R.string.menu_delete)

                popup.setOnMenuItemClickListener {

                    when (it.itemId) {

                        MENU_EDIT ->
                            onEdit(id)

                        MENU_MOVE ->
                            onMove(id)

                        MENU_DELETE ->
                            onDelete(id)
                    }

                    true
                }

                popup.show()
            }
    }

    override fun getItemCount() =
        items.size

    fun updateData(
        newItems: List<ObjectWithType>
    ) {

        val diff =
            DiffUtil.calculateDiff(
                ObjectDiffCallback(
                    items,
                    newItems
                )
            )

        items =
            newItems

        diff.dispatchUpdatesTo(this)
    }

    fun updateSelection(
        selectedIds: Set<Int>,
        selectionMode: Boolean
    ) {

        this.selectedIds =
            selectedIds

        this.selectionMode =
            selectionMode

        notifyDataSetChanged()
    }

    fun updateFilterState(
        active: Boolean
    ) {

        isFilterActive =
            active

        notifyDataSetChanged()
    }

    fun updateQuery(
        query: String,
        wholeWord: Boolean = false
    ) {

        currentQuery =
            query

        matchWholeWords =
            wholeWord

        notifyDataSetChanged()
    }

    private fun highlight(
        text: String
    ): SpannableString {

        if (!matchWholeWords) {

            return SimpleSearchHighlight.paint(
                text,
                currentQuery
            )
        }

        val result =
            SpannableString(text)

        CanonicalNormalizer
            .matchingWordRanges(
                text,
                SearchConfiguration.locationHighlightQuery(
                    currentQuery
                )
            )
            .forEach { range ->

                result.setSpan(
                    BackgroundColorSpan(
                        Color.YELLOW
                    ),
                    range.first,
                    range.last + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

        return result
    }

    class ObjectDiffCallback(
        private val oldList:
        List<ObjectWithType>,

        private val newList:
        List<ObjectWithType>

    ) : DiffUtil.Callback() {

        override fun getOldListSize() =
            oldList.size

        override fun getNewListSize() =
            newList.size

        override fun areItemsTheSame(
            oldPos: Int,
            newPos: Int
        ) =
            oldList[oldPos]
                .obj.id ==
                    newList[newPos]
                        .obj.id

        override fun areContentsTheSame(
            oldPos: Int,
            newPos: Int
        ): Boolean {

            return oldList[oldPos] ==
                    newList[newPos]
        }
    }

    companion object {
        private const val MENU_EDIT = 1
        private const val MENU_MOVE = 2
        private const val MENU_DELETE = 3
    }
}