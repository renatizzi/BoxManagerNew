package com.example.boxmanagernew.backup.checksum

import com.example.boxmanagernew.backup.constants.BackupConstants
import java.io.InputStream
import java.security.MessageDigest

/**
 * Utility per il calcolo del checksum SHA-256.
 */
object BackupChecksum {

    /**
     * Calcola il checksum SHA-256 di uno stream.
     */
    fun calculate(
        inputStream: InputStream
    ): String {

        val digest =
            MessageDigest.getInstance("SHA-256")

        val buffer =
            ByteArray(
                BackupConstants.DEFAULT_BUFFER_SIZE
            )

        var read =
            inputStream.read(buffer)

        while (read != -1) {

            digest.update(
                buffer,
                0,
                read
            )

            read =
                inputStream.read(buffer)
        }

        return digest
            .digest()
            .joinToString("") {
                "%02x".format(it)
            }
    }
}
