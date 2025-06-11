package com.zenlauncher.presentation.focus.blockscreen

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.repositories.AppBlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel para a tela de bloqueio de aplicativo.
 */
@HiltViewModel
class AppBlockScreenViewModel @Inject constructor(
    private val appBlockRepository: AppBlockRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // Estado para informações do aplicativo bloqueado
    private val _appInfo = MutableStateFlow<AppInfo?>(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo.asStateFlow()
    
    // Estado para o bloco de bloqueio
    private val _appBlock = MutableStateFlow<AppBlock?>(null)
    val appBlock: StateFlow<AppBlock?> = _appBlock.asStateFlow()
    
    // Estado para o tempo restante
    private val _remainingTime = MutableStateFlow(0L)
    val remainingTime: StateFlow<Long> = _remainingTime.asStateFlow()
    
    // Estado para descrição do nível de bloqueio
    private val _blockLevelDescription = MutableStateFlow("")
    val blockLevelDescription: StateFlow<String> = _blockLevelDescription.asStateFlow()
    
    // Estado para indicar se o app foi desbloqueado
    private val _isUnblocked = MutableStateFlow(false)
    val isUnblocked: StateFlow<Boolean> = _isUnblocked.asStateFlow()
    
    private var packageName: String? = null
    private var timerJob: Job? = null
    
    /**
     * Carrega informações do bloqueio para um aplicativo específico.
     */
    fun loadAppBlockInfo(packageName: String) {
        this.packageName = packageName
        
        // Carregar informações básicas do aplicativo
        viewModelScope.launch {
            val appInfo = getAppInfo(packageName)
            _appInfo.value = appInfo
        }
        
        // Observar detalhes do bloqueio
        viewModelScope.launch {
            appBlockRepository.getAppBlock(packageName)
                .collectLatest { block ->
                    _appBlock.value = block
                    
                    if (block != null) {
                        updateBlockLevelDescription(block.blockLevel)
                        startRemainingTimeTimer(block)
                    } else {
                        _isUnblocked.value = true
                    }
                }
        }
    }
    
    /**
     * Estende o tempo de bloqueio.
     */
    fun extendBlockTime(additionalTimeMs: Long) {
        val currentPackage = packageName ?: return
        
        viewModelScope.launch {
            try {
                val success = appBlockRepository.extendBlockTime(currentPackage, additionalTimeMs)
                Timber.d("Extensão de bloqueio: $success")
            } catch (e: Exception) {
                Timber.e(e, "Erro ao estender tempo de bloqueio")
            }
        }
    }
    
    /**
     * Remove o bloqueio do aplicativo.
     */
    fun unblockApp() {
        val currentPackage = packageName ?: return
        
        viewModelScope.launch {
            try {
                val success = appBlockRepository.unblockApp(currentPackage)
                if (success) {
                    _isUnblocked.value = true
                }
            } catch (e: Exception) {
                Timber.e(e, "Erro ao desbloquear aplicativo")
            }
        }
    }
    
    /**
     * Inicia temporizador para atualizar o tempo restante.
     */
    private fun startRemainingTimeTimer(block: AppBlock) {
        timerJob?.cancel()
        
        timerJob = viewModelScope.launch {
            while (isActive) {
                val remaining = block.remainingTimeMs()
                _remainingTime.value = remaining
                
                if (remaining <= 0) {
                    break
                }
                
                delay(1000) // Atualizar a cada segundo
            }
        }
    }
    
    /**
     * Atualiza a descrição do nível de bloqueio.
     */
    private fun updateBlockLevelDescription(blockLevel: AppBlock.BlockLevel) {
        _blockLevelDescription.value = when (blockLevel) {
            AppBlock.BlockLevel.SOFT -> "Lembrete Suave"
            AppBlock.BlockLevel.MEDIUM -> "Bloqueio Moderado"
            AppBlock.BlockLevel.HARD -> "Bloqueio Rigoroso"
        }
    }
    
    /**
     * Obtém informações básicas do aplicativo a partir do pacote.
     */
    private suspend fun getAppInfo(packageName: String): AppInfo? = withContext(Dispatchers.IO) {
        try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val appIcon = packageManager.getApplicationIcon(appInfo)
            
            return@withContext AppInfo(
                packageName = packageName,
                label = appName,
                icon = appIcon
            )
        } catch (e: Exception) {
            Timber.e(e, "Erro ao obter informações do aplicativo: $packageName")
            return@withContext null
        }
    }
    
    /**
     * Classe de dados para informações básicas do aplicativo.
     */
    data class AppInfo(
        val packageName: String,
        val label: String,
        val icon: Drawable
    )
    
    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
