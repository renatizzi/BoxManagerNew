package com.example.boxmanagernew.data.photo

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import com.example.boxmanagernew.data.local.dao.ObjectPhotoDao
import com.example.boxmanagernew.data.local.entity.ObjectPhotoEntity
import java.io.File

/**
 * Storage file + metadati foto oggetto (A3 T1).
 */
class ObjectPhotoStore(
    private val appContext: Context,
    private val photoDao: ObjectPhotoDao
) {

    private val photosDir: File =
        File(appContext.filesDir, ObjectPhotoPaths.DIR_NAME).also { it.mkdirs() }

    fun displayFile(permanentId: String): File =
        File(photosDir, ObjectPhotoPaths.displayFileName(permanentId))

    fun thumbFile(permanentId: String): File =
        File(photosDir, ObjectPhotoPaths.thumbFileName(permanentId))

    suspend fun hasPhoto(permanentId: String): Boolean =
        photoDao.getByPermanentId(permanentId) != null &&
            displayFile(permanentId).isFile &&
            thumbFile(permanentId).isFile

    suspend fun getMeta(permanentId: String): ObjectPhotoEntity? =
        photoDao.getByPermanentId(permanentId)

    suspend fun getAllMeta(): List<ObjectPhotoEntity> =
        photoDao.getAll()

    suspend fun saveFromUri(permanentId: String, uri: Uri): ObjectPhotoEntity {
        val pair = ObjectPhotoCompressor.compressFromUri(appContext, uri)
        return writePair(permanentId, pair)
    }

    suspend fun saveFromFile(permanentId: String, file: File): ObjectPhotoEntity {
        val pair = ObjectPhotoCompressor.compressFromFile(file)
        return writePair(permanentId, pair)
    }

    suspend fun saveFromBytes(
        permanentId: String,
        displayJpeg: ByteArray,
        thumbJpeg: ByteArray
    ): ObjectPhotoEntity {
        return writePair(
            permanentId,
            ObjectPhotoCompressor.CompressedPair(displayJpeg, thumbJpeg)
        )
    }

    private suspend fun writePair(
        permanentId: String,
        pair: ObjectPhotoCompressor.CompressedPair
    ): ObjectPhotoEntity {
        require(permanentId.isNotBlank()) { "permanentId_blank" }
        val display = displayFile(permanentId)
        val thumb = thumbFile(permanentId)
        display.writeBytes(pair.displayJpeg)
        thumb.writeBytes(pair.thumbJpeg)
        val meta = ObjectPhotoEntity(
            objectPermanentId = permanentId,
            displayFileName = ObjectPhotoPaths.displayFileName(permanentId),
            thumbFileName = ObjectPhotoPaths.thumbFileName(permanentId),
            updatedAt = System.currentTimeMillis(),
            byteSize = pair.displayJpeg.size.toLong() + pair.thumbJpeg.size.toLong()
        )
        photoDao.upsert(meta)
        return meta
    }

    suspend fun deletePhoto(permanentId: String) {
        displayFile(permanentId).delete()
        thumbFile(permanentId).delete()
        photoDao.deleteByPermanentId(permanentId)
    }

    suspend fun deletePhotos(permanentIds: List<String>) {
        for (id in permanentIds) {
            displayFile(id).delete()
            thumbFile(id).delete()
        }
        if (permanentIds.isNotEmpty()) {
            photoDao.deleteByPermanentIds(permanentIds)
        }
    }

    suspend fun clearAllFilesAndMeta() {
        photosDir.listFiles()?.forEach { it.delete() }
        photoDao.deleteAll()
    }

    fun bindThumb(imageView: ImageView, permanentId: String): Boolean {
        val file = thumbFile(permanentId)
        if (!file.isFile) {
            return false
        }
        val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: return false
        imageView.setImageBitmap(bmp)
        return true
    }

    fun bindDisplay(imageView: ImageView, permanentId: String): Boolean {
        val file = displayFile(permanentId)
        if (!file.isFile) {
            return false
        }
        val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: return false
        imageView.setImageBitmap(bmp)
        return true
    }

    /** Entries for Backup / Family ZIP (path → bytes). */
    suspend fun zipEntriesForBackup(): Map<String, ByteArray> {
        val out = linkedMapOf<String, ByteArray>()
        for (meta in photoDao.getAll()) {
            val id = meta.objectPermanentId
            val display = displayFile(id)
            val thumb = thumbFile(id)
            if (display.isFile) {
                out[ObjectPhotoPaths.zipDisplayEntry(id)] = display.readBytes()
            }
            if (thumb.isFile) {
                out[ObjectPhotoPaths.zipThumbEntry(id)] = thumb.readBytes()
            }
        }
        return out
    }

    /**
     * After DB restore: write photo files from ZIP entries and upsert meta.
     * Orphan photos (no matching live object) are skipped by caller via [keepIds].
     */
    suspend fun restoreFromZipEntries(
        entries: Map<String, ByteArray>,
        keepIds: Set<String>
    ) {
        clearAllFilesAndMeta()
        applyZipEntries(entries, keepIds)
    }

    /**
     * T4/T5 merge: upsert photo files from ZIP without wiping local photos
     * for objects not present in the package.
     */
    suspend fun mergeFromZipEntries(
        entries: Map<String, ByteArray>,
        keepIds: Set<String>
    ) {
        applyZipEntries(entries, keepIds)
    }

    private suspend fun applyZipEntries(
        entries: Map<String, ByteArray>,
        keepIds: Set<String>
    ) {
        val prefix = ObjectPhotoPaths.ZIP_DIR
        val byId = mutableMapOf<String, MutableMap<String, ByteArray>>()
        for ((path, bytes) in entries) {
            if (!path.startsWith(prefix)) continue
            val name = path.removePrefix(prefix)
            val permanentId = when {
                name.endsWith("_thumb.jpg") -> name.removeSuffix("_thumb.jpg")
                name.endsWith(".jpg") -> name.removeSuffix(".jpg")
                else -> continue
            }
            if (permanentId !in keepIds) continue
            val slot = byId.getOrPut(permanentId) { mutableMapOf() }
            if (name.endsWith("_thumb.jpg")) {
                slot["thumb"] = bytes
            } else {
                slot["display"] = bytes
            }
        }
        for ((id, parts) in byId) {
            val display = parts["display"] ?: continue
            val thumb = parts["thumb"] ?: continue
            saveFromBytes(id, display, thumb)
        }
    }

    suspend fun totalByteSize(): Long =
        photoDao.getAll().sumOf { it.byteSize }

    suspend fun photoCount(): Int =
        photoDao.getAll().size
}
