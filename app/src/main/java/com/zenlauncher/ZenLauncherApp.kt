package com.zenlauncher

import android.app.Application
import android.app.AppOpsManager
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.zenlauncher.data.managers.AppUsageMonitorManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Classe de aplicação principal do MindfulLauncher.
 * 
 * Esta classe é responsável pela inicialização de componentes globais
 * e configuração da injeção de dependência via Hilt.
 */
@HiltAndroidApp
class MindfulLauncherApp : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var appUsageMonitorManager: AppUsageMonitorManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Timber para logging
        if (com.zenlauncher.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Iniciar monitoramento de uso de apps
        Timber.d("Iniciando monitoramento de uso de apps")
        
        // Verificar se temos permissão antes de iniciar
        if (hasUsageStatsPermission()) {
            appUsageMonitorManager.startMonitoring()
        } else {
            Timber.w("Permissão PACKAGE_USAGE_STATS não concedida")
        }
        
        // Outras inicializações de componentes globais
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
    }
    
    /**
     * Verifica se temos permissão para acessar estatísticas de uso.
     */
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    /**
     * Reinicia o monitoramento após concessão de permissão.
     */
    fun restartMonitoring() {
        if (hasUsageStatsPermission()) {
            Timber.d("Reiniciando monitoramento com permissão concedida")
            appUsageMonitorManager.startMonitoring()
        }
    }
}
