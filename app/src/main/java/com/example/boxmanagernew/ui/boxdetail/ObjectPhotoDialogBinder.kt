package com.example.boxmanagernew.ui.boxdetail

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.boxmanagernew.R
import com.example.boxmanagernew.data.photo.ObjectDescriptionOcr
import com.example.boxmanagernew.data.photo.ObjectPhotoCompressor
import com.example.boxmanagernew.data.photo.ObjectPhotoStore
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.common.DialogUtils
import com.example.boxmanagernew.ui.premium.ArchivioCompletoNav
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Gestione galleria/scatto/rimozione foto + OCR Descrizione (A3 / R-OCR).
 * HD temporaneo: dopo Salva resta solo display/thumb; il file capture si elimina.
 */
class ObjectPhotoDialogBinder(
    private val activity: AppCompatActivity,
    private val photoStore: ObjectPhotoStore
) {

    enum class PendingAction {
        NONE,
        SET_URI,
        REMOVE
    }

    var pendingAction: PendingAction = PendingAction.NONE
        private set
    var pendingUri: Uri? = null
        private set
    var pendingCaptureFile: File? = null
        private set

    private var descriptionField: EditText? = null
    private var previewView: ImageView? = null
    private var removeBtn: View? = null
    private var captureFile: File? = null

    private lateinit var pickGallery: ActivityResultLauncher<String>
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private lateinit var requestCamera: ActivityResultLauncher<String>

    fun register() {
        pickGallery =
            activity.registerForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    pendingAction = PendingAction.SET_URI
                    pendingUri = uri
                    pendingCaptureFile = null
                    showPreview(uri)
                    runOcrFromUri(uri)
                }
            }
        takePicture =
            activity.registerForActivityResult(
                ActivityResultContracts.TakePicture()
            ) { ok ->
                val file = captureFile
                if (ok && file != null && file.isFile) {
                    pendingAction = PendingAction.SET_URI
                    pendingUri = null
                    pendingCaptureFile = file
                    showPreviewFile(file)
                    runOcrFromFile(file)
                }
            }
        requestCamera =
            activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    launchCameraInternal()
                }
            }
    }

    fun bind(
        views: DialogUtils.ObjectDialogViews,
        permanentId: String?
    ) {
        pendingAction = PendingAction.NONE
        pendingUri = null
        pendingCaptureFile = null
        descriptionField = views.description
        previewView = views.photoPreview
        removeBtn = views.btnPhotoRemove

        val preview = views.photoPreview
        val gallery = views.btnPhotoGallery
        val camera = views.btnPhotoCamera
        val remove = views.btnPhotoRemove
        if (preview == null || gallery == null || camera == null || remove == null) {
            return
        }

        if (permanentId != null && photoStore.thumbFile(permanentId).isFile) {
            photoStore.bindThumb(preview, permanentId)
            preview.visibility = View.VISIBLE
            remove.visibility = View.VISIBLE
        } else {
            preview.setImageDrawable(null)
            preview.visibility = View.GONE
            remove.visibility = View.GONE
        }

        gallery.setOnClickListener {
            ArchivioCompletoNav.run(activity, PremiumFeature.OBJECT_PHOTO) {
                pickGallery.launch("image/*")
            }
        }
        camera.setOnClickListener {
            ArchivioCompletoNav.run(activity, PremiumFeature.OBJECT_PHOTO) {
                ensureCameraThenCapture()
            }
        }
        remove.setOnClickListener {
            ArchivioCompletoNav.run(activity, PremiumFeature.OBJECT_PHOTO) {
                pendingAction = PendingAction.REMOVE
                pendingUri = null
                pendingCaptureFile = null
                preview.setImageDrawable(null)
                preview.visibility = View.GONE
                remove.visibility = View.GONE
            }
        }
    }

    fun showFullscreen(permanentId: String) {
        ArchivioCompletoNav.run(activity, PremiumFeature.OBJECT_PHOTO) {
            val file = photoStore.displayFile(permanentId)
            if (!file.isFile) return@run
            val image = ImageView(activity).apply {
                adjustViewBounds = true
                photoStore.bindDisplay(this, permanentId)
            }
            AlertDialog.Builder(activity)
                .setTitle(R.string.object_photo_preview_title)
                .setView(image)
                .setPositiveButton(R.string.common_ok, null)
                .show()
        }
    }

    private fun runOcrFromUri(uri: Uri) {
        activity.lifecycleScope.launch {
            val text = withContext(Dispatchers.IO) {
                ObjectDescriptionOcr.readFromUri(activity, uri)
            }
            proposeDescription(text)
        }
    }

    private fun runOcrFromFile(file: File) {
        activity.lifecycleScope.launch {
            val text = withContext(Dispatchers.IO) {
                ObjectDescriptionOcr.readFromFile(file)
            }
            proposeDescription(text)
        }
    }

    private fun proposeDescription(text: String?) {
        if (text.isNullOrBlank()) return
        val field = descriptionField ?: return
        val clipped = ObjectDescriptionOcr.truncateToMax(text)
        val current = field.text?.toString().orEmpty().trim()
        if (current.isEmpty()) {
            field.setText(clipped)
            return
        }
        AlertDialog.Builder(activity)
            .setMessage(R.string.object_photo_ocr_replace_description)
            .setPositiveButton(R.string.common_yes) { _, _ ->
                field.setText(clipped)
            }
            .setNegativeButton(R.string.common_no, null)
            .show()
    }

    private fun showPreview(uri: Uri) {
        val preview = previewView ?: return
        try {
            activity.contentResolver.openInputStream(uri)?.use { input ->
                val bytes = input.readBytes()
                val bmp =
                    ObjectPhotoCompressor.decodeOrientedBitmap(
                        bytes,
                        orientationFromBytes(bytes)
                    ) ?: return
                preview.setImageBitmap(bmp)
                preview.visibility = View.VISIBLE
                removeBtn?.visibility = View.VISIBLE
            }
        } catch (_: Exception) {
            // ignore preview failures
        }
    }

    private fun showPreviewFile(file: File) {
        val preview = previewView ?: return
        val bytes = file.readBytes()
        val bmp =
            ObjectPhotoCompressor.decodeOrientedBitmap(
                bytes,
                orientationFromFile(file)
            ) ?: return
        preview.setImageBitmap(bmp)
        preview.visibility = View.VISIBLE
        removeBtn?.visibility = View.VISIBLE
    }

    private fun orientationFromBytes(bytes: ByteArray): Int =
        try {
            android.media.ExifInterface(java.io.ByteArrayInputStream(bytes))
                .getAttributeInt(
                    android.media.ExifInterface.TAG_ORIENTATION,
                    android.media.ExifInterface.ORIENTATION_NORMAL
                )
        } catch (_: Exception) {
            android.media.ExifInterface.ORIENTATION_NORMAL
        }

    private fun orientationFromFile(file: File): Int =
        try {
            android.media.ExifInterface(file.absolutePath)
                .getAttributeInt(
                    android.media.ExifInterface.TAG_ORIENTATION,
                    android.media.ExifInterface.ORIENTATION_NORMAL
                )
        } catch (_: Exception) {
            android.media.ExifInterface.ORIENTATION_NORMAL
        }

    private fun ensureCameraThenCapture() {
        val granted =
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            launchCameraInternal()
        } else {
            AlertDialog.Builder(activity)
                .setMessage(R.string.object_photo_camera_rationale)
                .setPositiveButton(R.string.privacy_camera_continue) { _, _ ->
                    requestCamera.launch(Manifest.permission.CAMERA)
                }
                .setNegativeButton(R.string.common_cancel, null)
                .show()
        }
    }

    private fun launchCameraInternal() {
        val dir = File(activity.cacheDir, "object_photo_capture").also { it.mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        captureFile = file
        val uri =
            FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                file
            )
        takePicture.launch(uri)
    }
}
