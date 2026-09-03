package com.example.boxmanagernew.domain.premium

import android.content.Context
import com.example.boxmanagernew.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Testi dello sblocco commerciale. Non fanno parte del catalogo 2.6.
 * Testi utente da risorse IT/EN.
 */
object ArchivioCompletoCopy {

    data class FeaturePitch(
        val lead: String,
        val example: String?,
        val callToAction: String? = null
    )

    fun pageSubtitle(context: Context): String =
        context.getString(R.string.premium_page_subtitle)

    fun buttonShare(context: Context): String =
        context.getString(R.string.premium_button_share)

    fun buttonRedeem(context: Context): String =
        context.getString(R.string.premium_button_redeem)

    fun buttonClose(context: Context): String =
        context.getString(R.string.premium_button_close)

    fun codeHint(context: Context): String =
        context.getString(R.string.premium_code_prompt)

    fun unlockCodeHint(context: Context): String =
        context.getString(R.string.premium_unlock_code_hint)

    fun codeOk(context: Context): String =
        context.getString(R.string.premium_code_ok)

    fun codeKo(context: Context): String =
        context.getString(R.string.premium_code_ko)

    fun packageShareHint(
        context: Context,
        trialDays: Int,
        bonusDays: Int,
        friends: Int
    ): String {
        return if (friends <= 1) {
            context.getString(
                R.string.premium_share_hint_one,
                trialDays,
                bonusDays
            )
        } else {
            context.getString(
                R.string.premium_share_hint_many,
                trialDays,
                friends,
                bonusDays
            )
        }
    }

    fun shareGranted(context: Context, bonusDays: Int): String =
        context.getString(R.string.premium_share_granted, bonusDays)

    fun shareProgressLine(
        context: Context,
        done: Int,
        required: Int
    ): String =
        context.getString(R.string.premium_share_progress, done, required)

    fun featureTitle(context: Context, feature: PremiumFeature): String {
        return when (feature) {
            PremiumFeature.ADVANCED_SEARCH ->
                context.getString(R.string.premium_feature_advanced_search)
            PremiumFeature.QR_SCAN ->
                context.getString(R.string.premium_feature_qr_scan)
            PremiumFeature.QR_LABEL ->
                context.getString(R.string.premium_feature_qr_label)
            PremiumFeature.IMPORT ->
                context.getString(R.string.premium_feature_import)
            PremiumFeature.EXPORT ->
                context.getString(R.string.premium_feature_export)
        }
    }

    fun pitch(context: Context, feature: PremiumFeature): FeaturePitch {
        return when (feature) {
            PremiumFeature.ADVANCED_SEARCH ->
                FeaturePitch(
                    lead = context.getString(
                        R.string.premium_pitch_advanced_search_lead
                    ),
                    example = context.getString(
                        R.string.premium_pitch_advanced_search_example
                    )
                )

            PremiumFeature.QR_SCAN ->
                FeaturePitch(
                    lead = context.getString(
                        R.string.premium_pitch_qr_scan_lead
                    ),
                    example = context.getString(
                        R.string.premium_pitch_qr_scan_example
                    )
                )

            PremiumFeature.QR_LABEL ->
                FeaturePitch(
                    lead = context.getString(
                        R.string.premium_pitch_qr_label_lead
                    ),
                    example = context.getString(
                        R.string.premium_pitch_qr_label_example
                    )
                )

            PremiumFeature.IMPORT ->
                FeaturePitch(
                    lead = context.getString(
                        R.string.premium_pitch_import_lead
                    ),
                    example = context.getString(
                        R.string.premium_pitch_import_example
                    )
                )

            PremiumFeature.EXPORT ->
                FeaturePitch(
                    lead = context.getString(
                        R.string.premium_pitch_export_lead
                    ),
                    example = context.getString(
                        R.string.premium_pitch_export_example
                    )
                )
        }
    }

    fun trialStatusLine(
        context: Context,
        remainingDays: Int,
        accessUntil: Long
    ): String {
        if (remainingDays <= 0) {
            return context.getString(R.string.premium_trial_ended)
        }
        val until = formatDay(accessUntil)
        return if (remainingDays == 1) {
            context.getString(R.string.premium_trial_status_one, until)
        } else {
            context.getString(
                R.string.premium_trial_status_many,
                remainingDays,
                until
            )
        }
    }

    fun shareCooldownLine(
        context: Context,
        remainingMs: Long,
        bonusDays: Int
    ): String {
        val hours =
            ((remainingMs + 3_599_999L) / 3_600_000L).toInt()
        return if (hours <= 1) {
            context.getString(
                R.string.premium_share_cooldown_one,
                bonusDays
            )
        } else {
            context.getString(
                R.string.premium_share_cooldown_many,
                bonusDays,
                hours
            )
        }
    }

    fun shareMessage(context: Context, playUrl: String): String =
        context.getString(R.string.premium_share_message, playUrl)

    fun formatDay(epochMs: Long): String {
        val fmt =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return fmt.format(Date(epochMs))
    }
}
