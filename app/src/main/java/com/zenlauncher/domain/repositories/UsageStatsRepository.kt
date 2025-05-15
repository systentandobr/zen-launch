package com.zenlauncher.domain.repositories

import com.zenlauncher.domain.entities.AppUsageStats
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repositório para gerenciar estatísticas de uso de aplicativos.
 */
interface UsageStatsRepository {
    
    /**
     * Obtém estatísticas de uso de todos os aplicativos.
     * @return Flow com lista de estatísticas de uso
     */
    fun getAppUsageStats(): Flow<List<AppUsageStats>>
    
    /**
     * Obtém estatísticas de uso para um aplicativo específico.
     * @param packageName Nome do pacote do aplicativo
     * @return Flow com estatísticas de uso ou null se não encontrado
     */
    fun getAppUsageStats(packageName: String): Flow<AppUsageStats?>
    
    /**
     * Obtém o tempo total de uso do dispositivo.
     * @param startTime Data de início para o cálculo
     * @param endTime Data de fim para o cálculo
     * @return Tempo total de uso em milissegundos
     */
    suspend fun getTotalUsageTime(startTime: Date, endTime: Date): Long
    
    /**
     * Obtém os aplicativos mais usados no período especificado.
     * @param startTime Data de início para o cálculo
     * @param endTime Data de fim para o cálculo
     * @param limit Número máximo de aplicativos a retornar
     * @return Lista de estatísticas de uso ordenada por tempo de uso (decrescente)
     */
    suspend fun getMostUsedApps(
        startTime: Date,
        endTime: Date,
        limit: Int = 5
    ): List<AppUsageStats>
    
    /**
     * Registra um evento de uso de aplicativo.
     * Geralmente chamado quando o launcher detecta uma mudança de aplicativo em primeiro plano.
     * @param packageName Nome do pacote do aplicativo
     * @param usageTimeMs Tempo de uso em milissegundos
     */
    suspend fun recordAppUsage(packageName: String, usageTimeMs: Long)
}
