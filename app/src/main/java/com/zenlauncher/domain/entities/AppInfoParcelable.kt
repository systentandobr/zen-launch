package com.zenlauncher.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Versão parcelável de AppInfo para passar entre fragments/activities
 * Não inclui o Drawable pois este não é serializável
 */
@Parcelize
data class AppInfoParcelable(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean = false,
    val isFavorite: Boolean = false,
    val hasTimeReminder: Boolean = false,
    val isVisibleOnHomeScreen: Boolean = true,
    val customLabel: String? = null,
    val isHidden: Boolean = false,
    val folderId: String? = null
) : Parcelable {
    
    /**
     * Retorna o nome de exibição do app (customLabel se definido, senão label padrão)
     */
    val displayLabel: String
        get() = customLabel ?: label
    
    companion object {
        /**
         * Converte AppInfo para AppInfoParcelable (sem o drawable)
         */
        fun fromAppInfo(appInfo: AppInfo): AppInfoParcelable {
            return AppInfoParcelable(
                packageName = appInfo.packageName,
                label = appInfo.label,
                isSystemApp = appInfo.isSystemApp,
                isFavorite = appInfo.isFavorite,
                hasTimeReminder = appInfo.hasTimeReminder,
                isVisibleOnHomeScreen = appInfo.isVisibleOnHomeScreen,
                customLabel = appInfo.customLabel,
                isHidden = appInfo.isHidden,
                folderId = appInfo.folderId
            )
        }
    }
}
