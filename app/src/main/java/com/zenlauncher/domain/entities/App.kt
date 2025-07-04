package com.zenlauncher.domain.entities

import android.graphics.drawable.Drawable

/**
 * Representa uma aplicação instalada no dispositivo.
 *
 * @property packageName Nome do pacote do aplicativo
 * @property appName Nome de exibição do aplicativo
 * @property isSystemApp Indica se é um aplicativo do sistema
 * @property isFavorite Indica se o aplicativo é um favorito
 * @property lastUsed Timestamp da última utilização do aplicativo
 * @property icon Ícone do aplicativo
 */
data class App(
    val packageName: String,
    val appName: String,
    val isSystemApp: Boolean,
    val isFavorite: Boolean = false,
    val lastUsed: Long = 0,
    val icon: Drawable? = null
)
