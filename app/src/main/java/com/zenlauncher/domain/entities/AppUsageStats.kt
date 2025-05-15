package com.zenlauncher.domain.entities

/**
 * Representa as estatísticas de uso de um aplicativo.
 *
 * @property packageName Nome do pacote do aplicativo
 * @property appName Nome de exibição do aplicativo
 * @property usageTimeToday Tempo de uso hoje em milissegundos
 * @property usageTimeWeek Tempo de uso nos últimos 7 dias em milissegundos
 * @property lastUsed Timestamp da última utilização
 * @property launchCount Número de vezes que o aplicativo foi iniciado
 */
data class AppUsageStats(
    val packageName: String,
    val appName: String,
    val usageTimeToday: Long,
    val usageTimeWeek: Long,
    val lastUsed: Long,
    val launchCount: Long
)
