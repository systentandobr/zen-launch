package com.zenlauncher.data.repositories

import com.zenlauncher.data.datasources.local.AppPreferencesDataSource
import com.zenlauncher.data.datasources.system.AppSystemDataSource
import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.repositories.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de aplicativos.
 */
@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appSystemDataSource: AppSystemDataSource,
    private val appPreferencesDataSource: AppPreferencesDataSource
) : AppRepository {
    
    /**
     * Obtém todos os aplicativos instalados.
     */
    override fun getAllApps(): Flow<List<App>> {
        // Combina as informações de aplicativos instalados com o status de favorito
        return flow { 
            emit(appSystemDataSource.getAllInstalledApps()) 
        }.combine(appPreferencesDataSource.favoriteApps) { apps, favorites ->
            apps.map { app ->
                app.copy(isFavorite = favorites.contains(app.packageName))
            }
        }.flowOn(Dispatchers.IO)
    }
    
    /**
     * Obtém apenas aplicativos favoritos.
     */
    override fun getFavoriteApps(): Flow<List<App>> {
        return getAllApps().map { allApps ->
            allApps.filter { it.isFavorite }
        }
    }
    
    /**
     * Adiciona ou remove um aplicativo dos favoritos.
     */
    override suspend fun setAppFavorite(packageName: String, isFavorite: Boolean) {
        if (isFavorite) {
            appPreferencesDataSource.addFavoriteApp(packageName)
        } else {
            appPreferencesDataSource.removeFavoriteApp(packageName)
        }
    }
    
    /**
     * Obtém informações de um aplicativo específico.
     */
    override suspend fun getAppByPackage(packageName: String): App? = withContext(Dispatchers.IO) {
        if (!appSystemDataSource.isAppInstalled(packageName)) {
            return@withContext null
        }
        
        val allApps = appSystemDataSource.getAllInstalledApps()
        val app = allApps.find { it.packageName == packageName } ?: return@withContext null
        
        return@withContext app.copy(
            isFavorite = appPreferencesDataSource.isAppFavorite(packageName)
        )
    }
    
    /**
     * Lança um aplicativo.
     */
    override suspend fun launchApp(packageName: String): Boolean {
        return appSystemDataSource.launchApp(packageName)
    }
    
    /**
     * Busca aplicativos pelo nome.
     */
    override suspend fun searchApps(query: String): List<App> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            return@withContext emptyList<App>()
        }
        
        val lowerCaseQuery = query.lowercase()
        val allApps = appSystemDataSource.getAllInstalledApps()
        val favoritePackages = appPreferencesDataSource.getFavoriteAppsSync()
        
        return@withContext allApps
            .filter { 
                it.appName.lowercase().contains(lowerCaseQuery) || 
                it.packageName.lowercase().contains(lowerCaseQuery)
            }
            .map { 
                it.copy(isFavorite = favoritePackages.contains(it.packageName))
            }
    }
}
