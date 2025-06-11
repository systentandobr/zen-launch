package com.zenlauncher.domain.repositories

import com.zenlauncher.domain.entities.AppMonitoringConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para gerenciar configurações de monitoramento de apps
 */
interface AppMonitoringRepository {
    
    /**
     * Obtém a configuração de monitoramento para um app específico
     */
    fun getMonitoringConfig(packageName: String): Flow<AppMonitoringConfig?>
    
    /**
     * Obtém todas as configurações de monitoramento
     */
    fun getAllMonitoringConfigs(): Flow<List<AppMonitoringConfig>>
    
    /**
     * Salva ou atualiza uma configuração de monitoramento
     */
    suspend fun saveMonitoringConfig(config: AppMonitoringConfig)
    
    /**
     * Remove uma configuração de monitoramento
     */
    suspend fun deleteMonitoringConfig(packageName: String)
    
    /**
     * Define se um app está excluído do monitoramento
     */
    suspend fun setExcludedFromMonitoring(packageName: String, excluded: Boolean)
    
    /**
     * Define tempo customizado de aviso
     */
    suspend fun setCustomWarningTime(packageName: String, minutes: Long?)
    
    /**
     * Define tempo customizado de bloqueio
     */
    suspend fun setCustomBlockTime(packageName: String, minutes: Long?)
}
