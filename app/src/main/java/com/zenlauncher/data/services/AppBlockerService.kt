package com.zenlauncher.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zenlauncher.R
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.repositories.AppBlockRepository
import com.zenlauncher.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

/**
 * Serviço para monitorar e gerenciar bloqueios de aplicativos.
 * 
 * Este serviço roda em segundo plano e é responsável por:
 * 1. Monitorar o lançamento de aplicativos
 * 2. Interceptar tentativas de abrir aplicativos bloqueados
 * 3. Mostrar notificações e interfaces de bloqueio quando necessário
 */
@AndroidEntryPoint
class AppBlockerService : Service() {
    
    @Inject
    lateinit var appBlockRepository: AppBlockRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitoringJob: Job? = null
    private var activeBlocks: List<AppBlock> = emptyList()
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP -> stopMonitoring()
        }
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
    
    /**
     * Inicia o monitoramento de bloqueios de aplicativos.
     */
    private fun startMonitoring() {
        Timber.d("Iniciando monitoramento de bloqueios de aplicativos")
        
        if (monitoringJob != null) {
            Timber.d("Monitoramento já está ativo")
            return
        }
        
        monitoringJob = serviceScope.launch {
            // Monitor de bloqueios ativos
            launch {
                appBlockRepository.getActiveBlocks()
                    .catch { e -> 
                        Timber.e(e, "Erro ao monitorar bloqueios ativos")
                    }
                    .collectLatest { blocks ->
                        activeBlocks = blocks
                        updateNotification(blocks)
                        Timber.d("Bloqueios ativos atualizados: ${blocks.size}")
                    }
            }
            
            // Limpeza periódica de bloqueios expirados
            launch {
                while (true) {
                    try {
                        cleanupExpiredBlocks()
                        Timber.d("Limpeza de bloqueios expirados realizada")
                    } catch (e: Exception) {
                        Timber.e(e, "Erro ao limpar bloqueios expirados")
                    }
                    
                    delay(15.minutes)
                }
            }
        }
    }
    
    /**
     * Limpa bloqueios expirados.
     */
    private suspend fun cleanupExpiredBlocks() {
        // Verificar se appBlockRepository é uma implementação de AppBlockRepositoryImpl
        if (appBlockRepository is com.zenlauncher.data.repositories.AppBlockRepositoryImpl) {
            (appBlockRepository as com.zenlauncher.data.repositories.AppBlockRepositoryImpl).cleanupExpiredBlocks()
        } else {
            Timber.w("Não foi possível limpar bloqueios expirados: repositório não suporta esta operação")
        }
    }
    
    /**
     * Para o monitoramento de bloqueios de aplicativos.
     */
    private fun stopMonitoring() {
        Timber.d("Parando monitoramento de bloqueios de aplicativos")
        monitoringJob?.cancel()
        monitoringJob = null
        stopSelf()
    }
    
    /**
     * Atualiza a notificação com informações sobre bloqueios ativos.
     */
    private fun updateNotification(blocks: List<AppBlock>) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createForegroundNotification(blocks))
    }
    
    /**
     * Cria a notificação para o serviço em primeiro plano.
     */
    private fun createForegroundNotification(blocks: List<AppBlock> = emptyList()): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("ZenLauncher - Modo Foco")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setSilent(true)
        
        if (blocks.isEmpty()) {
            builder.setContentText("Nenhum aplicativo bloqueado")
        } else {
            val contentText = "${blocks.size} ${if (blocks.size == 1) "aplicativo bloqueado" else "aplicativos bloqueados"}"
            builder.setContentText(contentText)
            
            // Estilo expandido para mostrar detalhes
            val inboxStyle = NotificationCompat.InboxStyle()
                .setBigContentTitle(contentText)
            
            blocks.take(5).forEach { block ->
                val appName = getAppNameFromPackage(block.packageName) ?: block.packageName
                val remainingTime = formatRemainingTime(block.remainingTimeMs())
                inboxStyle.addLine("$appName - $remainingTime")
            }
            
            if (blocks.size > 5) {
                inboxStyle.addLine("... e ${blocks.size - 5} mais")
            }
            
            builder.setStyle(inboxStyle)
        }
        
        return builder.build()
    }
    
    /**
     * Cria o canal de notificação para o serviço.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ZenLauncher Modo Foco"
            val descriptionText = "Notificações do serviço de bloqueio de aplicativos"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Obtém o nome do aplicativo a partir do pacote.
     */
    private fun getAppNameFromPackage(packageName: String): String? {
        return try {
            val packageManager = applicationContext.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Formata o tempo restante em formato legível.
     */
    private fun formatRemainingTime(timeMs: Long): String {
        if (timeMs <= 0) return "Expirado"
        
        val seconds = timeMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        
        return when {
            hours > 0 -> "$hours ${if (hours == 1L) "hora" else "horas"}"
            minutes > 0 -> "$minutes ${if (minutes == 1L) "minuto" else "minutos"}"
            else -> "$seconds ${if (seconds == 1L) "segundo" else "segundos"}"
        }
    }
    
    companion object {
        private const val CHANNEL_ID = "zenlauncher_focus_mode_channel"
        private const val NOTIFICATION_ID = 1001
        
        const val ACTION_START = "com.zenlauncher.action.START_APP_BLOCKER"
        const val ACTION_STOP = "com.zenlauncher.action.STOP_APP_BLOCKER"
        
        /**
         * Cria intent para iniciar o serviço.
         */
        fun getStartIntent(context: Context): Intent {
            return Intent(context, AppBlockerService::class.java).apply {
                action = ACTION_START
            }
        }
        
        /**
         * Cria intent para parar o serviço.
         */
        fun getStopIntent(context: Context): Intent {
            return Intent(context, AppBlockerService::class.java).apply {
                action = ACTION_STOP
            }
        }
    }
}
