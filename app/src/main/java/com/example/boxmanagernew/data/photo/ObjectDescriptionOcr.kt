package com.example.boxmanagernew.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * OCR on-device (R-OCR-5) — solo proposta Descrizione.
 * Rispetta [DESCRIPTION_MAX_CHARS] (allineato a `maxLength` sul campo Descrizione).
 */
object ObjectDescriptionOcr {

    /** Limite UI Descrizione (2 righe); OCR e digitazione non superano questo tetto. */
    const val DESCRIPTION_MAX_CHARS = 100

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    suspend fun readFromUri(context: Context, uri: Uri): String? {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            process(image)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun readFromFile(file: File): String? {
        val bytes = file.readBytes()
        val orientation =
            try {
                ExifInterface(file.absolutePath).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } catch (_: Exception) {
                ExifInterface.ORIENTATION_NORMAL
            }
        val bitmap =
            ObjectPhotoCompressor.decodeOrientedBitmap(bytes, orientation)
                ?: return null
        return try {
            readFromBitmap(bitmap)
        } finally {
            if (!bitmap.isRecycled) bitmap.recycle()
        }
    }

    suspend fun readFromBitmap(bitmap: Bitmap): String? {
        return try {
            process(InputImage.fromBitmap(bitmap, 0))
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun process(image: InputImage): String? =
        suspendCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { result ->
                    cont.resume(normalize(result.text))
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }

    fun normalize(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val cleaned =
            raw
                .lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString("\n")
                .trim()
                .ifBlank { return null }
        return truncateToMax(cleaned)
    }

    fun truncateToMax(text: String): String {
        if (text.length <= DESCRIPTION_MAX_CHARS) return text
        return text.take(DESCRIPTION_MAX_CHARS).trimEnd()
    }
}
