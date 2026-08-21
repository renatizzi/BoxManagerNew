package com.example.boxmanagernew.domain.model

import java.util.UUID

/**
 * Identificativo tecnico permanente del contenitore (Nota 3.4.4).
 * Non visibile in UI. Non è l'id Room.
 */
object BoxPermanentId {

    fun generate(): String =
        UUID.randomUUID().toString()

    fun fromStored(value: String?): String {
        val trimmed = value?.trim().orEmpty()
        return if (trimmed.isEmpty()) {
            generate()
        } else {
            trimmed
        }
    }
}
