package com.example.boxmanagernew.importdata.inspect

/**
 * Errore di import con traccia riga (B–C R6 / G1).
 * [line] = numero riga 1-based nel file sorgente (dopo trim; righe vuote escluse dal parsing
 * ma il numero resta quello del file grezzo).
 */
data class ImportRowError(
    val section: String,
    val line: Int,
    val reason: String
)
