package com.example.boxmanagernew.ui.qr

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.domain.qr.BoxQrPayload
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QrLabelActivity : BaseActivity() {

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

        setContentView(R.layout.activity_qr_label)

        setupAppShell()

        setupPageHeader(
            title = getString(R.string.page_qr_label_title),
            subtitle = ""
        )

        setupBottomNav()

        val boxId = intent.getIntExtra("boxId", -1)
        if (boxId < 0) {
            finish()
            return
        }

        val imageQr = findViewById<ImageView>(R.id.imageQr)
        val textIdentifier = findViewById<TextView>(R.id.textIdentifier)
        val btnPrint = findViewById<Button>(R.id.btnPrintLabel)
        val btnExport = findViewById<Button>(R.id.btnExportPdf)
        val labelView = findViewById<View>(R.id.labelRoot)

        btnPrint.setOnClickListener {
            printLabel(labelView)
        }

        btnExport.setOnClickListener {
            createPdf.launch(getString(R.string.qr_label_pdf_name))
        }

        val repository = BoxRepositoryImpl(
            DatabaseProvider.getDatabase(applicationContext).boxDao()
        )

        lifecycleScope.launch {

            val box = withContext(Dispatchers.IO) {
                repository.getBoxById(boxId)
            }

            val permanentId = box?.permanentId?.trim().orEmpty()
            if (permanentId.isEmpty()) {
                finish()
                return@launch
            }

            val payload = BoxQrPayload.encode(permanentId)
            val bitmap = withContext(Dispatchers.Default) {
                QrLabelBitmap.render(payload)
            }

            imageQr.setImageBitmap(bitmap)
            textIdentifier.text = permanentId

            labelView.post {
                btnPrint.isEnabled = true
                btnExport.isEnabled = true
            }
        }
    }

    private fun printLabel(labelView: View) {

        val pdfBytes = QrLabelPdf.toBytes(labelView)
        val printManager =
            getSystemService(Context.PRINT_SERVICE) as? PrintManager
                ?: return

        try {
            printManager.print(
                getString(R.string.qr_print_label),
                QrLabelPrintAdapter(
                    pdfBytes,
                    getString(R.string.qr_label_pdf_name)
                ),
                PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A6)
                    .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                    .build()
            )
        } catch (_: Exception) {
            return
        }
    }

    private fun writePdf(uri: Uri) {

        val labelView = findViewById<View>(R.id.labelRoot)
        val pdfBytes = QrLabelPdf.toBytes(labelView)

        try {
            contentResolver.openOutputStream(uri)?.use { output ->
                output.write(pdfBytes)
            }
        } catch (_: Exception) {
            return
        }
    }
}
