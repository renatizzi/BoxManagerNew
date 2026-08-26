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

    fun now(): Long {
        return System.currentTimeMillis()
    }

    fun trialDays(): Int {
        return ArchivioCompletoPolicy.clampTrialDays(
            prefs.getInt(
                KEY_TRIAL_DAYS,
                ArchivioCompletoPolicy.DEFAULT_TRIAL_DAYS
            )
        )
    }

    fun shareBonusDays(): Int {
        return ArchivioCompletoPolicy.clampShareBonusDays(
            prefs.getInt(
                KEY_SHARE_BONUS_DAYS,
                ArchivioCompletoPolicy.DEFAULT_SHARE_BONUS_DAYS
            )
        )
    }

    fun shareFriendsRequired(): Int {
        return ArchivioCompletoPolicy.clampShareFriends(
            prefs.getInt(
                KEY_SHARE_FRIENDS,
                ArchivioCompletoPolicy.DEFAULT_SHARE_FRIENDS
            )
        )
    }

    fun shareProgress(): Int {
        return prefs.getInt(KEY_SHARE_PROGRESS, 0)
            .coerceAtLeast(0)
    }

    fun saveParams(
        trialDays: Int,
        shareBonusDays: Int,
        shareFriends: Int
    ) {
        prefs.edit()
            .putInt(
                KEY_TRIAL_DAYS,
                ArchivioCompletoPolicy.clampTrialDays(trialDays)
            )
            .putInt(
                KEY_SHARE_BONUS_DAYS,
                ArchivioCompletoPolicy.clampShareBonusDays(shareBonusDays)
            )
            .putInt(
                KEY_SHARE_FRIENDS,
                ArchivioCompletoPolicy.clampShareFriends(shareFriends)
            )
            .apply()
    }

    fun isOpen(): Boolean {
        ensureTrialStarted()
        return ArchivioCompletoPolicy.isOpen(
            now = now(),
            accessUntil = accessUntil(),
            codeUnlock = isCodeUnlock(),
            debugUnlock = isDebugUnlock()
        )
    }

    fun isPermanentUnlock(): Boolean {
        return isCodeUnlock() || isDebugUnlock()
    }

    fun isDebugUnlock(): Boolean {
        return prefs.getBoolean(KEY_DEBUG_UNLOCK, false)
    }

    fun isCodeUnlock(): Boolean {
        return prefs.getBoolean(KEY_CODE_UNLOCK, false)
    }

    fun setDebugUnlock(value: Boolean) {
        prefs.edit()
            .putBoolean(KEY_DEBUG_UNLOCK, value)
            .apply()
    }

    fun accessUntil(): Long {
        return prefs.getLong(KEY_ACCESS_UNTIL, 0L)
    }

    fun remainingDays(): Int {
        if (isPermanentUnlock()) {
            return Int.MAX_VALUE
        }
        ensureTrialStarted()
        return ArchivioCompletoPolicy.remainingDaysCeil(
            now(),
            accessUntil()
        )
    }

    fun canGrantShare(): Boolean {
        return ArchivioCompletoPolicy.canGrantShare(
            now(),
            lastShareAt()
        )
    }

    fun cooldownRemainingMs(): Long {
        return ArchivioCompletoPolicy.cooldownRemainingMs(
            now(),
            lastShareAt()
        )
    }

    fun registerShareAction(): ShareActionResult {
        val required = shareFriendsRequired()
        val next = shareProgress() + 1

        if (next < required) {
            prefs.edit()
                .putInt(KEY_SHARE_PROGRESS, next)
                .apply()
            return ShareActionResult.PROGRESS
        }

        if (!canGrantShare()) {
            return ShareActionResult.COOLDOWN
        }

        val until =
            ArchivioCompletoPolicy.extendAccess(
                now(),
                accessUntil(),
                shareBonusDays()
            )

        prefs.edit()
            .putLong(KEY_ACCESS_UNTIL, until)
            .putLong(KEY_LAST_SHARE_AT, now())
            .putBoolean(KEY_TRIAL_STARTED, true)
            .putInt(KEY_SHARE_PROGRESS, 0)
            .apply()

        return ShareActionResult.GRANTED
    }

    fun redeemCode(raw: String): Boolean {
        if (!ArchivioCompletoPolicy.isValidUnlockCode(raw)) {
            return false
        }
        prefs.edit()
            .putBoolean(KEY_CODE_UNLOCK, true)
            .apply()
        return true
    }

    fun ensureTrialStarted() {
        if (isCodeUnlock() || isDebugUnlock()) {
            return
        }
        if (prefs.getBoolean(KEY_TRIAL_STARTED, false)) {
            return
        }
        val start = now()
        prefs.edit()
            .putBoolean(KEY_TRIAL_STARTED, true)
            .putLong(
                KEY_ACCESS_UNTIL,
                ArchivioCompletoPolicy.trialEnd(
                    start,
                    trialDays()
                )
            )
            .apply()
    }

    fun expireTrialForDebug() {
        prefs.edit()
            .putBoolean(KEY_TRIAL_STARTED, true)
            .putLong(KEY_ACCESS_UNTIL, now() - 1L)
            .putLong(KEY_LAST_SHARE_AT, 0L)
            .putInt(KEY_SHARE_PROGRESS, 0)
            .putBoolean(KEY_DEBUG_UNLOCK, false)
            .apply()
    }

    fun restartTrialForDebug() {
        prefs.edit()
            .putBoolean(KEY_TRIAL_STARTED, false)
            .remove(KEY_ACCESS_UNTIL)
            .putLong(KEY_LAST_SHARE_AT, 0L)
            .putInt(KEY_SHARE_PROGRESS, 0)
            .putBoolean(KEY_CODE_UNLOCK, false)
            .putBoolean(KEY_DEBUG_UNLOCK, false)
            .apply()
    }

    private fun lastShareAt(): Long {
        return prefs.getLong(KEY_LAST_SHARE_AT, 0L)
    }

    companion object {
        const val PREFS = "archivio_completo"
        const val KEY_DEBUG_UNLOCK = "debug_unlock"
        const val KEY_CODE_UNLOCK = "code_unlock"
        const val KEY_TRIAL_STARTED = "trial_started"
        const val KEY_ACCESS_UNTIL = "access_until"
        const val KEY_LAST_SHARE_AT = "last_share_at"
        const val KEY_SHARE_PROGRESS = "share_progress"
        const val KEY_TRIAL_DAYS = "param_trial_days"
        const val KEY_SHARE_BONUS_DAYS = "param_share_bonus_days"
        const val KEY_SHARE_FRIENDS = "param_share_friends"
    }
}
