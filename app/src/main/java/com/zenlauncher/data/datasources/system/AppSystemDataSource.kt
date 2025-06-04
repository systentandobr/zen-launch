package com.zenlauncher.data.datasources.system

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.zenlauncher.domain.entities.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados para acessar aplicativos instalados no sistema.
 */
@Singleton
class AppSystemDataSource @Inject constructor(
    private val context: Context
) {
    /**
     * Obtém todos os aplicativos instalados no dispositivo.
     */
    suspend fun getAllInstalledApps(): List<App> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        
        return@withContext packageManager
            .queryIntentActivities(intent, PackageManager.GET_META_DATA)
            .map { resolveInfo ->
                val packageName = resolveInfo.activityInfo.packageName
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                
                App(
                    packageName = packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    isSystemApp = isSystemApp,
                    isFavorite = false, // Será preenchido pelo repositório
                    lastUsed = 0, // Será preenchido pelo repositório
                    icon = packageManager.getApplicationIcon(packageName)
                )
            }
            .sortedBy { it.appName.lowercase() }
    }
    
    /**
     * Lança um aplicativo pelo seu pacote.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @return true se o lançamento foi bem-sucedido
     */
    suspend fun launchApp(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                // Adicionando FLAG_ACTIVITY_NEW_TASK para lançar de um contexto não-Activity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return@withContext true
            }
            return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Verifica se um pacote de aplicativo está instalado.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @return true se o aplicativo está instalado
     */
    suspend fun isAppInstalled(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            context.packageManager.getApplicationInfo(packageName, 0)
            return@withContext true
        } catch (e: PackageManager.NameNotFoundException) {
            return@withContext false
        }
    }
}
