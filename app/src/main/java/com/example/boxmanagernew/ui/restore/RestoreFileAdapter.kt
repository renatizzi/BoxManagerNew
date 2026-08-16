package com.example.boxmanagernew.ui.restore

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.boxmanagernew.R
import com.example.boxmanagernew.ui.backup.BackupZipPersister
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RestoreFileAdapter(
    private val onSelect: (BackupZipPersister.ZipFileItem) -> Unit
) : RecyclerView.Adapter<RestoreFileAdapter.ViewHolder>() {

    private var data: List<BackupZipPersister.ZipFileItem> = emptyList()
    private var selectedUri: Uri? = null

    inner class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.tvRestoreFileName)
        val date: TextView = view.findViewById(R.id.tvRestoreFileDate)
        val marker: View = view.findViewById(R.id.viewRestoreFileSelected)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restore_file, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = data[position]
        val selected = item.uri == selectedUri

        holder.name.text = item.name
        holder.date.text = formatDate(item.lastModified)
        holder.itemView.isSelected = selected
        holder.marker.visibility =
            if (selected) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            onSelect(item)
        }
    }

    override fun getItemCount(): Int = data.size

    fun submit(
        files: List<BackupZipPersister.ZipFileItem>,
        selected: Uri?
    ) {
        data = files
        selectedUri = selected
        notifyDataSetChanged()
    }

    fun select(uri: Uri?) {
        selectedUri = uri
        notifyDataSetChanged()
    }

    private fun formatDate(
        lastModified: Long
    ): String {

        if (lastModified <= 0L) {
            return ""
        }

        return SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date(lastModified))
    }
}
