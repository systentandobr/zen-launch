package com.zenlauncher.data.workers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zenlauncher.domain.entities.AppMonitoringConfig
import com.zenlauncher.domain.repositories.AppBlockRepository
import com.zenlauncher.domain.repositories.AppMonitoringRepository
import com.zenlauncher.presentation.focus.UsageBlockActivity
import com.zenlauncher.presentation.focus.UsageWarningActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Worker do WorkManager para monitoramento periódico de uso de apps.
 * Mais eficiente que um serviço contínuo para verificações periódicas.
 */
@HiltWorker
class AppUsageMonitorWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val appBlockRepository: AppBlockRepository,
    private val monitoringRepository: AppMonitoringRepository
) : CoroutineWorker(context, workerParams) {

    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    override suspend fun doWork(): Result {
        return try {
            Timber.d("AppUsageMonitorWorker: Iniciando verificação")
            
            val currentPackage = getCurrentForegroundApp()
            if (currentPackage != null && currentPackage != context.packageName) {
                checkAppUsage(currentPackage)
            }
            
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Erro no AppUsageMonitorWorker")
            Result.retry()
        }
    }

    private suspend fun checkAppUsage(packageName: String) {
        // Verifica se o app está bloqueado manualmente
        val blockedApps = appBlockRepository.getActiveBlocks().first()
        val isBlocked = blockedApps.any { it.packageName == packageName }
        
        if (isBlocked) {
            // App já está bloqueado, não precisa verificar tempo de uso
            return
        }
        
        // Obtém configuração de monitoramento
        val config = monitoringRepository.getMonitoringConfig(packageName).first()
            ?: AppMonitoringConfig(packageName)
        
        // Se excluído do monitoramento, ignora
        if (config.isExcludedFromMonitoring) {
            return
        }
        
        // Calcula tempo de uso hoje
        val usageTimeToday = getTodayUsageTime(packageName)
        val usageMinutes = usageTimeToday / (1000 * 60) // Converte para minutos
        
        Timber.d("App $packageName usado por $usageMinutes minutos hoje")
        
        // Verifica se deve mostrar aviso ou bloqueio
        when {
            usageMinutes >= config.blockTimeMinutes -> {
                showUsageBlockActivity(packageName, usageMinutes)
            }
            usageMinutes >= config.warningTimeMinutes -> {
                // Verifica se já foi mostrado aviso recentemente
                if (!wasWarningShownRecently(packageName)) {
                    showUsageWarningActivity(packageName, usageMinutes)
                    markWarningShown(packageName)
                }
            }
        }
    }

    private fun getCurrentForegroundApp(): String? {
        return try {
            val currentTime = System.currentTimeMillis()
            val usageEvents = usageStatsManager.queryEvents(currentTime - 5000, currentTime)
            
            var lastForegroundPackage: String? = null
            val event = UsageEvents.Event()
            
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    lastForegroundPackage = event.packageName
                }
            }
            
            lastForegroundPackage
        } catch (e: Exception) {
            Timber.e(e, "Erro ao obter app em primeiro plano")
            null
        }
    }

    private fun getTodayUsageTime(packageName: String): Long {
        return try {
            val calendar = java.util.Calendar.getInstance()
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val todayStart = calendar.timeInMillis
            val now = System.currentTimeMillis()
            
            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                todayStart,
                now
            )
            
            usageStats.filter { it.packageName == packageName }
                .sumOf { it.totalTimeInForeground }
        } catch (e: Exception) {
            Timber.e(e, "Erro ao calcular tempo de uso")
            0L
        }
    }

    private fun showUsageWarningActivity(packageName: String, usageMinutes: Long) {
        val intent = Intent(context, UsageWarningActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("PACKAGE_NAME", packageName)
            putExtra("USAGE_MINUTES", usageMinutes)
        }
        context.startActivity(intent)
    }

    private fun showUsageBlockActivity(packageName: String, usageMinutes: Long) {
        val intent = Intent(context, UsageBlockActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("PACKAGE_NAME", packageName)
            putExtra("USAGE_MINUTES", usageMinutes)
        }
        context.startActivity(intent)
    }

    private suspend fun wasWarningShownRecently(packageName: String): Boolean {
        // Implementar lógica para verificar se o warning foi mostrado recentemente
        // Por simplicidade, assumimos que não foi mostrado
        return false
    }

    private suspend fun markWarningShown(packageName: String) {
        // Implementar lógica para marcar que o warning foi mostrado
        // Pode usar DataStore para persistir essa informação
        Timber.d("Warning marcado como mostrado para $packageName")
    }
}
