package com.example.boxmanagernew.backup.pipeline

import com.example.boxmanagernew.backup.report.BackupReport
import com.example.boxmanagernew.backup.result.BackupExecutionResult

/**
 * Orchestratore della pipeline di Backup.
 *
 * Coordina l'intero flusso senza conoscere
 * il layer dati o la UI.
 */
class BackupPipeline {

    fun execute(
        executionResult: BackupExecutionResult,
        report: BackupReport
    ): BackupPipelineResult {

        return BackupPipelineResult(
            executionResult = executionResult,
            report = report
        )
    }
}