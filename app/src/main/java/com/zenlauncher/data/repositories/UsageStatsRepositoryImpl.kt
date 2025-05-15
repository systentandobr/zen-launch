package com.zenlauncher.data.repositories

import com.zenlauncher.data.datasources.system.UsageStatsDataSource
import com.zenlauncher.domain.entities.AppUsageStats
import com.zenlauncher.domain.repositories.UsageStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
     * Obtém estatísticas de uso de todos os aplicativos.
     */
    override fun getAppUsageStats(): Flow<List<AppUsageStats>> = flow {
        val endTime = Date()
        val startTime = Calendar.getInstance().apply {
            time = endTime
            add(Calendar.DAY_OF_YEAR, -7) // Últimos 7 dias
        }.time
        
        val stats = usageStatsDataSource.getUsageStats(startTime, endTime)
            .sortedByDescending { it.usageTimeWeek }
        
        emit(stats)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Obtém estatísticas de uso para um aplicativo específico.
     */
    override fun getAppUsageStats(packageName: String): Flow<AppUsageStats?> = flow {
        val stats = usageStatsDataSource.getAppUsageStats(packageName)
        emit(stats)
    }.flowOn(Dispatchers.IO)
    
    /**
     * Obtém o tempo total de uso do dispositivo.
     */
    override suspend fun getTotalUsageTime(
        startTime: Date,
        endTime: Date
    ): Long = withContext(Dispatchers.IO) {
        return@withContext usageStatsDataSource.getTotalUsageTime(startTime, endTime)
    }
    
    /**
     * Obtém os aplicativos mais usados no período especificado.
     */
    override suspend fun getMostUsedApps(
        startTime: Date,
        endTime: Date,
        limit: Int
    ): List<AppUsageStats> = withContext(Dispatchers.IO) {
        return@withContext usageStatsDataSource.getUsageStats(startTime, endTime)
            .sortedByDescending { it.usageTimeWeek }
            .take(limit)
    }
    
    /**
     * Registra um evento de uso de aplicativo.
     * Nota: No Android, isso é automaticamente rastreado pelo sistema,
     * então esta implementação é apenas para compatibilidade com a interface.
     */
    override suspend fun recordAppUsage(
        packageName: String,
        usageTimeMs: Long
    ) {
        // O Android já rastreia o uso automaticamente,
        // então não precisamos implementar esta funcionalidade
    }
}
