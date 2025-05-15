package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.AppUsageStats
import com.zenlauncher.domain.repositories.UsageStatsRepository
import javax.inject.Inject
import java.util.Calendar
import java.util.Date

/**
 * Caso de uso para obter os aplicativos mais usados.
 */
class GetMostUsedAppsUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    /**
     * Executa o caso de uso para obter os aplicativos mais usados em um período.
     * 
     * @param days Número de dias para calcular (contando de hoje para trás)
     * @param limit Número máximo de aplicativos a retornar
     * @return Lista de estatísticas de uso ordenada por tempo de uso (decrescente)
     */
    suspend operator fun invoke(days: Int = 7, limit: Int = 5): List<AppUsageStats> {
        val endTime = Date()
        val startTime = Calendar.getInstance().apply {
            time = endTime
            add(Calendar.DAY_OF_YEAR, -days)
        }.time
        
        return usageStatsRepository.getMostUsedApps(startTime, endTime, limit)
    }
}
