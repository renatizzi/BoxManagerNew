package com.example.boxmanagernew.util

import java.text.Normalizer

object CanonicalNormalizer {

    private val excluded =
        setOf(
            "usb","hdmi","wifi","tv","pc","ssd","hdd",
            "bluetooth","ethernet","gps","dvd","ram",
            "cpu","gpu","lcd","oled"
        )

    private val irregulars =
        mapOf(
            "mano" to "mani",
            "mani" to "mano",

            "uomo" to "uomini",
            "uomini" to "uomo",

            "uovo" to "uova",
            "uova" to "uovo",

            "dito" to "dita",
            "dita" to "dito",

            "braccio" to "braccia",
            "braccia" to "braccio",

            "ginocchio" to "ginocchia",
            "ginocchia" to "ginocchio",

            "osso" to "ossa",
            "ossa" to "osso",

            "labbro" to "labbra",
            "labbra" to "labbro",

            "lenzuolo" to "lenzuola",
            "lenzuola" to "lenzuolo",

            "muro" to "mura",
            "mura" to "muro",

            "urlo" to "urla",
            "urla" to "urlo",

            "grido" to "grida",
            "grida" to "grido",

            "forbice" to "forbici",
            "forbici" to "forbice",

            "occhiale" to "occhiali",
            "occhiali" to "occhiale",

            "pantalone" to "pantaloni",
            "pantaloni" to "pantalone",

            "chiave" to "chiavi",
            "chiavi" to "chiave",

            "camicia" to "camicie",
            "camicie" to "camicia",

            "valigia" to "valigie",
            "valigie" to "valigia",

            "ciliegia" to "ciliegie",
            "ciliegie" to "ciliegia"
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

        val normalized =
            value.lowercase()

        if (
            normalized.length < 5 ||
            excluded.contains(normalized)
        ) {
            return normalized
        }

        irregulars[normalized]?.let {
            return it
        }

        return when {

            normalized.endsWith("a") ->
                normalized.dropLast(1) + "e"

            normalized.endsWith("e") -> {

                val singularA =
                    normalized.dropLast(1) + "a"

                if (
                    singularA.length >= 5
                ) {
                    singularA
                } else {
                    normalized.dropLast(1) + "i"
                }
            }

            normalized.endsWith("o") ->
                normalized.dropLast(1) + "i"

            normalized.endsWith("i") -> {

                val singularO =
                    normalized.dropLast(1) + "o"

                val singularE =
                    normalized.dropLast(1) + "e"

                if (
                    singularO.length >= 5
                ) {
                    singularO
                } else {
                    singularE
                }
            }

            else -> normalized
        }
    }

    fun irregularVariant(
        value: String
    ): String {

        return irregulars[
            value.lowercase()
        ] ?: value
    }
}