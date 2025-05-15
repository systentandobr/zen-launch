package com.zenlauncher.domain.interfaces

/**
 * Interface para ouvintes de mudanças no estado de carregamento do dispositivo.
 */
interface ChargingStateListener {
    
    /**
     * Chamado quando o dispositivo começa a carregar.
     */
    fun onChargingStarted()
    
    /**
     * Chamado quando o dispositivo para de carregar.
     */
    fun onChargingStopped()
    
    /**
     * Chamado quando o nível da bateria muda.
     * 
     * @param level Nível da bateria (0-100)
     */
    fun onBatteryLevelChanged(level: Int)
}
