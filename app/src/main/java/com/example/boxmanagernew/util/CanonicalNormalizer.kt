package com.example.boxmanagernew.util

import java.text.Normalizer

object CanonicalNormalizer {

    private val excluded =
        setOf(
            "usb","hdmi","wifi","tv","pc","ssd","hdd",
            "bluetooth","ethernet","gps","dvd","ram",
            "cpu","gpu","lcd","oled"
        )

    fun canonical(
        value: String
    ): String {

        return normalize(value)
            .replace(" ", "")
    }

    fun normalize(
        value: String
    ): String {

        return Normalizer
            .normalize(
                value.lowercase().trim(),
                Normalizer.Form.NFD
            )
            .replace(
                "\\p{InCombiningDiacriticalMarks}+"
                    .toRegex(),
                ""
            )
            .replace(
                "[^a-z0-9 ]"
                    .toRegex(),
                " "
            )
            .replace(
                "\\s+"
                    .toRegex(),
                " "
            )
            .trim()
    }

    fun singularPluralVariant(
        value: String
    ): String {

        if (
            value.length < 5 ||
            excluded.contains(value)
        ) return value

        return when {

            value.endsWith("a") ->
                value.dropLast(1) + "e"

            value.endsWith("e") ->
                value.dropLast(1) + "i"

            value.endsWith("o") ->
                value.dropLast(1) + "i"

            value.endsWith("i") ->
                value.dropLast(1) + "e"

            else -> value
        }
    }

    fun irregularVariant(
        value: String
    ): String {

        return when (value) {

            "mano" -> "mani"
            "mani" -> "mano"

            "uomo" -> "uomini"
            "uomini" -> "uomo"

            "uovo" -> "uova"
            "uova" -> "uovo"

            else -> value
        }
    }
}