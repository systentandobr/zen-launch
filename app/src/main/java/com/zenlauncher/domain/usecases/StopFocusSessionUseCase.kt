package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.FocusSession
import com.zenlauncher.domain.repositories.FocusSessionRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Use case para parar uma sessão de foco.
 */
class StopFocusSessionUseCase @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository
) {
    
    suspend operator fun invoke(): Result<FocusSession?> {
        val activeSession = focusSessionRepository.getActiveFocusSession()
            ?: return Result.success(null)
        
        val endTime = LocalDateTime.now()
        val actualDurationMinutes = ChronoUnit.MINUTES.between(
            activeSession.startTime, 
            endTime
        ).toInt()
        
        val updatedSession = activeSession.copy(
            endTime = endTime,
            actualDurationMinutes = actualDurationMinutes,
            isCompleted = actualDurationMinutes >= activeSession.plannedDurationMinutes
        )
        
        return focusSessionRepository.updateFocusSession(updatedSession)
    }
}
