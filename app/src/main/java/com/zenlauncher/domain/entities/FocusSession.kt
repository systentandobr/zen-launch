package com.zenlauncher.domain.entities

import java.time.LocalDateTime

/**
 * Representa uma sessão de modo foco.
 */
data class FocusSession(
    val id: String,
    val startTime: LocalDateTime,
    val plannedDurationMinutes: Int,
    val actualDurationMinutes: Int? = null,
    val endTime: LocalDateTime? = null,
    val isCompleted: Boolean = false,
    val blockedApps: List<String> = emptyList(),
    val sessionType: FocusSessionType = FocusSessionType.DEEP_FOCUS
)

/**
 * Tipos de sessão de foco.
 */
enum class FocusSessionType {
    DEEP_FOCUS,
    POMODORO,
    STUDY,
    MEDITATION,
    WORK
}

/**
 * Estado atual de uma sessão de foco.
 */
sealed class FocusSessionState {
    object Idle : FocusSessionState()
    data class Running(
        val session: FocusSession,
        val remainingTimeMinutes: Int,
        val remainingTimeSeconds: Int
    ) : FocusSessionState()
    data class Paused(
        val session: FocusSession,
        val remainingTimeMinutes: Int,
        val remainingTimeSeconds: Int
    ) : FocusSessionState()
    data class Completed(val session: FocusSession) : FocusSessionState()
}
