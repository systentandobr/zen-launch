package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.FocusSession
import com.zenlauncher.domain.entities.FocusSessionType
import com.zenlauncher.domain.repositories.FocusSessionRepository
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Use case para iniciar uma sessão de foco.
 */
class StartFocusSessionUseCase @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository
) {
    
    suspend operator fun invoke(
        durationMinutes: Int,
        blockedApps: List<String> = emptyList(),
        sessionType: FocusSessionType = FocusSessionType.DEEP_FOCUS
    ): Result<FocusSession> {
        // Verificar se já existe uma sessão ativa
        val activeSession = focusSessionRepository.getActiveFocusSession()
        if (activeSession != null) {
            return Result.failure(IllegalStateException("Já existe uma sessão de foco ativa"))
        }
        
        // Criar nova sessão
        val session = FocusSession(
            id = UUID.randomUUID().toString(),
            startTime = LocalDateTime.now(),
            plannedDurationMinutes = durationMinutes,
            blockedApps = blockedApps,
            sessionType = sessionType
        )
        
        return focusSessionRepository.saveFocusSession(session)
    }
}
