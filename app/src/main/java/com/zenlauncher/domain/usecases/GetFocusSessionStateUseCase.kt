package com.zenlauncher.domain.usecases

import com.zenlauncher.domain.entities.FocusSession
import com.zenlauncher.domain.entities.FocusSessionState
import com.zenlauncher.domain.repositories.FocusSessionRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Use case para obter o estado atual da sessão de foco com timer em tempo real.
 */
class GetFocusSessionStateUseCase @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository
) {
    
    operator fun invoke(): Flow<FocusSessionState> = flow {
        while (true) {
            val activeSession = focusSessionRepository.getActiveFocusSession()
            
            if (activeSession == null) {
                emit(FocusSessionState.Idle)
            } else {
                val now = LocalDateTime.now()
                val elapsedMinutes = ChronoUnit.MINUTES.between(activeSession.startTime, now).toInt()
                val elapsedSeconds = ChronoUnit.SECONDS.between(activeSession.startTime, now).toInt()
                
                val remainingTotalSeconds = (activeSession.plannedDurationMinutes * 60) - elapsedSeconds
                
                if (remainingTotalSeconds <= 0) {
                    // Sessão concluída
                    val completedSession = activeSession.copy(
                        endTime = now,
                        actualDurationMinutes = activeSession.plannedDurationMinutes,
                        isCompleted = true
                    )
                    focusSessionRepository.updateFocusSession(completedSession)
                    emit(FocusSessionState.Completed(completedSession))
                } else {
                    // Sessão ainda rodando
                    val remainingMinutes = remainingTotalSeconds / 60
                    val remainingSeconds = remainingTotalSeconds % 60
                    
                    emit(FocusSessionState.Running(
                        session = activeSession,
                        remainingTimeMinutes = remainingMinutes,
                        remainingTimeSeconds = remainingSeconds
                    ))
                }
            }
            
            delay(1000) // Atualizar a cada segundo
        }
    }
}
