package com.zenlauncher.domain.entities

import java.util.concurrent.TimeUnit

/**
 * Representa uma sessão de uso de um aplicativo
 */
data class AppUsageSession(
    val packageName: String,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var lastWarningTime: Long? = null,
    var isBlocked: Boolean = false
) {
    /**
     * Duração da sessão em minutos
     */
    val durationMinutes: Long
        get() {
            val end = endTime ?: System.currentTimeMillis()
            return TimeUnit.MILLISECONDS.toMinutes(end - startTime)
        }
    
    /**
     * Verifica se deve mostrar aviso de 1 hora
     */
    fun shouldShowWarning(warningTimeMinutes: Long): Boolean {
        if (lastWarningTime != null) return false
        return durationMinutes >= warningTimeMinutes
    }
    
    /**
     * Verifica se deve bloquear (2 horas)
     */
    fun shouldBlock(blockTimeMinutes: Long): Boolean {
        if (isBlocked) return false
        return durationMinutes >= blockTimeMinutes
    }
    
    /**
     * Marca que o aviso foi mostrado
     */
    fun markWarningShown() {
        lastWarningTime = System.currentTimeMillis()
    }
    
    /**
     * Marca como bloqueado
     */
    fun markAsBlocked() {
        isBlocked = true
        endTime = System.currentTimeMillis()
    }
}
