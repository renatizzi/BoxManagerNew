package com.example.boxmanagernew.domain.premium

enum class PremiumFeature {
    ADVANCED_SEARCH,
    QR_SCAN,
    QR_LABEL,
    IMPORT,
    EXPORT,
    FAMILY_SHARE
}

enum class ShareActionResult {
    PROGRESS,
    GRANTED,
    COOLDOWN
}

object ArchivioCompletoPolicy {

    const val DEFAULT_TRIAL_DAYS = 14
    const val DEFAULT_SHARE_BONUS_DAYS = 7
    const val DEFAULT_SHARE_FRIENDS = 1

    const val SHARE_COOLDOWN_MS =
        48L * 60L * 60L * 1000L
    const val DAY_MS =
        24L * 60L * 60L * 1000L

    const val UNLOCK_CODE =
        "BOXMANAGER-AMICO"

    const val UNLOCK_CODE_TESTER =
        "BOXMANAGER-TESTER"

    private val UNLOCK_CODES =
        setOf(UNLOCK_CODE, UNLOCK_CODE_TESTER)

    const val ADMIN_USERNAME =
        "Renato Stefanizzi"

    fun trialEnd(
        startedAt: Long,
        trialDays: Int
    ): Long {
        return startedAt + trialDays * DAY_MS
    }

    fun isOpen(
        now: Long,
        accessUntil: Long,
        codeUnlock: Boolean,
        debugUnlock: Boolean
    ): Boolean {
        if (codeUnlock || debugUnlock) {
            return true
        }
        return accessUntil > now
    }

    fun remainingDaysCeil(
        now: Long,
        accessUntil: Long
    ): Int {
        val left = accessUntil - now
        if (left <= 0) {
            return 0
        }
        val days =
            (left + DAY_MS - 1) / DAY_MS
        return days.toInt()
    }

    fun canGrantShare(
        now: Long,
        lastShareAt: Long
    ): Boolean {
        if (lastShareAt <= 0) {
            return true
        }
        return now - lastShareAt >= SHARE_COOLDOWN_MS
    }

    fun cooldownRemainingMs(
        now: Long,
        lastShareAt: Long
    ): Long {
        if (lastShareAt <= 0) {
            return 0
        }
        val wait =
            SHARE_COOLDOWN_MS - (now - lastShareAt)
        return if (wait < 0) 0 else wait
    }

    fun extendAccess(
        now: Long,
        accessUntil: Long,
        bonusDays: Int
    ): Long {
        val base =
            if (accessUntil > now) {
                accessUntil
            } else {
                now
            }
        return base + bonusDays * DAY_MS
    }

    fun clampTrialDays(value: Int): Int {
        return value.coerceIn(1, 365)
    }

    fun clampShareBonusDays(value: Int): Int {
        return value.coerceIn(1, 90)
    }

    fun clampShareFriends(value: Int): Int {
        return value.coerceIn(1, 20)
    }

    fun isAdminUsername(raw: String?): Boolean {
        val name =
            raw
                ?.trim()
                ?.replace(Regex("\\s+"), " ")
                ?: return false
        return name.equals(ADMIN_USERNAME, ignoreCase = false)
    }

    fun normalizeCode(raw: String): String {
        return raw
            .trim()
            .uppercase()
            .replace('_', '-')
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
    }

    fun isValidUnlockCode(raw: String): Boolean {
        return normalizeCode(raw) in UNLOCK_CODES
    }
}
