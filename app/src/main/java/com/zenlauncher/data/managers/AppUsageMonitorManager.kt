package com.zenlauncher.data.managers

import android.content.Context
import androidx.work.*
import com.zenlauncher.data.workers.AppUsageMonitorWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador para agendar e controlar o monitoramento de uso de apps via WorkManager.
 */
@Singleton
class AppUsageMonitorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    companion object {
        private const val WORK_NAME = "app_usage_monitor_work"
        private const val MONITOR_INTERVAL_MINUTES = 5L // Verificar a cada 5 minutos
    }
    
    /**
     * Inicia o monitoramento periódico de uso de apps.
     */
    fun startMonitoring() {
        Timber.d("Iniciando monitoramento de uso de apps via WorkManager")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false) // Permitir execução mesmo com bateria baixa
            .setRequiresCharging(false)
            .setRequiresDeviceIdle(false)
            .build()
        
        val monitorRequest = PeriodicWorkRequestBuilder<AppUsageMonitorWorker>(
            MONITOR_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Manter o trabalho existente se já estiver agendado
            monitorRequest
        )
        
        Timber.d("WorkManager agendado para monitoramento a cada $MONITOR_INTERVAL_MINUTES minutos")
    }
    
    /**
     * Para o monitoramento de uso de apps.
     */
    fun stopMonitoring() {
        Timber.d("Parando monitoramento de uso de apps")
        workManager.cancelUniqueWork(WORK_NAME)
    }
    
    /**
     * Verifica se o monitoramento está ativo.
     */
    fun isMonitoringActive(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(WORK_NAME)
        return try {
            val workInfo = workInfos.get().firstOrNull()
            workInfo != null && !workInfo.state.isFinished
        } catch (e: Exception) {
            Timber.e(e, "Erro ao verificar status do monitoramento")
            false
        }
    }
    
    /**
     * Força uma verificação imediata (útil para testes ou quando o usuário muda configurações).
     */
    fun triggerImmediateCheck() {
        Timber.d("Triggering immediate usage check")
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()
        
        val immediateRequest = OneTimeWorkRequestBuilder<AppUsageMonitorWorker>()
            .setConstraints(constraints)
            .build()
        
        workManager.enqueue(immediateRequest)
    }
}
