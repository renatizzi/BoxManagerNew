package com.example.boxmanagernew.ui.qr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.domain.qr.BoxQrPayload
import com.example.boxmanagernew.domain.qr.LabelSheetSpec
import com.example.boxmanagernew.domain.qr.QrBatchFileNames
import com.example.boxmanagernew.ui.common.BaseActivity
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
            subtitle = ""
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
        val repository =
            BoxRepositoryImpl(
                DatabaseProvider.getDatabase(applicationContext).boxDao()
            )

        lifecycleScope.launch {
            val boxes =
                withContext(Dispatchers.IO) {
                    boxIds.toList().mapNotNull { id ->
                        repository.getBoxById(id)
                    }
                }

            val sorted = boxes.sortedBy { it.name.trim().lowercase() }
            val included = mutableListOf<Pair<String, String>>()
            var excluded = 0
            for (box in sorted) {
                val permanentId = box.permanentId.trim()
                if (permanentId.isEmpty()) {
                    excluded++
                } else {
                    included.add(box.name to permanentId)
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

            findViewById<TextView>(R.id.textBatchNames).text =
                included.joinToString(separator = "\n") { it.first }

            if (included.isEmpty()) {
                return@launch
            }

            preparedLabels =
                withContext(Dispatchers.Default) {
                    included.map { (_, permanentId) ->
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
        val media =
            if (selectedSpec.isoA4) {
                PrintAttributes.MediaSize.ISO_A4
            } else {
                PrintAttributes.MediaSize.ISO_A6
            }
        try {
            printManager.print(
                getString(R.string.qr_print_label),
                QrLabelPrintAdapter(
                    pdfBytes,
                    QrBatchFileNames.proposed(),
                    selectedSpec.pageCount(preparedLabels.size)
                ),
                PrintAttributes.Builder()
                    .setMediaSize(media)
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
