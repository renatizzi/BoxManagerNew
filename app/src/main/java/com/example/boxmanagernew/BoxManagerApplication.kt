package com.example.boxmanagernew

import android.app.Application
import com.example.boxmanagernew.data.local.DatabaseProvider
import com.example.boxmanagernew.data.repository.BoxRepositoryImpl
import com.example.boxmanagernew.data.repository.ObjectRepositoryImpl
import com.example.boxmanagernew.data.trash.TrashStoreProvider
import com.example.boxmanagernew.ui.common.LocaleManager
import com.example.boxmanagernew.ui.common.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BoxManagerApplication : Application() {

    private val appScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {

        super.onCreate()

        ThemeManager.applyStoredNightMode(
            this
        )
        LocaleManager.applyStored(this)

        appScope.launch {
            val db = DatabaseProvider.getDatabase(this@BoxManagerApplication)
            val boxRepo = BoxRepositoryImpl(db.boxDao())
            val objectRepo =
                ObjectRepositoryImpl(
                    db.objectDao(),
                    db.objectTypeDao()
                )
            TrashStoreProvider.create(
                db,
                boxRepo,
                objectRepo,
                this@BoxManagerApplication
            ).purgeExpired()
        }
    }
}
