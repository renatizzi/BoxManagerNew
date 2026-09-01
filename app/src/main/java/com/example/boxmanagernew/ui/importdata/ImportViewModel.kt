package com.example.boxmanagernew.ui.importdata

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.boxmanagernew.backup.config.BackupConfiguration
import com.example.boxmanagernew.backup.facade.BackupFacade
import com.example.boxmanagernew.data.local.dao.ObjectTypeDao
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.CategoryRepositoryImpl
import com.example.boxmanagernew.data.repository.LocationRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.importdata.config.ImportConfiguration
import com.example.boxmanagernew.importdata.inspect.ImportDependencyValidator
import com.example.boxmanagernew.importdata.inspect.ImportFileInspector
import com.example.boxmanagernew.importdata.merge.ImportMergeApplier
import com.example.boxmanagernew.importdata.merge.ImportMergePlanner
import com.example.boxmanagernew.importdata.template.ImportTemplateBuilder
import com.example.boxmanagernew.ui.backup.BackupZipPersister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImportViewModel(
    private val boxRepository: BoxRepositoryImpl,
    private val objectRepository: ObjectRepositoryImpl,
    private val categoryRepository: CategoryRepositoryImpl,
    private val locationRepository: LocationRepositoryImpl,
    private val objectTypeDao: ObjectTypeDao,
    private val backupFacade: BackupFacade,
    private val mergeApplier: ImportMergeApplier,
    private val templateBuilder: ImportTemplateBuilder = ImportTemplateBuilder(),
    private val fileInspector: ImportFileInspector = ImportFileInspector(),
    private val dependencyValidator: ImportDependencyValidator = ImportDependencyValidator(),
    private val mergePlanner: ImportMergePlanner = ImportMergePlanner()
) : ViewModel() {

    data class UserMessage(
        val text: String,
        val blockingError: Boolean = false
    )

    private val _busy = MutableLiveData(false)
    val busy: LiveData<Boolean> = _busy

    private val _message = MutableLiveData(UserMessage(""))
    val message: LiveData<UserMessage> = _message

    private val _awaitingAutoBackup = MutableLiveData<String?>(null)
    val awaitingAutoBackup: LiveData<String?> = _awaitingAutoBackup

    private var lastPreview: String = ""
    private var pendingImport: ImportFileInspector.Result.Ready? = null

    fun persistTemplate(
        treeUri: Uri,
        fileName: String,
        overwrite: Boolean,
        persister: ImportTemplatePersister
    ) {

        viewModelScope.launch {

            _busy.value = true

            try {

                val result = withContext(Dispatchers.IO) {
                    persister.persist(
                        treeUri = treeUri,
                        fileName = fileName,
                        bytes = templateBuilder.build(),
                        overwrite = overwrite
                    )
                }

                if (result.success) {

                    _message.value = UserMessage(
                        buildConfirmation(result)
                    )

                } else if (result.folderInaccessible) {

                    _message.value = UserMessage(
                        BackupConfiguration.MSG_FOLDER_INACCESSIBLE,
                        blockingError = true
                    )

                } else {

                    _message.value = UserMessage(
                        "",
                        blockingError = true
                    )
                }

            } finally {

                _busy.value = false
            }
        }
    }

    fun inspectImportFile(
        fileName: String,
        bytes: ByteArray?
    ) {

        viewModelScope.launch {

            _busy.value = true
            _awaitingAutoBackup.value = null
            pendingImport = null

            try {

                val result = withContext(Dispatchers.IO) {
                    val inspected = fileInspector.inspect(bytes)
                    if (inspected is ImportFileInspector.Result.Ready) {
                        val dependencies = dependencyValidator.validate(
                            boxes = inspected.boxes,
                            objects = inspected.objects,
                            categoryNames = categoryRepository
                                .getAllCategoryEntitiesSync()
                                .map { it.name },
                            locationNames = locationRepository
                                .getAllLocationEntitiesSync()
                                .map { it.name },
                            archiveBoxNames = boxRepository
                                .getAllBoxEntitiesSync()
                                .map { it.name }
                        )
                        Pair(inspected, dependencies)
                    } else {
                        Pair(inspected, null)
                    }
                }

                val inspected = result.first
                val dependencies = result.second

                when {
                    inspected is ImportFileInspector.Result.Failed -> {
                        _message.value = UserMessage(
                            buildInspectFailure(inspected.check),
                            blockingError = true
                        )
                    }
                    dependencies is ImportDependencyValidator.Result.Failed -> {
                        _message.value = UserMessage(
                            buildDependencyFailure(dependencies.message),
                            blockingError = true
                        )
                    }
                    inspected is ImportFileInspector.Result.Ready -> {
                        pendingImport = inspected
                        lastPreview = buildPreview(fileName, inspected)
                        _message.value = UserMessage(lastPreview)
                        _awaitingAutoBackup.value = lastPreview
                    }
                    else -> {
                        _message.value = UserMessage("")
                    }
                }

            } finally {

                _busy.value = false
            }
        }
    }

    fun persistAutoBackup(
        treeUri: Uri,
        applicationVersion: String,
        fileName: String,
        overwrite: Boolean,
        persister: BackupZipPersister
    ) {

        viewModelScope.launch {

            _busy.value = true
            _awaitingAutoBackup.value = null

            try {

                val result = withContext(Dispatchers.IO) {
                    val payload = backupFacade.exportPayload(
                        boxes = boxRepository.getAllBoxEntitiesSync(),
                        objects = objectRepository.getAllObjectEntitiesSync(),
                        categories = categoryRepository.getAllCategoryEntitiesSync(),
                        locations = locationRepository.getAllLocationEntitiesSync(),
                        objectTypes = objectTypeDao.getAllTypesSync(),
                        applicationVersion = applicationVersion
                    )

                    persister.persist(
                        treeUri = treeUri,
                        fileName = fileName,
                        entries = payload,
                        overwrite = overwrite
                    )
                }

                if (result.success) {

                    mergePendingImport(result)

                } else if (result.folderInaccessible) {

                    pendingImport = null
                    _message.value = UserMessage(
                        BackupConfiguration.MSG_FOLDER_INACCESSIBLE,
                        blockingError = true
                    )

                } else {

                    pendingImport = null
                    _message.value = UserMessage(
                        BackupConfiguration.MSG_WRITE_FAILED,
                        blockingError = true
                    )
                }

            } finally {

                _busy.value = false
            }
        }
    }

    fun autoBackupFileName(): String {
        return ImportConfiguration.preImportFileName()
    }

    private suspend fun mergePendingImport(
        backup: BackupZipPersister.Result
    ) {

        val pending = pendingImport
        if (pending == null) {
            _message.value = UserMessage(
                buildAutoBackupSummary(backup)
            )
            return
        }

        try {

            val plan = withContext(Dispatchers.IO) {
                val categories = categoryRepository
                    .getAllCategoryEntitiesSync()
                    .associate { it.id to it.name }
                val archiveBoxes = boxRepository.getAllBoxEntitiesSync().map { box ->
                    ImportMergePlanner.ArchiveBox(
                        name = box.name,
                        categoryName = categories[box.categoryId].orEmpty(),
                        position = box.position
                    )
                }
                val boxNames = boxRepository.getAllBoxEntitiesSync()
                    .associate { it.id to it.name }
                val typeNames = objectTypeDao.getAllTypesSync()
                    .associate { it.id to it.name }
                val archiveObjects = objectRepository.getAllObjectEntitiesSync().map { obj ->
                    ImportMergePlanner.ArchiveObject(
                        typeName = typeNames[obj.typeObjectId].orEmpty(),
                        boxName = boxNames[obj.boxId].orEmpty(),
                        description = obj.description,
                        quantity = obj.quantity
                    )
                }

                val planned = mergePlanner.plan(
                    fileBoxes = pending.boxes,
                    fileObjects = pending.objects,
                    archiveBoxes = archiveBoxes,
                    archiveObjects = archiveObjects
                )

                if (planned.canApply) {
                    mergeApplier.apply(planned)
                }

                planned
            }

            pendingImport = null

            if (plan.canApply) {
                _message.value = UserMessage(
                    buildFinalReport(backup, plan)
                )
            } else {
                _message.value = UserMessage(
                    buildFinalReport(backup, plan) +
                        "\n\n" +
                        ImportConfiguration.MSG_IMPORT_CANCELLED,
                    blockingError = true
                )
            }

        } catch (_: Exception) {

            pendingImport = null
            _message.value = UserMessage(
                ImportConfiguration.MSG_IMPORT_CANCELLED,
                blockingError = true
            )
        }
    }

    private fun buildFinalReport(
        backup: BackupZipPersister.Result,
        plan: ImportMergePlanner.Plan
    ): String {

        return buildString {
            appendLine(lastPreview)
            appendLine()
            appendLine(BackupConfiguration.MSG_BACKUP_COMPLETED)
            appendLine("Nome file: ${backup.fileName}")
            appendLine("Cartella: ${backup.folderName}")
            appendLine()
            appendLine("${ImportConfiguration.REPORT_RECORDS_READ}: ${plan.recordsRead}")
            appendLine("${ImportConfiguration.REPORT_IMPORTED}: ${plan.imported}")
            appendLine("${ImportConfiguration.REPORT_IGNORED}: ${plan.ignoredDuplicates}")
            append("${ImportConfiguration.REPORT_DISCARDED}: ${plan.discardedErrors}")
        }
    }

    private fun buildAutoBackupSummary(
        result: BackupZipPersister.Result
    ): String {

        return buildString {
            appendLine(lastPreview)
            appendLine()
            appendLine(BackupConfiguration.MSG_BACKUP_COMPLETED)
            appendLine("Nome file: ${result.fileName}")
            append("Cartella: ${result.folderName}")
        }
    }

    private fun buildPreview(
        fileName: String,
        ready: ImportFileInspector.Result.Ready
    ): String {

        return buildString {
            appendLine("Nome file: $fileName")
            appendLine("${ImportConfiguration.REPORT_RECORDS_READ}: ${ready.recordsRead}")
            appendLine("Contenitori: ${ready.boxes.size}")
            append("Oggetti: ${ready.objects.size}")
        }
    }

    private fun buildDependencyFailure(
        message: String
    ): String {

        return buildString {
            appendLine(message)
            appendLine()
            append(ImportConfiguration.MSG_RELATION_CANCELLED)
        }
    }

    private fun buildInspectFailure(
        check: String
    ): String {

        return buildString {
            appendLine(check)
            appendLine()
            append(ImportConfiguration.MSG_IMPORT_CANCELLED)
        }
    }

    private fun buildConfirmation(
        result: ImportTemplatePersister.Result
    ): String {

        return buildString {
            appendLine("Nome file: ${result.fileName}")
            append("Cartella: ${result.folderName}")
        }
    }
}
