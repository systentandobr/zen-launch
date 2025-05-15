package com.zenlauncher.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.zenlauncher.domain.interfaces.ChargingStateListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * BroadcastReceiver para detectar quando o dispositivo começa ou para de carregar.
 */
@AndroidEntryPoint
class PowerConnectionReceiver : BroadcastReceiver() {
    
    @Inject
    lateinit var listeners: Set<@JvmSuppressWildcards ChargingStateListener>
    
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                // Dispositivo conectado à energia
                listeners.forEach { it.onChargingStarted() }
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                // Dispositivo desconectado da energia
                listeners.forEach { it.onChargingStopped() }
            }
            Intent.ACTION_BATTERY_CHANGED -> {
                // Verificar estado atual de carregamento
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                 status == BatteryManager.BATTERY_STATUS_FULL
                
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = if (level != -1 && scale != -1) {
                    level * 100 / scale
                } else {
                    -1
                }
                
                if (isCharging) {
                    listeners.forEach { it.onChargingStarted() }
                } else {
                    listeners.forEach { it.onChargingStopped() }
                }
                
                if (batteryPct != -1) {
                    listeners.forEach { it.onBatteryLevelChanged(batteryPct) }
                }
            }
        }
    }
    
    companion object {
        /**
         * Verifica se o dispositivo está atualmente carregando.
         */
        fun isCharging(context: Context): Boolean {
            val intent = context.registerReceiver(null, 
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            
            val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            return status == BatteryManager.BATTERY_STATUS_CHARGING ||
                   status == BatteryManager.BATTERY_STATUS_FULL
        }
        
        /**
         * Obtém o nível atual da bateria (0-100).
         */
        fun getBatteryLevel(context: Context): Int {
            val intent = context.registerReceiver(null, 
                    IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            
            return if (level != -1 && scale != -1) {
                level * 100 / scale
            } else {
                -1
            }
        }
    }
}
