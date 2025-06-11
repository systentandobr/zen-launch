package com.zenlauncher.domain.services

import com.zenlauncher.domain.entities.FocusSession
import kotlinx.coroutines.flow.Flow

/**
 * Serviço para gerenciar sessões de foco, timer e notificações.
 */
interface FocusTimerService {
    
    /**
     * Inicia o timer de foco com a sessão especificada.
     */
    suspend fun startTimer(session: FocusSession): Result<Unit>
    
    /**
     * Para o timer de foco atual.
     */
    suspend fun stopTimer(): Result<Unit>
    
    /**
     * Pausa o timer de foco atual.
     */
    suspend fun pauseTimer(): Result<Unit>
    
    /**
     * Resume o timer de foco pausado.
     */
    suspend fun resumeTimer(): Result<Unit>
    
    /**
     * Obtém o tempo restante em segundos.
     */
    fun getRemainingTimeSeconds(): Flow<Int>
    
    /**
     * Verifica se o timer está rodando.
     */
    fun isTimerRunning(): Flow<Boolean>
    
    /**
     * Obtém a sessão ativa atual.
     */
    fun getCurrentSession(): Flow<FocusSession?>
    
    /**
     * Configura notificações de progresso.
     */
    suspend fun setupProgressNotifications(enabled: Boolean)
    
    /**
     * Configura notificação de conclusão.
     */
    suspend fun setupCompletionNotification(enabled: Boolean)
}
