package com.example.boxmanagernew.domain.help

import android.content.Context
import com.example.boxmanagernew.R
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.viewoutput.config.ViewOutputConfiguration

/**
 * Guida rapida in-app. Non fa parte del catalogo 2.6.
 * Testi utente da risorse IT/EN.
 */
object QuickStartGuideCopy {

    enum class Phase(
        val labelRes: Int,
        val colorRes: Int
    ) {
        CONFIG(
            R.string.guide_phase_config,
            R.color.guide_phase_config
        ),
        CENSUS(
            R.string.guide_phase_census,
            R.color.guide_phase_census
        ),
        USAGE(
            R.string.guide_phase_usage,
            R.color.guide_phase_usage
        );

        fun label(context: Context): String {
            return context.getString(labelRes)
        }

        fun numberedLabel(context: Context): String {
            return context.getString(
                R.string.guide_phase_numbered,
                ordinal + 1,
                label(context)
            )
        }
    }

    data class Section(
        val phase: Phase,
        val number: Int,
        val title: String,
        val bullets: List<String>,
        val bodyIntro: String? = null,
        val bodyClosing: String? = null
    )

    fun pageTitle(context: Context): String =
        context.getString(R.string.guide_page_title)

    fun pageSubtitle(context: Context): String =
        context.getString(R.string.guide_page_subtitle)

    fun workflowTitle(context: Context): String =
        context.getString(R.string.guide_workflow_title)

    fun footerNote(context: Context): String =
        context.getString(R.string.guide_footer_note)

    fun sectionsFor(
        context: Context,
        includeFamilyBeta: Boolean
    ): List<Section> {
        return listOf(
            Section(
                phase = Phase.CONFIG,
                number = 1,
                title = context.getString(R.string.guide_section_settings_title),
                bullets = listOf(
                    context.getString(R.string.guide_section_settings_b1),
                    context.getString(R.string.guide_section_settings_b2),
                    context.getString(R.string.guide_section_settings_b3),
                    context.getString(R.string.guide_section_settings_b4),
                    context.getString(R.string.guide_section_settings_b5),
                    context.getString(R.string.guide_section_settings_b6)
                )
            ),
            Section(
                phase = Phase.CENSUS,
                number = 2,
                title = context.getString(R.string.guide_section_categories_title),
                bullets = buildList {
                    add(context.getString(R.string.guide_section_categories_b1))
                    add(context.getString(R.string.guide_section_categories_b2))
                    if (includeFamilyBeta) {
                        add(context.getString(R.string.guide_section_categories_b3))
                    }
                }
            ),
            Section(
                phase = Phase.CENSUS,
                number = 3,
                title = context.getString(R.string.guide_section_boxes_title),
                bullets = listOf(
                    context.getString(R.string.guide_section_boxes_b1),
                    context.getString(R.string.guide_section_boxes_b2),
                    context.getString(R.string.guide_section_boxes_b3),
                    context.getString(R.string.guide_section_boxes_b4)
                )
            ),
            Section(
                phase = Phase.USAGE,
                number = 4,
                title = context.getString(R.string.guide_section_dashboard_title),
                bullets = listOf(
                    context.getString(R.string.guide_section_dashboard_b1),
                    context.getString(R.string.guide_section_dashboard_b2),
                    context.getString(R.string.guide_section_dashboard_b3),
                    context.getString(R.string.guide_section_dashboard_b4)
                )
            ),
            Section(
                phase = Phase.USAGE,
                number = 5,
                title = context.getString(R.string.guide_section_utility_title),
                bullets = utilityBullets(context, includeFamilyBeta)
            ),
            Section(
                phase = Phase.USAGE,
                number = 6,
                title = context.getString(R.string.guide_section_tools_title),
                bodyIntro =
                    context.getString(R.string.guide_section_tools_intro) +
                        "\n\n" +
                        context.getString(R.string.guide_section_tools_containers_lead),
                bullets = listOf(
                    context.getString(R.string.guide_section_tools_b1),
                    context.getString(R.string.guide_section_tools_b2)
                )
            )
        )
    }

    private fun utilityBullets(
        context: Context,
        includeFamilyBeta: Boolean
    ): List<String> {
        return buildList {
            add(context.getString(R.string.guide_utility_backup))
            add(context.getString(R.string.guide_utility_import))
            add(context.getString(R.string.guide_utility_qr))
            add(context.getString(R.string.guide_utility_qr_batch))
            if (includeFamilyBeta) {
                add(context.getString(R.string.guide_utility_family_share))
                add(context.getString(R.string.guide_utility_family_merge))
            }
            add(context.getString(R.string.guide_utility_trash))
        }
    }

    fun csvFootnote(
        context: Context,
        includeFamilyBeta: Boolean
    ): String {
        val sep = ImportConfiguration.SEPARATOR
        return buildString {
            appendLine(
                context.getString(R.string.guide_csv_intro, sep)
            )
            appendLine()
            appendLine(
                context.getString(
                    R.string.guide_csv_import_format_title,
                    ImportConfiguration.FILE_NAME
                )
            )
            appendLine()
            appendLine(context.getString(R.string.guide_csv_header_label))
            appendLine(
                "formato${sep}" +
                    "${ImportConfiguration.FORMAT_NAME}" +
                    sep +
                    ImportConfiguration.FORMAT_VERSION_WITH_IDS
            )
            appendLine(
                "sezione${sep}" +
                    ImportConfiguration.SECTION_BOXES
            )
            appendLine(
                ImportConfiguration.COL_NAME +
                    sep +
                    ImportConfiguration.COL_CATEGORY +
                    sep +
                    ImportConfiguration.COL_POSITION +
                    sep +
                    ImportConfiguration.COL_PERMANENT_ID
            )
            appendLine(
                "sezione${sep}" +
                    ImportConfiguration.SECTION_OBJECTS
            )
            appendLine(
                ImportConfiguration.COL_NAME +
                    sep +
                    ImportConfiguration.COL_BOX +
                    sep +
                    ImportConfiguration.COL_DESCRIPTION +
                    sep +
                    ImportConfiguration.COL_QUANTITY +
                    sep +
                    ImportConfiguration.COL_OBJECT_PERMANENT_ID
            )
            appendLine()
            appendLine(
                context.getString(
                    R.string.guide_csv_import_note,
                    ImportConfiguration.PRE_IMPORT_PREFIX
                )
            )
            appendLine()
            appendLine(
                context.getString(
                    R.string.guide_csv_export_format_title,
                    ViewOutputConfiguration.EXPORT_FILE_PREFIX
                )
            )
            appendLine(context.getString(R.string.guide_csv_export_same_schema))
            appendLine()
            appendLine(context.getString(R.string.guide_zip_title))
            appendLine(context.getString(R.string.guide_zip_backup))
            appendLine(context.getString(R.string.guide_zip_export))
            if (includeFamilyBeta) {
                append(context.getString(R.string.guide_zip_family))
            }
        }.trimEnd()
    }
}
