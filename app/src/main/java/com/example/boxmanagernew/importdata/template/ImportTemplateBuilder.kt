package com.example.boxmanagernew.importdata.template

import com.example.boxmanagernew.importdata.config.ImportConfiguration

class ImportTemplateBuilder {

    fun build(): ByteArray {
        val body = ImportConfiguration.TEMPLATE_LINES.joinToString(
            separator = "\r\n",
            postfix = "\r\n"
        )
        return ImportConfiguration.UTF8_BOM +
                body.toByteArray(Charsets.UTF_8)
    }
}
