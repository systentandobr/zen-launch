package com.zenlauncher.presentation.ranking.model

/**
 * Representa uma entrada no ranking.
 */
data class RankingEntry(
    val position: Int,
    val username: String,
    val reduction: String,
    val points: Int,
    val streakDays: Int,
    val isCurrentUser: Boolean = false
)

/**
 * Períodos disponíveis para o ranking.
 */
enum class RankingPeriod {
    WEEKLY,
    MONTHLY,
    FRIENDS
}
