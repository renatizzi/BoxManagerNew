package com.example.boxmanagernew.backup.pipeline

import com.example.boxmanagernew.backup.report.BackupReport
import com.example.boxmanagernew.backup.result.BackupExecutionResult

/**
 * Risultato completo della pipeline di Backup.
 */
data class BackupPipelineResult(

    /**
     * Esito dell'esecuzione.
     */
    val executionResult: BackupExecutionResult,

    /**
     * Report statistico del Backup.
     */
    val report: BackupReport
)