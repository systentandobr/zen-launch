package com.zenlauncher.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.repositories.AppBlockRepository
import com.zenlauncher.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Serviço em segundo plano para monitorar o uso de aplicativos
 * e aplicar bloqueios quando necessário.
 */
@AndroidEntryPoint
class UsageTrackingService : Service() {
    
    @Inject
    lateinit var appBlockRepository: AppBlockRepository
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var powerManager: PowerManager
    
    private var isMonitoring = false
    private var lastAppPackage: String? = null
    private var lastTimeStamp: Long = 0
    
    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_MONITORING -> startMonitoring()
            ACTION_STOP_MONITORING -> stopMonitoring()
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    /**
     * Inicia o monitoramento de uso de aplicativos.
     */
    private fun startMonitoring() {
        if (isMonitoring) return
        
        val notification = createForegroundNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        
        isMonitoring = true
        
        serviceScope.launch {
            while (isMonitoring) {
                if (powerManager.isInteractive) {
                    checkCurrentApp()
                }
                delay(POLLING_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Para o monitoramento de uso de aplicativos.
     */
    private fun stopMonitoring() {
        isMonitoring = false
        stopForeground(true)
        stopSelf()
    }
    
    /**
     * Verifica qual aplicativo está em uso atualmente
     * e aplica bloqueios se necessário.
     */
    private suspend fun checkCurrentApp() {
        val currentTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(
            currentTime - CHECK_INTERVAL_MS,
            currentTime
        )
        
        var currentAppPackage: String? = null
        val event = UsageEvents.Event()
        
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                currentAppPackage = event.packageName
            }
        }
        
        if (currentAppPackage != null && currentAppPackage != lastAppPackage 
                                      && currentAppPackage != packageName) {
            // Verificar se o aplicativo está bloqueado
            val isBlocked = appBlockRepository.isAppBlocked(currentAppPackage)
            
            if (isBlocked) {
                // Obter detalhes do bloqueio para notificação
                val appBlock = appBlockRepository.getAppBlock(currentAppPackage).first()
                
                if (appBlock != null && appBlock.isActive()) {
                    when (appBlock.blockLevel) {
                        AppBlock.BlockLevel.SOFT -> {
                            // Apenas notifica o usuário
                            showAppBlockedNotification(appBlock)
                        }
                        AppBlock.BlockLevel.MEDIUM, AppBlock.BlockLevel.HARD -> {
                            // Redireciona para a tela inicial do launcher
                            val launcherIntent = Intent(this, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(launcherIntent)
                            
                            // Mostra uma notificação
                            showAppBlockedNotification(appBlock)
                        }
                    }
                }
            }
            
            // Atualizar aplicativo atual
            lastAppPackage = currentAppPackage
            lastTimeStamp = currentTime
        }
    }
    
    /**
     * Cria o canal de notificação para Android O+.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Cria a notificação persistente para o serviço em primeiro plano.
     */
    private fun createForegroundNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Zen Launcher")
        .setContentText("Monitorando o uso de aplicativos")
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Substituir pelo ícone adequado
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()
    
    /**
     * Mostra uma notificação quando um aplicativo bloqueado é aberto.
     */
    private fun showAppBlockedNotification(appBlock: AppBlock) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_time_limit, appBlock.packageName))
            .setContentText(getString(R.string.notification_take_a_break))
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Substituir pelo ícone adequado
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                0,
                getString(R.string.notification_add_more_time),
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, MainActivity::class.java).apply {
                        action = ACTION_ADD_MORE_TIME
                        putExtra(EXTRA_PACKAGE_NAME, appBlock.packageName)
                    },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(BLOCK_NOTIFICATION_ID, notification)
    }
    
    companion object {
        private const val CHANNEL_ID = "zen_launcher_service_channel"
        private const val NOTIFICATION_ID = 1001
        private const val BLOCK_NOTIFICATION_ID = 1002
        
        private const val POLLING_INTERVAL_MS = 1000L // 1 segundo
        private const val CHECK_INTERVAL_MS = 5000L // 5 segundos
        
        const val ACTION_START_MONITORING = "com.zenlauncher.action.START_MONITORING"
        const val ACTION_STOP_MONITORING = "com.zenlauncher.action.STOP_MONITORING"
        const val ACTION_ADD_MORE_TIME = "com.zenlauncher.action.ADD_MORE_TIME"
        const val EXTRA_PACKAGE_NAME = "package_name"
        
        /**
         * Cria uma Intent para iniciar o serviço de monitoramento.
         */
        fun getStartIntent(context: Context): Intent {
            return Intent(context, UsageTrackingService::class.java).apply {
                action = ACTION_START_MONITORING
            }
        }
        
        /**
         * Cria uma Intent para parar o serviço de monitoramento.
         */
        fun getStopIntent(context: Context): Intent {
            return Intent(context, UsageTrackingService::class.java).apply {
                action = ACTION_STOP_MONITORING
            }
        }
    }
}
