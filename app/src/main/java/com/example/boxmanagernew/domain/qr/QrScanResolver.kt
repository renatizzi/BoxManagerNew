package com.example.boxmanagernew.domain.qr

import com.example.boxmanagernew.domain.model.Box

sealed class QrScanOutcome {
    data class OpenContainer(val box: Box) : QrScanOutcome()
    object Unrecognized : QrScanOutcome()
    object ContainerMissing : QrScanOutcome()
    object ReadError : QrScanOutcome()
}

object QrScanResolver {

    fun resolve(
        raw: String?,
        findByPermanentId: (String) -> Box?
    ): QrScanOutcome {

        return when (val parsed = BoxQrPayload.parse(raw)) {

            BoxQrPayload.Parse.Unreadable ->
                QrScanOutcome.ReadError

            BoxQrPayload.Parse.NotBoxManager ->
                QrScanOutcome.Unrecognized

            is BoxQrPayload.Parse.Identified -> {
                val box = findByPermanentId(parsed.permanentId)
                if (box == null) {
                    QrScanOutcome.ContainerMissing
                } else {
                    QrScanOutcome.OpenContainer(box)
                }
            }
        }
    }

    fun message(outcome: QrScanOutcome): String? {

        return when (outcome) {
            QrScanOutcome.Unrecognized ->
                QrConfiguration.MSG_UNRECOGNIZED
            QrScanOutcome.ContainerMissing ->
                QrConfiguration.MSG_BOX_MISSING
            QrScanOutcome.ReadError ->
                QrConfiguration.MSG_READ_ERROR
            is QrScanOutcome.OpenContainer ->
                null
        }
    }
}
