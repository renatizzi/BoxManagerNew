package com.example.boxmanagernew.domain.trash

/** R5 REQUISITI_CESTINO — retention fissa 30 giorni. */
object TrashRetention {
    const val RETENTION_DAYS = 30
    const val RETENTION_MS = RETENTION_DAYS * 24L * 60L * 60L * 1000L

    fun isExpired(deletedAt: Long, now: Long = System.currentTimeMillis()): Boolean {
        return now - deletedAt >= RETENTION_MS
    }
}
