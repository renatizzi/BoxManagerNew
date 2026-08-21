package com.example.boxmanagernew.ui.qr

import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo

class QrLabelPrintAdapter(
    private val pdfBytes: ByteArray
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {

        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }

        val info =
            PrintDocumentInfo.Builder("Etichetta.pdf")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build()

        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {

        if (cancellationSignal.isCanceled) {
            callback.onWriteCancelled()
            return
        }

        try {
            ParcelFileDescriptor.AutoCloseOutputStream(destination).use { output ->
                output.write(pdfBytes)
            }
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (_: Exception) {
            callback.onWriteFailed(null)
        }
    }
}
