package com.zenlauncher.data.datasources.system

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.zenlauncher.domain.entities.AppUsageStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Method
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados para acessar estatísticas de uso de aplicativos do sistema.
 * 
 * Nota: Requer a permissão PACKAGE_USAGE_STATS.
 */
@Singleton
class UsageStatsDataSource @Inject constructor(
    private val context: Context
) {
    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }
    private val packageManager by lazy {
        context.packageManager
    }
    
    /**
     * Verifica se temos permissão para acessar estatísticas de uso.
     * 
     * @return true se a permissão está concedida
     */
    fun hasUsageStatsPermission(): Boolean {
        val currentTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            currentTime - DAY_IN_MILLIS,
            currentTime
        )
        return stats.isNotEmpty()
    }
    
    /**
     * Obtém estatísticas de uso para todos os aplicativos em um período específico.
     * 
     * @param startTime Data de início para o cálculo
     * @param endTime Data de fim para o cálculo
     * @return Lista de estatísticas de uso de aplicativos
     */
    suspend fun getUsageStats(
        startTime: Date,
        endTime: Date
    ): List<AppUsageStats> = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) {
            return@withContext emptyList<AppUsageStats>()
        }
        
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startTime.time,
            endTime.time
        )
        
        return@withContext stats
            .filter { it.totalTimeInForeground > 0 }
            .map { stat ->
                mapToAppUsageStats(stat)
            }
            .filter { it != null }
            .filterNotNull()
    }
    
    /**
     * Obtém estatísticas de uso para um aplicativo específico.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param days Número de dias para calcular (contando de hoje para trás)
     * @return Estatísticas de uso do aplicativo ou null se não disponível
     */
    suspend fun getAppUsageStats(
        packageName: String,
        days: Int = 7
    ): AppUsageStats? = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) {
            return@withContext null
        }
        
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (days * DAY_IN_MILLIS)
        
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startTime,
            endTime
        )
        
        val appStats = stats.filter { it.packageName == packageName }
        
        if (appStats.isEmpty()) {
            return@withContext null
        }
        
        // Calcular tempo de uso hoje
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        val todayStats = stats.filter { 
            it.packageName == packageName && it.lastTimeUsed >= todayStart 
        }
        
        val usageTimeToday = todayStats.sumOf { it.totalTimeInForeground }
        val usageTimeWeek = appStats.sumOf { it.totalTimeInForeground }
        val lastUsed = appStats.maxOfOrNull { it.lastTimeUsed } ?: 0
        // Android Q (API 29) introduziu totalTimesLaunched
        val launchCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var totalLaunches = 0L
            for (stat in appStats) {
                try {
                    // Usar reflexão para acessar totalTimesLaunched com segurança
                    val method = stat.javaClass.getMethod("getTotalTimesLaunched")
                    val count = method.invoke(stat) as Int
                    totalLaunches += count.toLong()
                } catch (e: Exception) {
                    // Se ocorrer qualquer exceção, usar 0 como fallback
                }
            }
            totalLaunches
        } else {
            0L // Versões anteriores não suportam totalTimesLaunched
        }
        
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            
            return@withContext AppUsageStats(
                packageName = packageName,
                appName = appName,
                usageTimeToday = usageTimeToday,
                usageTimeWeek = usageTimeWeek,
                lastUsed = lastUsed,
                launchCount = launchCount
            )
        } catch (e: PackageManager.NameNotFoundException) {
            return@withContext null
        }
    }
    
    /**
     * Obtém o tempo total de uso do dispositivo.
     * 
     * @param startTime Data de início para o cálculo
     * @param endTime Data de fim para o cálculo
     * @return Tempo total de uso em milissegundos
     */
    suspend fun getTotalUsageTime(
        startTime: Date,
        endTime: Date
    ): Long = withContext(Dispatchers.IO) {
        if (!hasUsageStatsPermission()) {
            return@withContext 0L
        }
        
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startTime.time,
            endTime.time
        )
        
        return@withContext stats.sumOf { it.totalTimeInForeground }
    }
    
    /**
     * Converte UsageStats do sistema para a entidade AppUsageStats.
     */
    private fun mapToAppUsageStats(stat: UsageStats): AppUsageStats? {
        return try {
            val appInfo = packageManager.getApplicationInfo(stat.packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            
            // Android Q (API 29) introduziu totalTimesLaunched
            val launchCount = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    // Usar reflexão para acessar totalTimesLaunched com segurança
                    val method = stat.javaClass.getMethod("getTotalTimesLaunched")
                    val count = method.invoke(stat) as Int
                    count.toLong()
                } catch (e: Exception) {
                    // Se ocorrer qualquer exceção, usar 0 como fallback
                    0L
                }
            } else {
                0L
            }
            
            AppUsageStats(
                packageName = stat.packageName,
                appName = appName,
                usageTimeToday = 0, // Será calculado separadamente
                usageTimeWeek = stat.totalTimeInForeground,
                lastUsed = stat.lastTimeUsed,
                launchCount = launchCount
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    companion object {
        private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}
