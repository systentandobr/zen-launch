package com.zenlauncher.domain.repositories

import com.zenlauncher.domain.entities.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para gerenciar sessões de foco.
 */
interface FocusSessionRepository {
    
    /**
     * Salva uma nova sessão de foco.
     */
    suspend fun saveFocusSession(session: FocusSession): Result<FocusSession>
    
    /**
     * Atualiza uma sessão de foco existente.
     */
    suspend fun updateFocusSession(session: FocusSession): Result<FocusSession>
    
    /**
     * Obtém a sessão de foco ativa atual, se houver.
     */
    suspend fun getActiveFocusSession(): FocusSession?
    
    /**
     * Obtém todas as sessões de foco do usuário.
     */
    fun getAllFocusSessions(): Flow<List<FocusSession>>
    
    /**
     * Obtém sessões de foco em um período específico.
     */
    suspend fun getFocusSessionsInPeriod(
        startTime: Long, 
        endTime: Long
    ): List<FocusSession>
    
    /**
     * Deleta uma sessão de foco.
     */
    suspend fun deleteFocusSession(sessionId: String): Result<Unit>
    
    /**
     * Obtém estatísticas de sessões de foco.
     */
    suspend fun getFocusSessionStats(days: Int): FocusSessionStats
}

/**
 * Estatísticas de sessões de foco.
 */
data class FocusSessionStats(
    val totalSessions: Int,
    val completedSessions: Int,
    val totalFocusTimeMinutes: Int,
    val averageSessionDurationMinutes: Double,
    val successRate: Double
)
