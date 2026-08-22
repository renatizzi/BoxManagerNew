package com.example.boxmanagernew.util

import java.text.Normalizer

/**
 * Ricerca semplice: sottostringa inline, minimo 3 caratteri.
 * Filtro e giallo usano la stessa chiave.
 * Nessuna inflessione (plurali). I caratteri speciali e gli accenti
 * si normalizzano come CanonicalNormalizer (non entrano nel confronto).
 */
object SimpleSearch {

    const val MIN_LENGTH = 3

    private val combining =
        "\\p{InCombiningDiacriticalMarks}".toRegex()

    fun needle(
        query: String
    ): String {

        val normalized =
            normalize(query)

        return if (normalized.length >= MIN_LENGTH) {
            normalized
        } else {
            ""
        }
    }

    fun matches(
        text: String,
        query: String
    ): Boolean {

        val n =
            needle(query)

        if (n.isEmpty()) {
            return false
        }

        return normalize(text).contains(n)
    }

    fun matchesAny(
        query: String,
        vararg texts: String?
    ): Boolean {

        return texts.any { text ->

            !text.isNullOrBlank() &&
                    matches(text, query)
        }
    }

    fun highlightRanges(
        text: String,
        query: String
    ): List<IntRange> {

        val n =
            needle(query)

        if (
            n.isEmpty() ||
            text.isBlank()
        ) {
            return emptyList()
        }

        val mapped =
            normalizeWithIndexMap(text)

        val normalized =
            mapped.first

        val indexMap =
            mapped.second

        if (normalized.isEmpty()) {
            return emptyList()
        }

        val ranges =
            mutableListOf<IntRange>()

        var from = 0

        while (from <= normalized.length - n.length) {

            val start =
                normalized.indexOf(n, from)

            if (start < 0) {
                break
            }

            val end =
                start + n.length - 1

            ranges.add(
                indexMap[start]..indexMap[end]
            )

            from = start + n.length
        }

        return ranges
    }

    fun normalize(
        value: String
    ): String {

        return normalizeWithIndexMap(value).first
    }

    internal fun normalizeWithIndexMap(
        value: String
    ): Pair<String, IntArray> {

        var lo = 0
        var hi = value.length

        while (
            lo < hi &&
            value[lo].isWhitespace()
        ) {
            lo++
        }

        while (
            hi > lo &&
            value[hi - 1].isWhitespace()
        ) {
            hi--
        }

        val out =
            StringBuilder()

        val map =
            mutableListOf<Int>()

        var pendingSpace = false
        var emittedNonSpace = false

        var i = lo

        while (i < hi) {

            val cp =
                value.codePointAt(i)

            val origIndex = i

            i += Character.charCount(cp)

            val lowered =
                String(
                    Character.toChars(
                        Character.toLowerCase(cp)
                    )
                )

            val nfd =
                Normalizer.normalize(
                    lowered,
                    Normalizer.Form.NFD
                )

            for (ch in nfd) {

                if (
                    combining.containsMatchIn(
                        ch.toString()
                    )
                ) {
                    continue
                }

                val mapped =
                    if (
                        ch in 'a'..'z' ||
                        ch in '0'..'9'
                    ) {
                        ch
                    } else {
                        ' '
                    }

                if (mapped == ' ') {

                    if (emittedNonSpace) {
                        pendingSpace = true
                    }

                } else {

                    if (pendingSpace) {

                        out.append(' ')
                        map.add(origIndex)
                        pendingSpace = false
                    }

                    out.append(mapped)
                    map.add(origIndex)
                    emittedNonSpace = true
                }
            }
        }

        return out.toString() to map.toIntArray()
    }
}
