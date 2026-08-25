package com.example.boxmanagernew.ui.premium

import android.app.Activity
import android.content.Intent
import com.example.boxmanagernew.domain.premium.ArchivioCompletoAccess
import com.example.boxmanagernew.domain.premium.PremiumFeature
import com.example.boxmanagernew.ui.common.BottomNavManager

object ArchivioCompletoNav {

    const val EXTRA_GRANTED =
        "archivio_completo_granted"

    fun start(
        activity: Activity,
        feature: PremiumFeature,
        target: Intent
    ) {
        run(activity, feature) {
            target.putExtra(EXTRA_GRANTED, true)
            activity.startActivity(target)
        }
    }

    fun run(
        activity: Activity,
        feature: PremiumFeature,
        proceed: () -> Unit
    ) {
        val access =
            ArchivioCompletoAccess(activity)

        if (access.isOpen()) {
            proceed()
            return
        }

        pending = {
            val latest =
                ArchivioCompletoAccess(activity)
            if (
                feature != PremiumFeature.ADVANCED_SEARCH &&
                !latest.isOpen() &&
                latest.canTrial(feature)
            ) {
                latest.consumeTrial(feature)
            }
            proceed()
        }

        activity.startActivity(
            Intent(
                activity,
                ArchivioCompletoActivity::class.java
            ).putExtra(
                ArchivioCompletoActivity.EXTRA_FEATURE,
                feature.name
            ).putExtra(
                ArchivioCompletoActivity.EXTRA_NAV_TAB,
                BottomNavManager.tabFor(activity)
                    ?: defaultNavTab(feature)
            )
        )
    }

    fun allowActivity(
        activity: Activity,
        feature: PremiumFeature
    ): Boolean {
        val access =
            ArchivioCompletoAccess(activity)

        if (access.isOpen()) {
            return true
        }

        if (
            activity.intent.getBooleanExtra(
                EXTRA_GRANTED,
                false
            )
        ) {
            return true
        }

        val replay =
            Intent(
                activity,
                activity.javaClass
            ).putExtras(
                activity.intent
            )

        replay.removeExtra(EXTRA_GRANTED)

        start(activity, feature, replay)
        activity.finish()
        return false
    }

    internal var pending: (() -> Unit)? = null

    private fun defaultNavTab(
        feature: PremiumFeature
    ): Int {
        return when (feature) {
            PremiumFeature.ADVANCED_SEARCH ->
                BottomNavManager.TAB_DASHBOARD

            PremiumFeature.QR_SCAN,
            PremiumFeature.IMPORT ->
                BottomNavManager.TAB_UTILITY

            PremiumFeature.QR_LABEL,
            PremiumFeature.EXPORT ->
                BottomNavManager.TAB_BOXES
        }
    }
}
