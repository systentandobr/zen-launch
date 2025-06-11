package com.zenlauncher.domain.entities

import android.graphics.drawable.Drawable

/**
 * Representa informações básicas sobre um aplicativo instalado.
 *
 * @property packageName Nome do pacote do aplicativo
 * @property label Nome para exibição do aplicativo
 * @property icon Ícone do aplicativo
 * @property isSystemApp Indica se é um aplicativo de sistema
 * @property isFavorite Indica se o aplicativo está marcado como favorito
 * @property hasTimeReminder Indica se o aplicativo tem lembrete de tempo ativo
 * @property isVisibleOnHomeScreen Indica se o botão do app é visível na tela inicial
 * @property customLabel Nome personalizado do aplicativo (se renomeado)
 * @property isHidden Indica se o aplicativo está oculto
 * @property folderId ID da pasta onde o app está organizado (null se não estiver em pasta)
 */
data class AppInfo(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val isSystemApp: Boolean = false,
    val isFavorite: Boolean = false,
    val hasTimeReminder: Boolean = false,
    val isVisibleOnHomeScreen: Boolean = true,
    val customLabel: String? = null,
    val isHidden: Boolean = false,
    val folderId: String? = null,
    val isBlocked: Boolean = false
) {
    /**
     * Retorna o nome de exibição do app (customLabel se definido, senão label padrão)
     */
    val displayLabel: String
        get() = customLabel ?: label
}
