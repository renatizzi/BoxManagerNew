package com.example.boxmanagernew.ui.qr

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class QrScanController(
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView,
    private val onQrContent: (String) -> Unit,
    private val onQrUnreadable: () -> Unit
) {

    private val analysisExecutor: ExecutorService =
        Executors.newSingleThreadExecutor()

    private val barcodeScanner: BarcodeScanner =
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )

    private val stopped = AtomicBoolean(false)
    private val emitting = AtomicBoolean(true)

    private var cameraProvider: ProcessCameraProvider? = null

    fun start() {

        if (stopped.get()) {
            return
        }

        val context = previewView.context
        val future = ProcessCameraProvider.getInstance(context)

        future.addListener(
            {
                if (stopped.get()) {
                    return@addListener
                }

                if (lifecycleOwner.lifecycle.currentState ==
                    Lifecycle.State.DESTROYED
                ) {
                    return@addListener
                }

                val provider = future.get()
                cameraProvider = provider
                bind(provider)
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun stop() {

        if (!stopped.compareAndSet(false, true)) {
            return
        }

        cameraProvider?.unbindAll()
        cameraProvider = null
        barcodeScanner.close()
        analysisExecutor.shutdown()
    }

    fun pauseEmissions() {
        emitting.set(false)
    }

    fun resumeEmissions() {
        emitting.set(true)
    }

    private fun bind(provider: ProcessCameraProvider) {

        val preview = Preview.Builder().build().also { useCase ->
            useCase.setSurfaceProvider(previewView.surfaceProvider)
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val mainExecutor =
            ContextCompat.getMainExecutor(previewView.context)

        analysis.setAnalyzer(
            analysisExecutor,
            MlKitAnalyzer(
                listOf(barcodeScanner),
                ImageAnalysis.COORDINATE_SYSTEM_ORIGINAL,
                analysisExecutor
            ) { result ->

                if (stopped.get()) {
                    return@MlKitAnalyzer
                }

                val barcodes =
                    result.getValue(barcodeScanner)

                if (barcodes.isNullOrEmpty()) {
                    return@MlKitAnalyzer
                }

                if (!emitting.compareAndSet(true, false)) {
                    return@MlKitAnalyzer
                }

                val raw = barcodes.first().rawValue

                mainExecutor.execute {
                    if (stopped.get()) {
                        return@execute
                    }
                    if (raw.isNullOrBlank()) {
                        onQrUnreadable()
                    } else {
                        onQrContent(raw)
                    }
                }
            }
        )

        provider.unbindAll()

        val selector = firstAvailableSelector(provider) ?: return

        provider.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            analysis
        )
    }

    private fun firstAvailableSelector(
        provider: ProcessCameraProvider
    ): CameraSelector? {

        val back = CameraSelector.DEFAULT_BACK_CAMERA
        if (hasCamera(provider, back)) {
            return back
        }

        val front = CameraSelector.DEFAULT_FRONT_CAMERA
        if (hasCamera(provider, front)) {
            return front
        }

        return null
    }

    private fun hasCamera(
        provider: ProcessCameraProvider,
        selector: CameraSelector
    ): Boolean {

        return try {
            provider.hasCamera(selector)
        } catch (_: Exception) {
            false
        }
    }
}
