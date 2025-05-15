package com.zenlauncher.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.zenlauncher.data.services.UsageTrackingService
import dagger.hilt.android.AndroidEntryPoint

/**
 * Receiver para iniciar o serviço de monitoramento após a reinicialização do dispositivo.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Iniciar o serviço de monitoramento
            val serviceIntent = UsageTrackingService.getStartIntent(context)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
