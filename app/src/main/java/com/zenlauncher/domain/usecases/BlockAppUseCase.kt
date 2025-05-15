package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.repositories.AppBlockRepository
import javax.inject.Inject
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Caso de uso para bloquear um aplicativo.
 */
class BlockAppUseCase @Inject constructor(
    private val appBlockRepository: AppBlockRepository
) {
    /**
     * Executa o caso de uso para criar ou atualizar um bloqueio para um aplicativo.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param durationHours Duração do bloqueio em horas
     * @param blockLevel Nível de rigor do bloqueio (padrão: MEDIUM)
     * @return true se o bloqueio foi criado/atualizado com sucesso
     */
    suspend operator fun invoke(
        packageName: String,
        durationHours: Float,
        blockLevel: AppBlock.BlockLevel = AppBlock.BlockLevel.MEDIUM
    ): Boolean {
        // Calcula data de término do bloqueio
        val durationMs = (durationHours * 60 * 60 * 1000).toLong()
        val blockedUntil = Date(System.currentTimeMillis() + durationMs)
        
        return appBlockRepository.blockApp(packageName, blockedUntil, blockLevel)
    }
    
    /**
     * Sobrecarga para bloquear um aplicativo com duração específica em minutos.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param durationMinutes Duração do bloqueio em minutos
     * @param blockLevel Nível de rigor do bloqueio (padrão: MEDIUM)
     * @return true se o bloqueio foi criado/atualizado com sucesso
     */
    suspend fun blockForMinutes(
        packageName: String,
        durationMinutes: Int,
        blockLevel: AppBlock.BlockLevel = AppBlock.BlockLevel.MEDIUM
    ): Boolean {
        val hoursEquivalent = durationMinutes / 60f
        return invoke(packageName, hoursEquivalent, blockLevel)
    }
    
    /**
     * Sobrecarga para bloquear um aplicativo por um dia inteiro.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @param blockLevel Nível de rigor do bloqueio (padrão: MEDIUM)
     * @return true se o bloqueio foi criado/atualizado com sucesso
     */
    suspend fun blockForDay(
        packageName: String,
        blockLevel: AppBlock.BlockLevel = AppBlock.BlockLevel.MEDIUM
    ): Boolean {
        return invoke(packageName, 24f, blockLevel)
    }
}
