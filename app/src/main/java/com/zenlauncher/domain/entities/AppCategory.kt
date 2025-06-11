package com.zenlauncher.domain.entities

/**
 * Representa uma categoria de aplicativos.
 *
 * @property id Identificador único da categoria
 * @property title Título da categoria para exibição
 * @property apps Lista de aplicativos nesta categoria
 */
data class AppCategory(
    val id: String,
    val title: String,
    val apps: List<AppInfo>
)
