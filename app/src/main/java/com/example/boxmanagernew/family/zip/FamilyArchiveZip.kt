package com.example.boxmanagernew.family.zip

import com.example.boxmanagernew.data.photo.ObjectPhotoPaths
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Pacchetto Invia/Ricevi Archivio (T4): CSV FamilyMerge + photos/objects/.
 */
object FamilyArchiveZip {

    const val CSV_ENTRY = "archivio.csv"

    data class Unpacked(
        val csvBytes: ByteArray,
        val photoEntries: Map<String, ByteArray>
    )

    fun pack(
        csvBytes: ByteArray,
        photoEntries: Map<String, ByteArray>
    ): ByteArray {
        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            zip.putNextEntry(ZipEntry(CSV_ENTRY))
            zip.write(csvBytes)
            zip.closeEntry()
            for ((path, bytes) in photoEntries) {
                zip.putNextEntry(ZipEntry(path))
                zip.write(bytes)
                zip.closeEntry()
            }
        }
        return out.toByteArray()
    }

    fun unpack(zipBytes: ByteArray): Unpacked? {
        return try {
            var csv: ByteArray? = null
            val photos = linkedMapOf<String, ByteArray>()
            ZipInputStream(ByteArrayInputStream(zipBytes)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val name = entry.name.removePrefix("./")
                        val bytes = zip.readBytes()
                        when {
                            name == CSV_ENTRY ||
                                name.endsWith(".csv", ignoreCase = true) &&
                                !name.contains('/') -> {
                                csv = bytes
                            }
                            name.startsWith(ObjectPhotoPaths.ZIP_DIR) -> {
                                photos[name] = bytes
                            }
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
            val csvBytes = csv ?: return null
            Unpacked(csvBytes, photos)
        } catch (_: Exception) {
            null
        }
    }

    fun looksLikeZip(bytes: ByteArray): Boolean {
        return bytes.size >= 4 &&
            bytes[0] == 0x50.toByte() &&
            bytes[1] == 0x4B.toByte()
    }
}
