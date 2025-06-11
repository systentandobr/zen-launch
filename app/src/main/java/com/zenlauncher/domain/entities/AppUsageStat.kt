package com.zenlauncher.domain.entities

/**
 * Representa estatísticas de uso de um aplicativo.
 *
 * @property packageName Nome do pacote do aplicativo
 * @property totalTimeUsed Tempo total de uso em milissegundos
 * @property launchCount Número de vezes que o aplicativo foi iniciado
 * @property lastTimeUsed Timestamp da última vez que o aplicativo foi usado
 */
data class AppUsageStat(
    val packageName: String,
    val totalTimeUsed: Long,
    val launchCount: Int,
    val lastTimeUsed: Long
)
