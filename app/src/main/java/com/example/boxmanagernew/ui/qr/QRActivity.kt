package com.example.boxmanagernew.ui.qr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.domain.model.Box
import com.example.boxmanagernew.domain.qr.BoxQrPayload
import com.example.boxmanagernew.domain.qr.QrScanOutcome
import com.example.boxmanagernew.domain.qr.QrConfiguration
import com.example.boxmanagernew.domain.qr.QrScanResolver
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import com.example.boxmanagernew.ui.boxdetail.BoxDetailActivity
import com.example.boxmanagernew.ui.common.BaseActivity
import com.example.boxmanagernew.ui.common.FeedbackUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QRActivity : BaseActivity() {

    private var scanController: QrScanController? = null
    private lateinit var tvMessages: TextView
    private lateinit var boxRepository: BoxRepositoryImpl
    private var handlingScan = false

    private val requestCamera =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {
                startScan()
            } else {
                showCameraUnavailable()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            !ArchivioCompletoNav.allowActivity(
                this,
                PremiumFeature.QR_SCAN
            )
        ) {
            return
        }

        setContentView(R.layout.activity_qr)

        setupAppShell()

        setupPageHeader(
            title = "Codice QR",
            subtitle = ""
        )

        setupBottomNav()

        tvMessages = findViewById(R.id.tvMessages)

        val db = DatabaseProvider.getDatabase(applicationContext)
        boxRepository = BoxRepositoryImpl(db.boxDao())

        prepareCamera()
    }

    override fun onResume() {
        super.onResume()
        if (!handlingScan) {
            scanController?.resumeEmissions()
        }
    }

    override fun onDestroy() {
        scanController?.stop()
        scanController = null
        super.onDestroy()
    }

    private fun prepareCamera() {

        if (!packageManager.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY
            )
        ) {
            showCameraUnavailable()
            return
        }

        val granted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            startScan()
        } else {
            requestCamera.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startScan() {

        if (scanController != null) {
            return
        }

        val preview =
            findViewById<PreviewView>(R.id.previewQr)

        scanController =
            QrScanController(
                this,
                preview,
                onQrContent = { raw -> handleScan(raw) },
                onQrUnreadable = { handleScan(null) },
                onCameraUnavailable = { showCameraUnavailable() }
            ).also { controller ->
                controller.start()
            }
    }

    private fun handleScan(raw: String?) {

        handlingScan = true

        lifecycleScope.launch {

            val parsed = BoxQrPayload.parse(raw)

            val outcome =
                if (parsed is BoxQrPayload.Parse.Identified) {
                    val box =
                        withContext(Dispatchers.IO) {
                            boxRepository.getBoxByPermanentId(
                                parsed.permanentId
                            )
                        }
                    QrScanResolver.resolve(raw) { box }
                } else {
                    QrScanResolver.resolve(raw) { null }
                }

            when (outcome) {
                is QrScanOutcome.OpenContainer ->
                    openBoxDetail(outcome.box)

                QrScanOutcome.Unrecognized,
                QrScanOutcome.ContainerMissing,
                QrScanOutcome.ReadError -> {
                    showBlocking(QrScanResolver.message(outcome))
                    delay(1500)
                    handlingScan = false
                    scanController?.resumeEmissions()
                }
            }
        }
    }

    private fun openBoxDetail(box: Box) {

        handlingScan = false

        startActivity(
            Intent(
                this,
                BoxDetailActivity::class.java
            ).apply {
                putExtra("boxId", box.id)
                putExtra("boxName", box.name)
            }
        )
    }

    private fun showCameraUnavailable() {
        showBlocking(QrConfiguration.MSG_READ_ERROR)
    }

    private fun showBlocking(text: String?) {

        if (text.isNullOrEmpty()) {
            return
        }

        tvMessages.text = text
        tvMessages.visibility = View.VISIBLE
        FeedbackUtils.alert(this)
    }
}
