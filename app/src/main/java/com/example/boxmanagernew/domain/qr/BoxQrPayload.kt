package com.example.boxmanagernew.domain.qr

import org.json.JSONObject

/**
 * Payload QR V1: solo identificativo permanente.
 * Struttura versionata ed estendibile (campi extra ignorati).
 */
object BoxQrPayload {

    const val SOURCE = "boxmanager"
    const val VERSION = 1

    private const val KEY_SRC = "src"
    private const val KEY_VER = "ver"
    private const val KEY_ID = "id"

    sealed class Parse {
        data class Identified(val permanentId: String) : Parse()
        object Unreadable : Parse()
        object NotBoxManager : Parse()
    }

    fun encode(permanentId: String): String {
        val id = permanentId.trim()
        require(id.isNotEmpty())
        return JSONObject()
            .put(KEY_SRC, SOURCE)
            .put(KEY_VER, VERSION)
            .put(KEY_ID, id)
            .toString()
    }

    fun parse(raw: String?): Parse {
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) {
            return Parse.Unreadable
        }

        val obj = try {
            JSONObject(text)
        } catch (_: Exception) {
            return Parse.NotBoxManager
        }

        val source = obj.optString(KEY_SRC, "").trim()
        if (source != SOURCE) {
            return Parse.NotBoxManager
        }

        if (obj.has(KEY_VER) && !obj.isNull(KEY_VER)) {
            val version = try {
                obj.getInt(KEY_VER)
            } catch (_: Exception) {
                return Parse.NotBoxManager
            }
            if (version < VERSION) {
                return Parse.NotBoxManager
            }
        }

        val permanentId = obj.optString(KEY_ID, "").trim()
        if (permanentId.isEmpty()) {
            return Parse.NotBoxManager
        }

        return Parse.Identified(permanentId)
    }
}
