package com.zenlauncher.domain.repositories

import com.zenlauncher.domain.entities.AppUsageStat
import com.zenlauncher.domain.entities.AppUsageStats
import java.util.Date

/**
 * Repositório para gerenciar estatísticas de uso de aplicativos.
 */
interface UsageStatsRepository {
    
    /**
     * Obtém estatísticas de uso de aplicativos para um período específico.
     * 
     * @param daysBack Número de dias para olhar para trás
     * @param limit Número máximo de resultados
     * @return Lista de estatísticas de uso ordenada por tempo total
     */
    suspend fun getAppUsageStats(daysBack: Int = 7, limit: Int = 10): List<AppUsageStat>
    
    /**
     * Obtém estatísticas de uso para um aplicativo específico.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param daysBack Número de dias para olhar para trás
     * @return Estatística de uso ou null se não houver dados
     */
    suspend fun getAppUsageStat(packageName: String, daysBack: Int = 7): AppUsageStat?
    
    /**
     * Obtém o tempo total de uso do dispositivo.
     * 
     * @param startTime Data de início do período
     * @param endTime Data de fim do período
     * @return Tempo total de uso em milissegundos
     */
    suspend fun getTotalUsageTime(startTime: Date, endTime: Date): Long
    
    /**
     * Obtém os aplicativos mais usados no período especificado.
     * 
     * @param startTime Data de início do período
     * @param endTime Data de fim do período
     * @param limit Número máximo de resultados
     * @return Lista de estatísticas de uso ordenada por tempo total
     */
    suspend fun getMostUsedApps(startTime: Date, endTime: Date, limit: Int): List<AppUsageStats>
    
    /**
     * Registra um evento de uso de aplicativo.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param usageTimeMs Tempo de uso em milissegundos
     */
    suspend fun recordAppUsage(packageName: String, usageTimeMs: Long)
}
