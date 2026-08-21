package com.example.boxmanagernew.domain.qr

/**
 * Testi 3.4.4 / catalogo 2.6. Importati, non riformulati.
 */
object QrConfiguration {

    const val MSG_UNRECOGNIZED =
        "Il codice QR non appartiene ad un archivio BoxManager."

    const val MSG_BOX_MISSING =
        "Il contenitore associato a questo codice QR non è presente nell'archivio."

    const val MSG_READ_ERROR =
        "Impossibile leggere il codice QR. Riprovare."

    const val MSG_DELETE =
        "Se elimini il contenitore, l'etichetta QR non sarà più utilizzabile. Confermi l'eliminazione?"
}
