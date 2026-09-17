package com.example.boxmanagernew.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.math.max

/**
 * Compressione foto oggetto (T2): display 800/JPEG75, thumb 160/JPEG70.
 */
object ObjectPhotoCompressor {

    const val DISPLAY_MAX_SIDE = 800
    const val DISPLAY_JPEG_QUALITY = 75
    const val THUMB_MAX_SIDE = 160
    const val THUMB_JPEG_QUALITY = 70

    data class CompressedPair(
        val displayJpeg: ByteArray,
        val thumbJpeg: ByteArray
    )

    fun compressFromUri(context: Context, uri: Uri): CompressedPair {
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "photo_unreadable" }
            return compressFromStream(input)
        }
    }

    fun compressFromFile(file: File): CompressedPair {
        file.inputStream().use { return compressFromStream(it) }
    }

    fun compressFromStream(input: InputStream): CompressedPair {
        val bytes = input.readBytes()
        val original = decodeBoundsAware(bytes)
            ?: error("photo_decode_failed")
        val display = scaleToMaxSide(original, DISPLAY_MAX_SIDE)
        val thumb = scaleToMaxSide(original, THUMB_MAX_SIDE)
        try {
            return CompressedPair(
                displayJpeg = toJpeg(display, DISPLAY_JPEG_QUALITY),
                thumbJpeg = toJpeg(thumb, THUMB_JPEG_QUALITY)
            )
        } finally {
            if (display !== original && !display.isRecycled) display.recycle()
            if (thumb !== original && !thumb.isRecycled) thumb.recycle()
            if (!original.isRecycled) original.recycle()
        }
    }

    private fun decodeBoundsAware(bytes: ByteArray): Bitmap? {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val maxSide = max(bounds.outWidth, bounds.outHeight).coerceAtLeast(1)
        var sample = 1
        while (maxSide / (sample * 2) >= DISPLAY_MAX_SIDE * 2) {
            sample *= 2
        }
        val opts = BitmapFactory.Options().apply {
            inSampleSize = sample
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
    }

    private fun scaleToMaxSide(source: Bitmap, maxSide: Int): Bitmap {
        val w = source.width
        val h = source.height
        val longest = max(w, h)
        if (longest <= maxSide) {
            return source
        }
        val scale = maxSide.toFloat() / longest.toFloat()
        val nw = (w * scale).toInt().coerceAtLeast(1)
        val nh = (h * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(source, nw, nh, true)
    }

    private fun toJpeg(bitmap: Bitmap, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        return out.toByteArray()
    }
}
