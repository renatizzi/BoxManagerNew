package com.example.boxmanagernew.domain.premium

import android.content.Context

class ArchivioCompletoAccess(
    context: Context
) {

    private val prefs =
        context.applicationContext
            .getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
            )

    fun isOpen(): Boolean {
        return ArchivioCompletoPolicy.isOpen(
            purchased = prefs.getBoolean(KEY_PURCHASED, false),
            debugUnlock = prefs.getBoolean(KEY_DEBUG_UNLOCK, false)
        )
    }

    fun isDebugUnlock(): Boolean {
        return prefs.getBoolean(KEY_DEBUG_UNLOCK, false)
    }

    fun setDebugUnlock(value: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DEBUG_UNLOCK, value)
            .apply()
    }

    fun used(feature: PremiumFeature): Int {
        return prefs.getInt(usedKey(feature), 0)
    }

    fun remaining(feature: PremiumFeature): Int {
        return ArchivioCompletoPolicy.remaining(
            feature.trialLimit,
            used(feature)
        )
    }

    fun canTrial(feature: PremiumFeature): Boolean {
        if (isOpen()) {
            return false
        }
        return ArchivioCompletoPolicy.canTrial(
            feature.trialLimit,
            used(feature)
        )
    }

    fun previewSeen(feature: PremiumFeature): Boolean {
        return prefs.getBoolean(seenKey(feature), false)
    }

    fun markPreviewSeen(feature: PremiumFeature) {
        prefs.edit()
            .putBoolean(seenKey(feature), true)
            .apply()
    }

    fun consumeTrial(feature: PremiumFeature) {
        if (isOpen()) {
            return
        }
        if (!canTrial(feature)) {
            return
        }
        prefs.edit()
            .putInt(usedKey(feature), used(feature) + 1)
            .apply()
    }

    fun resetTrials() {
        val editor = prefs.edit()
        PremiumFeature.entries.forEach { feature ->
            editor.putInt(usedKey(feature), 0)
            editor.putBoolean(seenKey(feature), false)
        }
        editor.apply()
    }

    private fun usedKey(feature: PremiumFeature): String {
        return "used_" + feature.name
    }

    private fun seenKey(feature: PremiumFeature): String {
        return "seen_" + feature.name
    }

    companion object {
        const val PREFS = "archivio_completo"
        const val KEY_PURCHASED = "purchased"
        const val KEY_DEBUG_UNLOCK = "debug_unlock"
    }
}
