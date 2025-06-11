package com.zenlauncher.data.services

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.zenlauncher.data.extensions.UsageEventsExtensions.copyFromEvent
import com.zenlauncher.domain.repositories.AppBlockRepository
import com.zenlauncher.presentation.focus.blockscreen.AppBlockScreenActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitor de aplicativos bloqueados.
 * 
 * Esta classe é responsável por detectar quando um aplicativo bloqueado
 * está sendo iniciado e interceptá-lo, mostrando uma tela de bloqueio.
 */
@Singleton
class AppBlockMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appBlockRepository: AppBlockRepository
) {
    private var monitorJob: Job? = null
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0
    
    /**
     * Inicia o monitoramento de aplicativos bloqueados.
     */
    fun startMonitoring(scope: CoroutineScope) {
        if (monitorJob != null) {
            Timber.d("Monitoramento já está ativo")
            return
        }
        
        Timber.d("Iniciando monitoramento de aplicativos")
        
        monitorJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                try {
                    val foregroundPackage = getForegroundPackage()
                    
                    if (foregroundPackage != null && 
                        foregroundPackage != context.packageName &&
                        foregroundPackage != lastBlockedPackage) {
                        
                        Timber.d("Verificando pacote em primeiro plano: $foregroundPackage")
                        
                        if (appBlockRepository.isAppBlocked(foregroundPackage)) {
                            Timber.d("Aplicativo bloqueado detectado: $foregroundPackage")
                            
                            // Evitar múltiplas interceptações no mesmo aplicativo em curto período
                            val now = System.currentTimeMillis()
                            if (foregroundPackage != lastBlockedPackage || (now - lastBlockTime > 2000)) {
                                lastBlockedPackage = foregroundPackage
                                lastBlockTime = now
                                
                                withContext(Dispatchers.Main) {
                                    showBlockScreen(foregroundPackage)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Erro no monitoramento de aplicativos")
                }
                
                delay(2000) // Verificar a cada 2 segundos (otimizado)
            }
        }
    }
    
    /**
     * Para o monitoramento de aplicativos bloqueados.
     */
    fun stopMonitoring() {
        Timber.d("Parando monitoramento de aplicativos")
        monitorJob?.cancel()
        monitorJob = null
    }
    
    /**
     * Obtém o pacote do aplicativo em primeiro plano.
     */
    private fun getForegroundPackage(): String? {
        return try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // Para Android 10+ (Q)
                    getRecentForegroundPackage()
                }
                else -> {
                    // Para versões anteriores ao Android 10
                    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    activityManager.runningAppProcesses?.firstOrNull { 
                        it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND 
                    }?.processName
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Erro ao obter pacote em primeiro plano")
            null
        }
    }
    
    /**
     * Obtém o pacote recente em primeiro plano usando UsageStatsManager.
     */
    private fun getRecentForegroundPackage(): String? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()
        
        // Buscar eventos de uso nos últimos 5 segundos
        val usageEvents = usageStatsManager.queryEvents(time - 5000, time)
        
        // Processar eventos em ordem cronológica reversa
        var lastEvent: UsageEvents.Event? = null
        val event = UsageEvents.Event()
        
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            
            // Verificar se é um evento de transição para primeiro plano
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                lastEvent = UsageEvents.Event()
                // Usar nossa implementação de cópia
                lastEvent.copyFromEvent(event)
            }
        }
        
        return lastEvent?.packageName
    }
    
    /**
     * Mostra a tela de bloqueio para um aplicativo específico.
     */
    private fun showBlockScreen(packageName: String) {
        val intent = Intent(context, AppBlockScreenActivity::class.java).apply {
            putExtra(AppBlockScreenActivity.EXTRA_PACKAGE_NAME, packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        context.startActivity(intent)
    }
}
