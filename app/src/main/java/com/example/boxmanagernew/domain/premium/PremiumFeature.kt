package com.example.boxmanagernew.domain.premium

enum class PremiumFeature(
    val trialLimit: Int
) {
    ADVANCED_SEARCH(3),
    QR_SCAN(1),
    QR_LABEL(1),
    IMPORT(0),
    EXPORT(1)
}

object ArchivioCompletoPolicy {

    fun remaining(
        limit: Int,
        used: Int
    ): Int {
        val safeUsed =
            if (used < 0) 0 else used
        val left = limit - safeUsed
        return if (left < 0) 0 else left
    }

    fun canTrial(
        limit: Int,
        used: Int
    ): Boolean {
        return remaining(limit, used) > 0
    }

    fun isOpen(
        purchased: Boolean,
        debugUnlock: Boolean
    ): Boolean {
        return purchased || debugUnlock
    }
}
