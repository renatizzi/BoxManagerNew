package com.example.boxmanagernew.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.local.entity.ObjectEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.trash.TrashStore
import com.example.boxmanagernew.data.trash.TrashStoreProvider
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.CreatedByResolver
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            !ArchivioCompletoNav.allowActivity(
                this,
                PremiumFeature.TRASH
            )
        ) {
            return
        }

        setContentView(R.layout.activity_trash)
        setupAppShell()
        setupPageHeader(
            title = getString(R.string.page_trash_title),
            subtitle = getString(R.string.page_trash_subtitle)
        )
        setupBottomNav()

        val db = DatabaseProvider.getDatabase(applicationContext)
        val boxRepo = BoxRepositoryImpl(db.boxDao())
        val objectRepo =
            ObjectRepositoryImpl(db.objectDao(), db.objectTypeDao())
        val trashStore =
            TrashStoreProvider.create(db, boxRepo, objectRepo)

        val listContainer = findViewById<LinearLayout>(R.id.trashListContainer)
        val emptyView = findViewById<TextView>(R.id.textTrashEmpty)

        findViewById<Button>(R.id.btnEmptyTrash).setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage(R.string.trash_empty_confirm)
                .setPositiveButton(R.string.common_yes) { _, _ ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            trashStore.emptyTrash(
                                CreatedByResolver.current(
                                    this@TrashActivity
                                )
                            )
                        }
                        reloadList(
                            trashStore,
                            listContainer,
                            emptyView
                        )
                    }
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                trashStore.purgeExpired()
            }
            reloadList(trashStore, listContainer, emptyView)
        }
    }

    private suspend fun reloadList(
        trashStore: TrashStore,
        listContainer: LinearLayout,
        emptyView: TextView
    ) {
        val db = DatabaseProvider.getDatabase(applicationContext)
        val typeNames =
            withContext(Dispatchers.IO) {
                db.objectTypeDao().getAllTypesSync()
                    .associate { it.id to it.name }
            }

        val (boxes, objects) =
            withContext(Dispatchers.IO) {
                trashStore.listTrashBoxes() to
                    trashStore.listTrashObjects()
            }

        listContainer.removeAllViews()
        val inflater = LayoutInflater.from(this)

        if (boxes.isEmpty() && objects.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            return
        }
        emptyView.visibility = View.GONE

        val trashedBoxIds = boxes.map { it.id }.toSet()

        for (box in boxes) {
            listContainer.addView(
                bindRow(
                    inflater,
                    listContainer,
                    title = box.name,
                    subtitle = formatSubtitle(
                        getString(R.string.trash_type_box),
                        box.deletedAt
                    ),
                    onRestore = {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                trashStore.undoSoftDeleteBox(box.id)
                            }
                            reloadList(
                                trashStore,
                                listContainer,
                                emptyView
                            )
                        }
                    },
                    onDeletePermanent = {
                        confirmPermanentDelete {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    trashStore.hardDeleteBox(
                                        box.id,
                                        CreatedByResolver.current(
                                            this@TrashActivity
                                        )
                                    )
                                }
                                reloadList(
                                    trashStore,
                                    listContainer,
                                    emptyView
                                )
                            }
                        }
                    }
                )
            )
        }

        for (obj in objects) {
            if (obj.boxId in trashedBoxIds) {
                continue
            }
            val title = objectTitle(obj, typeNames)
            listContainer.addView(
                bindRow(
                    inflater,
                    listContainer,
                    title = title,
                    subtitle = formatSubtitle(
                        getString(R.string.trash_type_object),
                        obj.deletedAt
                    ),
                    onRestore = {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                trashStore.restoreObjectFromTrash(obj.id)
                            }
                            reloadList(
                                trashStore,
                                listContainer,
                                emptyView
                            )
                        }
                    },
                    onDeletePermanent = {
                        confirmPermanentDelete {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    trashStore.hardDeleteObjects(
                                        listOf(obj.id),
                                        CreatedByResolver.current(
                                            this@TrashActivity
                                        )
                                    )
                                }
                                reloadList(
                                    trashStore,
                                    listContainer,
                                    emptyView
                                )
                            }
                        }
                    }
                )
            )
        }
    }

    private fun objectTitle(
        obj: ObjectEntity,
        typeNames: Map<Int, String>
    ): String {
        return typeNames[obj.typeObjectId]
            ?: obj.description.orEmpty().ifBlank {
                getString(R.string.trash_type_object)
            }
    }

    private fun formatSubtitle(typeLabel: String, deletedAt: Long?): String {
        val whenDeleted =
            deletedAt?.let { UiUtils.formatDate(it) }.orEmpty()
        return "$typeLabel • $whenDeleted"
    }

    private fun bindRow(
        inflater: LayoutInflater,
        parent: LinearLayout,
        title: String,
        subtitle: String,
        onRestore: () -> Unit,
        onDeletePermanent: () -> Unit
    ): View {
        val row =
            inflater.inflate(R.layout.item_trash, parent, false)
        row.findViewById<TextView>(R.id.textTrashTitle).text = title
        row.findViewById<TextView>(R.id.textTrashSubtitle).text =
            subtitle
        row.findViewById<Button>(R.id.btnTrashRestore)
            .setOnClickListener { onRestore() }
        row.findViewById<Button>(R.id.btnTrashDeletePermanent)
            .setOnClickListener { onDeletePermanent() }
        return row
    }

    private fun confirmPermanentDelete(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(R.string.dialog_delete_confirm)
            .setPositiveButton(R.string.common_yes) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }
}
