package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.AppUsageStat
import com.zenlauncher.domain.repositories.UsageStatsRepository
import javax.inject.Inject

/**
 * Caso de uso para obter estatísticas de uso de aplicativos.
 */
class GetAppUsageStatsUseCase @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) {
    /**
     * Executa o caso de uso para obter estatísticas de uso.
     * 
     * @param daysBack Número de dias para olhar para trás
     * @param limit Número máximo de resultados
     * @return Lista de estatísticas de uso de aplicativos
     */
    suspend operator fun invoke(daysBack: Int = 7, limit: Int = 10): List<AppUsageStat> {
        return usageStatsRepository.getAppUsageStats(daysBack, limit)
    }
}
