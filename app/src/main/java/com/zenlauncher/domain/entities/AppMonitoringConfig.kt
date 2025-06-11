package com.zenlauncher.domain.entities

/**
 * Configuração de monitoramento para um aplicativo
 */
data class AppMonitoringConfig(
    val packageName: String,
    val isExcludedFromMonitoring: Boolean = false,
    val customWarningTime: Long? = null, // em minutos, null usa o padrão (60 min)
    val customBlockTime: Long? = null // em minutos, null usa o padrão (120 min)
) {
    companion object {
        const val DEFAULT_WARNING_TIME = 60L // 1 hora em minutos
        const val DEFAULT_BLOCK_TIME = 120L // 2 horas em minutos
    }
    
    val warningTimeMinutes: Long
        get() = customWarningTime ?: DEFAULT_WARNING_TIME
        
    val blockTimeMinutes: Long
        get() = customBlockTime ?: DEFAULT_BLOCK_TIME
}
