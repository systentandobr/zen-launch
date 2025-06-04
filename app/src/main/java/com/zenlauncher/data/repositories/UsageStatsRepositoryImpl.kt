package com.zenlauncher.data.repositories

import com.zenlauncher.data.datasources.system.UsageStatsDataSource
import com.zenlauncher.domain.entities.AppUsageStat
import com.zenlauncher.domain.repositories.UsageStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de estatísticas de uso.
 */
@Singleton
class UsageStatsRepositoryImpl @Inject constructor(
    private val usageStatsDataSource: UsageStatsDataSource
) : UsageStatsRepository {
    
    /**
     * Obtém estatísticas de uso de aplicativos para um período específico.
     * 
     * @param daysBack Número de dias para olhar para trás
     * @param limit Número máximo de resultados
     * @return Lista de estatísticas de uso ordenada por tempo total
     */
    override suspend fun getAppUsageStats(daysBack: Int, limit: Int): List<AppUsageStat> = withContext(Dispatchers.IO) {
        val endTime = System.currentTimeMillis()
        val startTime = Calendar.getInstance().apply {
            timeInMillis = endTime
            add(Calendar.DAY_OF_YEAR, -daysBack)
        }.timeInMillis
        
        // Transformar os dados da fonte de dados para o modelo de domínio
        return@withContext getMostUsedApps(Date(startTime), Date(endTime), limit)
            .map { stats ->
                AppUsageStat(
                    packageName = stats.packageName,
                    totalTimeUsed = stats.usageTimeWeek,
                    launchCount = stats.launchCount.toInt(),
                    lastTimeUsed = stats.lastUsed
                )
            }
    }
    
    /**
     * Obtém estatísticas de uso para um aplicativo específico.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param daysBack Número de dias para olhar para trás
     * @return Estatística de uso ou null se não houver dados
     */
    override suspend fun getAppUsageStat(packageName: String, daysBack: Int): AppUsageStat? = withContext(Dispatchers.IO) {
        val endTime = System.currentTimeMillis()
        val startTime = Calendar.getInstance().apply {
            timeInMillis = endTime
            add(Calendar.DAY_OF_YEAR, -daysBack)
        }.timeInMillis
        
        val stats = usageStatsDataSource.getAppUsageStats(packageName) ?: return@withContext null
        
        return@withContext AppUsageStat(
            packageName = stats.packageName,
            totalTimeUsed = stats.usageTimeWeek,
            launchCount = stats.launchCount.toInt(),
            lastTimeUsed = stats.lastUsed
        )
    }
    
    /**
     * Obtém o tempo total de uso do dispositivo.
     * 
     * @param startTime Data de início do período
     * @param endTime Data de fim do período
     * @return Tempo total de uso em milissegundos
     */
    override suspend fun getTotalUsageTime(
        startTime: Date,
        endTime: Date
    ): Long = withContext(Dispatchers.IO) {
        return@withContext usageStatsDataSource.getTotalUsageTime(startTime, endTime)
    }
    
    /**
     * Obtém os aplicativos mais usados no período especificado.
     * 
     * @param startTime Data de início do período
     * @param endTime Data de fim do período
     * @param limit Número máximo de resultados
     * @return Lista de estatísticas de uso ordenada por tempo total
     */
    override suspend fun getMostUsedApps(
        startTime: Date,
        endTime: Date,
        limit: Int
    ): List<com.zenlauncher.domain.entities.AppUsageStats> = withContext(Dispatchers.IO) {
        return@withContext usageStatsDataSource.getUsageStats(startTime, endTime)
            .sortedByDescending { it.usageTimeWeek }
            .take(limit)
    }
    
    /**
     * Registra um evento de uso de aplicativo.
     * Nota: No Android, isso é automaticamente rastreado pelo sistema,
     * então esta implementação é apenas para compatibilidade com a interface.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param usageTimeMs Tempo de uso em milissegundos
     */
    override suspend fun recordAppUsage(
        packageName: String,
        usageTimeMs: Long
    ) {
        // O Android já rastreia o uso automaticamente,
        // então não precisamos implementar esta funcionalidade
    }
}
