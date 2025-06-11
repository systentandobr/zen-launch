package com.zenlauncher.domain.usecases

import com.zenlauncher.data.managers.AppUsageMonitorManager
import com.zenlauncher.domain.entities.AppMonitoringConfig
import com.zenlauncher.domain.repositories.AppMonitoringRepository
import javax.inject.Inject

/**
 * Use case para gerenciar o monitoramento de uso de aplicativos.
 */
class ManageAppMonitoringUseCase @Inject constructor(
    private val appMonitoringRepository: AppMonitoringRepository,
    private val appUsageMonitorManager: AppUsageMonitorManager
) {
    
    /**
     * Inicia o monitoramento de uso de apps.
     */
    fun startMonitoring() {
        appUsageMonitorManager.startMonitoring()
    }
    
    /**
     * Para o monitoramento de uso de apps.
     */
    fun stopMonitoring() {
        appUsageMonitorManager.stopMonitoring()
    }
    
    /**
     * Verifica se o monitoramento está ativo.
     */
    fun isMonitoringActive(): Boolean {
        return appUsageMonitorManager.isMonitoringActive()
    }
    
    /**
     * Força uma verificação imediata.
     */
    fun triggerImmediateCheck() {
        appUsageMonitorManager.triggerImmediateCheck()
    }
    
    /**
     * Exclui um app do monitoramento.
     */
    suspend fun excludeAppFromMonitoring(packageName: String) {
        appMonitoringRepository.setExcludedFromMonitoring(packageName, true)
    }
    
    /**
     * Inclui um app no monitoramento.
     */
    suspend fun includeAppInMonitoring(packageName: String) {
        appMonitoringRepository.setExcludedFromMonitoring(packageName, false)
    }
    
    /**
     * Define tempo customizado de aviso para um app.
     */
    suspend fun setCustomWarningTime(packageName: String, minutes: Long?) {
        appMonitoringRepository.setCustomWarningTime(packageName, minutes)
    }
    
    /**
     * Define tempo customizado de bloqueio para um app.
     */
    suspend fun setCustomBlockTime(packageName: String, minutes: Long?) {
        appMonitoringRepository.setCustomBlockTime(packageName, minutes)
    }
    
    /**
     * Salva uma configuração completa de monitoramento.
     */
    suspend fun saveMonitoringConfig(config: AppMonitoringConfig) {
        appMonitoringRepository.saveMonitoringConfig(config)
        // Trigger uma verificação imediata para aplicar as novas configurações
        appUsageMonitorManager.triggerImmediateCheck()
    }
}
