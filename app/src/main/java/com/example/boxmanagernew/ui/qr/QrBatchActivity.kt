package com.example.boxmanagernew.ui.qr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.local.entity.CategoryEntity
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.domain.qr.BoxQrPayload
import com.example.boxmanagernew.domain.qr.LabelSheetSpec
import com.example.boxmanagernew.domain.qr.QrBatchFileNames
import com.example.boxmanagernew.ui.categories.IconMapper
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.UiUtils
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class QrBatchActivity : BaseActivity() {

    companion object {
        const val EXTRA_BOX_IDS = "boxIds"

        fun intent(
            context: Context,
            boxIds: IntArray
        ): Intent {
            return Intent(context, QrBatchActivity::class.java)
                .putExtra(EXTRA_BOX_IDS, boxIds)
        }
    }

    private var preparedLabels: List<QrBatchPdf.Label> = emptyList()
    private var excludedCount: Int = 0
    private var selectedSpec: LabelSheetSpec = LabelSheetSpec.ONE_PER_PAGE
    private var cachedPdf: ByteArray? = null

    private val createPdf =
        registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/pdf")
        ) { uri ->
            if (uri != null) {
                writePdf(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            !ArchivioCompletoNav.allowActivity(
                this,
                PremiumFeature.QR_LABEL
            )
        ) {
            return
        }

        setContentView(R.layout.activity_qr_batch)
        setupAppShell()
        setupPageHeader(
            title = getString(R.string.page_qr_batch_title),
            subtitle = getString(R.string.page_qr_batch_subtitle)
        )
        setupBottomNav()

        val boxIds = intent.getIntArrayExtra(EXTRA_BOX_IDS) ?: intArrayOf()
        if (boxIds.isEmpty()) {
            finish()
            return
        }

        val spinner = findViewById<Spinner>(R.id.spinnerSheetFormat)
        val specs = LabelSheetSpec.presets()
        val labels =
            specs.map { spec ->
                when (spec) {
                    LabelSheetSpec.ONE_PER_PAGE ->
                        getString(R.string.qr_batch_format_one)
                    LabelSheetSpec.A4_2X2 ->
                        getString(R.string.qr_batch_format_a4_2x2)
                    LabelSheetSpec.A4_2X3 ->
                        getString(R.string.qr_batch_format_a4_2x3)
                }
            }
        spinner.adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                labels
            )
        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSpec = specs[position]
                    cachedPdf = null
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }

        findViewById<Button>(R.id.btnBatchPrint).setOnClickListener {
            printBatch()
        }
        findViewById<Button>(R.id.btnBatchExport).setOnClickListener {
            createPdf.launch(QrBatchFileNames.proposed())
        }
        findViewById<Button>(R.id.btnBatchShare).setOnClickListener {
            shareBatch()
        }

        loadBoxes(boxIds)
    }

    private fun loadBoxes(boxIds: IntArray) {
        val db = DatabaseProvider.getDatabase(applicationContext)
        val repository = BoxRepositoryImpl(db.boxDao())

        lifecycleScope.launch {
            val (boxes, categories) =
                withContext(Dispatchers.IO) {
                    val loaded =
                        boxIds.toList().mapNotNull { id ->
                            repository.getBoxById(id)
                        }
                    val cats = db.categoryDao().getAllSync()
                    loaded to cats
                }

            val sorted = boxes.sortedBy { it.name.trim().lowercase() }
            val included = mutableListOf<Box>()
            var excluded = 0
            for (box in sorted) {
                if (box.permanentId.trim().isEmpty()) {
                    excluded++
                } else {
                    included.add(box)
                }
            }
            excludedCount = excluded

            findViewById<TextView>(R.id.textBatchSummary).text =
                getString(R.string.qr_batch_summary, included.size)

            val excludedView = findViewById<TextView>(R.id.textBatchExcluded)
            if (excluded > 0) {
                excludedView.visibility = View.VISIBLE
                excludedView.text =
                    getString(R.string.qr_batch_excluded, excluded)
            } else {
                excludedView.visibility = View.GONE
            }

            bindBoxCards(included, categories)

            if (included.isEmpty()) {
                return@launch
            }

            preparedLabels =
                withContext(Dispatchers.Default) {
                    included.map { box ->
                        val permanentId = box.permanentId.trim()
                        val payload = BoxQrPayload.encode(permanentId)
                        QrBatchPdf.Label(
                            permanentId = permanentId,
                            qrBitmap = QrLabelBitmap.render(payload)
                        )
                    }
                }

            findViewById<Button>(R.id.btnBatchPrint).isEnabled = true
            findViewById<Button>(R.id.btnBatchExport).isEnabled = true
            findViewById<Button>(R.id.btnBatchShare).isEnabled = true
        }
    }

    private fun bindBoxCards(
        boxes: List<Box>,
        categories: List<CategoryEntity>
    ) {
        val container = findViewById<LinearLayout>(R.id.boxesListContainer)
        container.removeAllViews()
        val inflater = LayoutInflater.from(this)

        for (box in boxes) {
            val card = inflater.inflate(R.layout.item_box, container, false)
            card.findViewById<TextView>(R.id.textBoxName).text = box.name

            val category = categories.find { it.id == box.categoryId }
            val categoryName =
                category?.name ?: getString(R.string.category_unknown)
            val subtitleParts =
                listOf(
                    categoryName,
                    box.position,
                    UiUtils.formatDate(box.lastModified)
                ).filter { it.isNotBlank() }
            card.findViewById<TextView>(R.id.textSubtitle).text =
                subtitleParts.joinToString(" • ")

            val imageCategory = card.findViewById<ImageView>(R.id.imageCategory)
            if (category != null) {
                imageCategory.setImageResource(
                    IconMapper.getIconRes(category.icon)
                )
            } else {
                imageCategory.setImageResource(R.drawable.outline_browse_24)
            }

            card.findViewById<TextView>(R.id.textMenu).visibility = View.GONE
            card.findViewById<View>(R.id.iconArea).isClickable = false
            card.findViewById<View>(R.id.contentArea).isClickable = false
            card.findViewById<View>(R.id.contentArea).foreground = null

            container.addView(card)
        }
    }

    private fun buildPdf(): ByteArray? {
        cachedPdf?.let { return it }
        if (preparedLabels.isEmpty()) {
            return null
        }
        val bytes = QrBatchPdf.toBytes(preparedLabels, selectedSpec)
        cachedPdf = bytes
        return bytes
    }

    private fun printBatch() {
        val pdfBytes = buildPdf() ?: return
        val printManager =
            getSystemService(Context.PRINT_SERVICE) as? PrintManager
                ?: return
        try {
            printManager.print(
                getString(R.string.qr_print_label),
                QrLabelPrintAdapter(
                    pdfBytes,
                    QrBatchFileNames.proposed(),
                    selectedSpec.pageCount(preparedLabels.size)
                ),
                PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                    .build()
            )
        } catch (_: Exception) {
            return
        }
    }

    private fun writePdf(uri: Uri) {
        val pdfBytes = buildPdf() ?: return
        try {
            contentResolver.openOutputStream(uri)?.use { output ->
                output.write(pdfBytes)
            }
        } catch (_: Exception) {
            return
        }
    }

    private fun shareBatch() {
        val pdfBytes = buildPdf() ?: return
        val fileName = QrBatchFileNames.proposed()
        try {
            val dir = File(cacheDir, "qr_share").apply { mkdirs() }
            val file = File(dir, fileName)
            file.writeBytes(pdfBytes)
            val uri =
                FileProvider.getUriForFile(
                    this,
                    "$packageName.fileprovider",
                    file
                )
            val send =
                Intent(Intent.ACTION_SEND)
                    .setType("application/pdf")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .putExtra(
                        Intent.EXTRA_SUBJECT,
                        getString(R.string.qr_batch_share_subject)
                    )
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(
                Intent.createChooser(
                    send,
                    getString(R.string.qr_batch_share)
                )
            )
        } catch (_: Exception) {
            return
        }
    }
}
