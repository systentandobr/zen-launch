package com.zenlauncher.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.zenlauncher.domain.entities.AppMonitoringConfig
import com.zenlauncher.domain.repositories.AppMonitoringRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de configurações de monitoramento de apps.
 * Utiliza DataStore para persistência das configurações.
 */
@Singleton
class AppMonitoringRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : AppMonitoringRepository {
    
    companion object {
        private const val EXCLUDED_PREFIX = "excluded_"
        private const val WARNING_TIME_PREFIX = "warning_time_"
        private const val BLOCK_TIME_PREFIX = "block_time_"
    }
    
    /**
     * Obtém a configuração de monitoramento para um app específico
     */
    override fun getMonitoringConfig(packageName: String): Flow<AppMonitoringConfig?> {
        return dataStore.data.map { preferences ->
            val isExcluded = preferences[booleanPreferencesKey("$EXCLUDED_PREFIX$packageName")] ?: false
            val customWarningTime = preferences[longPreferencesKey("$WARNING_TIME_PREFIX$packageName")]
            val customBlockTime = preferences[longPreferencesKey("$BLOCK_TIME_PREFIX$packageName")]
            
            // Se não há configurações personalizadas e não está excluído, retorna null (usa padrão)
            if (!isExcluded && customWarningTime == null && customBlockTime == null) {
                null
            } else {
                AppMonitoringConfig(
                    packageName = packageName,
                    isExcludedFromMonitoring = isExcluded,
                    customWarningTime = customWarningTime,
                    customBlockTime = customBlockTime
                )
            }
        }
    }
    
    /**
     * Obtém todas as configurações de monitoramento
     */
    override fun getAllMonitoringConfigs(): Flow<List<AppMonitoringConfig>> {
        return dataStore.data.map { preferences ->
            val configs = mutableListOf<AppMonitoringConfig>()
            val processedPackages = mutableSetOf<String>()
            
            // Processar todas as preferências para extrair packageNames únicos
            preferences.asMap().forEach { (key, _) ->
                val keyString = key.name
                val packageName = when {
                    keyString.startsWith(EXCLUDED_PREFIX) -> 
                        keyString.removePrefix(EXCLUDED_PREFIX)
                    keyString.startsWith(WARNING_TIME_PREFIX) -> 
                        keyString.removePrefix(WARNING_TIME_PREFIX)
                    keyString.startsWith(BLOCK_TIME_PREFIX) -> 
                        keyString.removePrefix(BLOCK_TIME_PREFIX)
                    else -> null
                }
                
                if (packageName != null && packageName !in processedPackages) {
                    processedPackages.add(packageName)
                    
                    val isExcluded = preferences[booleanPreferencesKey("$EXCLUDED_PREFIX$packageName")] ?: false
                    val customWarningTime = preferences[longPreferencesKey("$WARNING_TIME_PREFIX$packageName")]
                    val customBlockTime = preferences[longPreferencesKey("$BLOCK_TIME_PREFIX$packageName")]
                    
                    configs.add(AppMonitoringConfig(
                        packageName = packageName,
                        isExcludedFromMonitoring = isExcluded,
                        customWarningTime = customWarningTime,
                        customBlockTime = customBlockTime
                    ))
                }
            }
            
            configs
        }
    }
    
    /**
     * Salva ou atualiza uma configuração de monitoramento
     */
    override suspend fun saveMonitoringConfig(config: AppMonitoringConfig) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("$EXCLUDED_PREFIX${config.packageName}")] = 
                config.isExcludedFromMonitoring
                
            config.customWarningTime?.let { warningTime ->
                preferences[longPreferencesKey("$WARNING_TIME_PREFIX${config.packageName}")] = warningTime
            } ?: run {
                preferences.remove(longPreferencesKey("$WARNING_TIME_PREFIX${config.packageName}"))
            }
            
            config.customBlockTime?.let { blockTime ->
                preferences[longPreferencesKey("$BLOCK_TIME_PREFIX${config.packageName}")] = blockTime
            } ?: run {
                preferences.remove(longPreferencesKey("$BLOCK_TIME_PREFIX${config.packageName}"))
            }
        }
    }
    
    /**
     * Remove uma configuração de monitoramento
     */
    override suspend fun deleteMonitoringConfig(packageName: String) {
        dataStore.edit { preferences ->
            preferences.remove(booleanPreferencesKey("$EXCLUDED_PREFIX$packageName"))
            preferences.remove(longPreferencesKey("$WARNING_TIME_PREFIX$packageName"))
            preferences.remove(longPreferencesKey("$BLOCK_TIME_PREFIX$packageName"))
        }
    }
    
    /**
     * Define se um app está excluído do monitoramento
     */
    override suspend fun setExcludedFromMonitoring(packageName: String, excluded: Boolean) {
        dataStore.edit { preferences ->
            if (excluded) {
                preferences[booleanPreferencesKey("$EXCLUDED_PREFIX$packageName")] = true
            } else {
                preferences.remove(booleanPreferencesKey("$EXCLUDED_PREFIX$packageName"))
            }
        }
    }
    
    /**
     * Define tempo customizado de aviso
     */
    override suspend fun setCustomWarningTime(packageName: String, minutes: Long?) {
        dataStore.edit { preferences ->
            if (minutes != null) {
                preferences[longPreferencesKey("$WARNING_TIME_PREFIX$packageName")] = minutes
            } else {
                preferences.remove(longPreferencesKey("$WARNING_TIME_PREFIX$packageName"))
            }
        }
    }
    
    /**
     * Define tempo customizado de bloqueio
     */
    override suspend fun setCustomBlockTime(packageName: String, minutes: Long?) {
        dataStore.edit { preferences ->
            if (minutes != null) {
                preferences[longPreferencesKey("$BLOCK_TIME_PREFIX$packageName")] = minutes
            } else {
                preferences.remove(longPreferencesKey("$BLOCK_TIME_PREFIX$packageName"))
            }
        }
    }
}
