package com.zenlauncher.domain.entities

import java.util.Date

/**
 * Representa uma configuração de bloqueio para um aplicativo.
 *
 * @property packageName Nome do pacote do aplicativo a ser bloqueado
 * @property blockedUntil Data até quando o aplicativo deve permanecer bloqueado
 * @property blockLevel Nível de rigor do bloqueio
 */
data class AppBlock(
    val packageName: String,
    val blockedUntil: Date,
    val blockLevel: BlockLevel
) {
    /**
     * Nível de rigor do bloqueio.
     */
    enum class BlockLevel {
        /**
         * Apenas notifica o usuário, mas permite o uso
         */
        SOFT,
        
        /**
         * Mostra uma tela de confirmação antes de abrir
         */
        MEDIUM,
        
        /**
         * Impede completamente a abertura do aplicativo
         */
        HARD
    }
    
    /**
     * Verifica se o bloqueio está ativo atualmente.
     */
    fun isActive(): Boolean {
        return Date().before(blockedUntil)
    }
    
    /**
     * Retorna o tempo restante de bloqueio em milissegundos.
     */
    fun remainingTimeMs(): Long {
        val now = Date()
        return if (now.before(blockedUntil)) {
            blockedUntil.time - now.time
        } else {
            0
        }
    }
}
