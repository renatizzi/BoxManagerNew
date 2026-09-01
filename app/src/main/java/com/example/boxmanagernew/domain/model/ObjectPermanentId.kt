package com.example.boxmanagernew.domain.model

import java.util.UUID

/**
 * Identificativo tecnico permanente dell'oggetto (Nota B0 / B2).
 * Non visibile in UI. Non è l'id Room.
 */
object ObjectPermanentId {

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
