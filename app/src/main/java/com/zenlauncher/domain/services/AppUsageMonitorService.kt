package com.zenlauncher.domain.services

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.entities.AppMonitoringConfig
import com.zenlauncher.domain.entities.AppUsageSession
import com.zenlauncher.domain.repositories.AppBlockRepository
import com.zenlauncher.domain.repositories.AppMonitoringRepository
import com.zenlauncher.presentation.MainActivity
import com.zenlauncher.presentation.focus.blockscreen.AppBlockScreenActivity
import com.zenlauncher.presentation.focus.UsageWarningActivity
import com.zenlauncher.presentation.focus.UsageBlockActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

/**
 * Serviço aprimorado para monitorar o uso de aplicativos
 * Monitora tanto apps bloqueados quanto tempo de uso
 */
@AndroidEntryPoint
class AppUsageMonitorService : Service() {

    @Inject
    lateinit var blockRepository: AppBlockRepository
    
    @Inject
    lateinit var monitoringRepository: AppMonitoringRepository

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    
    private var usageStatsManager: UsageStatsManager? = null
    private var lastCheckedPackage: String? = null
    private val activeSessions = mutableMapOf<String, AppUsageSession>()
    
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "app_monitor_channel"
        private const val NOTIFICATION_ID = 1001
        private const val CHECK_INTERVAL = 3000L // 3 segundos (otimizado)
        
        fun start(context: Context) {
            val intent = Intent(context, AppUsageMonitorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            context.stopService(Intent(context, AppUsageMonitorService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        handler.removeCallbacksAndMessages(null)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Monitoramento de Apps",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitora o uso de aplicativos para seu bem-estar"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("ZenLauncher")
            .setContentText("Monitorando uso de aplicativos")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startMonitoring() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkCurrentApp()
                handler.postDelayed(this, CHECK_INTERVAL)
            }
        }, CHECK_INTERVAL)
    }

    private fun checkCurrentApp() {
        val currentPackage = getCurrentForegroundApp() ?: return
        
        // Se mudou de app, finaliza a sessão anterior
        if (currentPackage != lastCheckedPackage) {
            lastCheckedPackage?.let { pkg ->
                activeSessions[pkg]?.let { session ->
                    session.endTime = System.currentTimeMillis()
                    Timber.d("Sessão finalizada: $pkg, duração: ${session.durationMinutes} min")
                }
            }
            lastCheckedPackage = currentPackage
        }
        
        // Ignora o próprio launcher
        if (currentPackage == packageName) return
        
        serviceScope.launch {
            // Verifica se o app está bloqueado
            val blockedApps = blockRepository.getActiveBlocks().first()
            val isBlocked = blockedApps.any { it.packageName == currentPackage }
            
            if (isBlocked) {
                handleBlockedApp(currentPackage, blockedApps.first { it.packageName == currentPackage })
            } else {
                // Monitora tempo de uso
                monitorAppUsage(currentPackage)
            }
        }
    }

    private suspend fun handleBlockedApp(packageName: String, block: AppBlock) {
        withContext(Dispatchers.Main) {
            val intent = Intent(this@AppUsageMonitorService, AppBlockScreenActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("PACKAGE_NAME", packageName)
                putExtra("BLOCK_LEVEL", block.blockLevel.name)
                putExtra("REMAINING_TIME", block.remainingTimeMs())
            }
            startActivity(intent)
        }
    }

    private suspend fun monitorAppUsage(packageName: String) {
        // Obtém ou cria sessão
        val session = activeSessions.getOrPut(packageName) {
            AppUsageSession(packageName)
        }
        
        // Obtém configuração de monitoramento
        val config = monitoringRepository.getMonitoringConfig(packageName).first()
            ?: AppMonitoringConfig(packageName)
        
        // Se excluído do monitoramento, ignora
        if (config.isExcludedFromMonitoring) return
        
        // Verifica se deve mostrar aviso (1 hora)
        if (session.shouldShowWarning(config.warningTimeMinutes)) {
            showUsageWarningDialog(packageName, session, config)
            session.markWarningShown()
        }
        
        // Verifica se deve bloquear (2 horas)
        if (session.shouldBlock(config.blockTimeMinutes)) {
            showUsageBlockDialog(packageName, session)
            session.markAsBlocked()
            
            // Cria um bloqueio temporário
            val currentTime = System.currentTimeMillis()
            val blockedUntilTime = Date(currentTime + (1 * 60 * 60 * 1000)) // 1 hora
            
            blockRepository.blockApp(
                packageName = packageName,
                blockedUntil = blockedUntilTime,
                blockLevel = AppBlock.BlockLevel.MEDIUM
            )
        }
    }

    private suspend fun showUsageWarningDialog(
        packageName: String, 
        session: AppUsageSession,
        config: AppMonitoringConfig
    ) {
        withContext(Dispatchers.Main) {
            val intent = Intent(this@AppUsageMonitorService, UsageWarningActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("PACKAGE_NAME", packageName)
                putExtra("USAGE_MINUTES", session.durationMinutes)
                putExtra("WARNING_TIME", config.warningTimeMinutes)
            }
            startActivity(intent)
        }
    }

    private suspend fun showUsageBlockDialog(packageName: String, session: AppUsageSession) {
        withContext(Dispatchers.Main) {
            val intent = Intent(this@AppUsageMonitorService, UsageBlockActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("PACKAGE_NAME", packageName)
                putExtra("USAGE_MINUTES", session.durationMinutes)
            }
            startActivity(intent)
        }
    }

    private fun getCurrentForegroundApp(): String? {
        val currentTime = System.currentTimeMillis()
        val usageEvents = usageStatsManager?.queryEvents(currentTime - 1000, currentTime)
        
        var lastForegroundPackage: String? = null
        val event = UsageEvents.Event()
        
        while (usageEvents?.hasNextEvent() == true) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastForegroundPackage = event.packageName
            }
        }
        
        return lastForegroundPackage
    }
}
