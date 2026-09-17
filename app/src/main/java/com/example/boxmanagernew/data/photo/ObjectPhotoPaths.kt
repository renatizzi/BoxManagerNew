package com.example.boxmanagernew.data.photo

/**
 * Layout file foto oggetto (T1 / T3).
 * Display: `{permanentId}.jpg` — Thumb: `{permanentId}_thumb.jpg`
 */
object ObjectPhotoPaths {

    const val DIR_NAME = "object_photos"

    const val ZIP_DIR = "photos/objects/"

    fun displayFileName(permanentId: String): String =
        "$permanentId.jpg"

    fun thumbFileName(permanentId: String): String =
        "${permanentId}_thumb.jpg"

    fun zipDisplayEntry(permanentId: String): String =
        ZIP_DIR + displayFileName(permanentId)

    fun zipThumbEntry(permanentId: String): String =
        ZIP_DIR + thumbFileName(permanentId)
}
