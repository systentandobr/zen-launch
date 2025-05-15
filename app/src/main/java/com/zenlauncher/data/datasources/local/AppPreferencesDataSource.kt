package com.zenlauncher.data.datasources.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fonte de dados para gerenciar preferências do aplicativo usando SharedPreferences.
 */
@Singleton
class AppPreferencesDataSource @Inject constructor(
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )
    
    private val _favoriteApps = MutableStateFlow<Set<String>>(emptySet())
    val favoriteApps: Flow<Set<String>> = _favoriteApps.asStateFlow()
    
    init {
        // Carregar preferências iniciais
        loadFavoriteApps()
    }
    
    /**
     * Carrega aplicativos favoritos das preferências.
     */
    private fun loadFavoriteApps() {
        val favorites = prefs.getStringSet(KEY_FAVORITE_APPS, emptySet()) ?: emptySet()
        _favoriteApps.value = favorites
    }
    
    /**
     * Adiciona um aplicativo aos favoritos.
     * 
     * @param packageName Nome do pacote do aplicativo
     */
    suspend fun addFavoriteApp(packageName: String) = withContext(Dispatchers.IO) {
        val currentFavorites = _favoriteApps.value.toMutableSet()
        currentFavorites.add(packageName)
        
        prefs.edit {
            putStringSet(KEY_FAVORITE_APPS, currentFavorites)
            apply()
        }
        
        _favoriteApps.value = currentFavorites
    }
    
    /**
     * Remove um aplicativo dos favoritos.
     * 
     * @param packageName Nome do pacote do aplicativo
     */
    suspend fun removeFavoriteApp(packageName: String) = withContext(Dispatchers.IO) {
        val currentFavorites = _favoriteApps.value.toMutableSet()
        currentFavorites.remove(packageName)
        
        prefs.edit {
            putStringSet(KEY_FAVORITE_APPS, currentFavorites)
            apply()
        }
        
        _favoriteApps.value = currentFavorites
    }
    
    /**
     * Verifica se um aplicativo é favorito.
     * 
     * @param packageName Nome do pacote do aplicativo
     * @return true se o aplicativo é favorito
     */
    fun isAppFavorite(packageName: String): Boolean {
        return _favoriteApps.value.contains(packageName)
    }
    
    /**
     * Obtém o conjunto atual de aplicativos favoritos (versão síncrona).
     * 
     * @return Conjunto de nomes de pacotes de aplicativos favoritos
     */
    fun getFavoriteAppsSync(): Set<String> {
        return _favoriteApps.value.toSet()
    }
    
    companion object {
        private const val PREFERENCES_NAME = "zen_launcher_preferences"
        private const val KEY_FAVORITE_APPS = "favorite_apps"
    }
}
