package com.example.boxmanagernew.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.math.max

/**
 * Compressione foto oggetto (T2): display 800/JPEG75, thumb 160/JPEG70.
 * Applica EXIF orientation (scatto fotocamera) prima di scalare.
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
            val bytes = input.readBytes()
            val orientation = readOrientation(ByteArrayInputStream(bytes))
            return compressBytes(bytes, orientation)
        }
    }

    fun compressFromFile(file: File): CompressedPair {
        val bytes = file.readBytes()
        val orientation = readOrientation(file)
        return compressBytes(bytes, orientation)
    }

    fun compressFromStream(input: InputStream): CompressedPair {
        val bytes = input.readBytes()
        val orientation = readOrientation(ByteArrayInputStream(bytes))
        return compressBytes(bytes, orientation)
    }

    /** Gradi di rotazione da applicare al bitmap per allinearlo a ORIENTATION_NORMAL. */
    fun rotationDegreesForExif(orientation: Int): Float =
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

    fun decodeOrientedBitmap(bytes: ByteArray, orientation: Int): Bitmap? {
        val original = decodeBoundsAware(bytes) ?: return null
        return applyOrientation(original, orientation)
    }

    private fun compressBytes(bytes: ByteArray, orientation: Int): CompressedPair {
        val oriented = decodeOrientedBitmap(bytes, orientation)
            ?: error("photo_decode_failed")
        val display = scaleToMaxSide(oriented, DISPLAY_MAX_SIDE)
        val thumb = scaleToMaxSide(oriented, THUMB_MAX_SIDE)
        try {
            return CompressedPair(
                displayJpeg = toJpeg(display, DISPLAY_JPEG_QUALITY),
                thumbJpeg = toJpeg(thumb, THUMB_JPEG_QUALITY)
            )
        } finally {
            if (display !== oriented && !display.isRecycled) display.recycle()
            if (thumb !== oriented && !thumb.isRecycled) thumb.recycle()
            if (!oriented.isRecycled) oriented.recycle()
        }
    }

    private fun readOrientation(file: File): Int =
        try {
            ExifInterface(file.absolutePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

    private fun readOrientation(input: InputStream): Int =
        try {
            ExifInterface(input).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

    private fun applyOrientation(source: Bitmap, orientation: Int): Bitmap {
        val degrees = rotationDegreesForExif(orientation)
        val flipHorizontal =
            orientation == ExifInterface.ORIENTATION_FLIP_HORIZONTAL ||
                orientation == ExifInterface.ORIENTATION_TRANSPOSE ||
                orientation == ExifInterface.ORIENTATION_TRANSVERSE
        val flipVertical =
            orientation == ExifInterface.ORIENTATION_FLIP_VERTICAL
        if (degrees == 0f && !flipHorizontal && !flipVertical) {
            return source
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
                matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL ->
                matrix.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.preRotate(90f)
                matrix.preScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.preRotate(270f)
                matrix.preScale(-1f, 1f)
            }
            else ->
                if (degrees != 0f) matrix.postRotate(degrees)
        }
        val out =
            Bitmap.createBitmap(
                source,
                0,
                0,
                source.width,
                source.height,
                matrix,
                true
            )
        if (out !== source && !source.isRecycled) {
            source.recycle()
        }
        return out
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
