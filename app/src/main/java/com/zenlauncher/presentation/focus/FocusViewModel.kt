package com.zenlauncher.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.entities.FocusSessionState
import com.zenlauncher.domain.usecases.BlockAppUseCase
import com.zenlauncher.domain.usecases.GetAllAppsUseCase
import com.zenlauncher.domain.usecases.GetFocusSessionStateUseCase
import com.zenlauncher.domain.usecases.GetMostUsedAppsUseCase
import com.zenlauncher.domain.usecases.StartFocusSessionUseCase
import com.zenlauncher.domain.usecases.StopFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela de Modo Foco (Focus Mode).
 */
@HiltViewModel
class FocusViewModel @Inject constructor(
    private val getAllAppsUseCase: GetAllAppsUseCase,
    private val getMostUsedAppsUseCase: GetMostUsedAppsUseCase,
    private val blockAppUseCase: BlockAppUseCase,
    private val startFocusSessionUseCase: StartFocusSessionUseCase,
    private val stopFocusSessionUseCase: StopFocusSessionUseCase,
    private val getFocusSessionStateUseCase: GetFocusSessionStateUseCase
) : ViewModel() {
    
    // Estado para aplicativos mais usados (sugestões para bloqueio)
    private val _mostUsedApps = MutableStateFlow<List<App>>(emptyList())
    val mostUsedApps: StateFlow<List<App>> = _mostUsedApps.asStateFlow()
    
    // Estado para todos os aplicativos
    private val _allApps = MutableStateFlow<List<App>>(emptyList())
    val allApps: StateFlow<List<App>> = _allApps.asStateFlow()
    
    // Estado para aplicativos selecionados para bloqueio
    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps: StateFlow<Set<String>> = _selectedApps.asStateFlow()
    
    // Estado para duração de bloqueio selecionada (em horas)
    private val _blockDuration = MutableStateFlow(1.0f)
    val blockDuration: StateFlow<Float> = _blockDuration.asStateFlow()
    
    // Estado para nível de bloqueio
    private val _blockLevel = MutableStateFlow(AppBlock.BlockLevel.MEDIUM)
    val blockLevel: StateFlow<AppBlock.BlockLevel> = _blockLevel.asStateFlow()
    
    // Estado para carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado para mensagens de erro
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Estado para sucesso de bloqueio
    private val _blockSuccess = MutableStateFlow(false)
    val blockSuccess: StateFlow<Boolean> = _blockSuccess.asStateFlow()
    
    // Filtro de pesquisa
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Aplicativos filtrados
    private val _filteredApps = MutableStateFlow<List<App>>(emptyList())
    val filteredApps: StateFlow<List<App>> = _filteredApps.asStateFlow()
    
    // Estado da sessão de foco
    private val _focusSessionState = MutableStateFlow<FocusSessionState>(FocusSessionState.Idle)
    val focusSessionState: StateFlow<FocusSessionState> = _focusSessionState.asStateFlow()
    
    // Estado do timer
    private val _timerText = MutableStateFlow("25:00")
    val timerText: StateFlow<String> = _timerText.asStateFlow()
    
    // Estado do botão de foco
    private val _focusButtonText = MutableStateFlow("▶ Iniciar Foco")
    val focusButtonText: StateFlow<String> = _focusButtonText.asStateFlow()
    
    init {
        loadApps()
        loadMostUsedApps()
        observeFocusSessionState()
    }
    
    /**
     * Carrega todos os aplicativos.
     */
    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            getAllAppsUseCase()
                .catch { e ->
                    _error.value = e.message ?: "Erro ao carregar aplicativos"
                    _isLoading.value = false
                }
                .collect { apps ->
                    _allApps.value = apps
                    filterApps()
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Carrega os aplicativos mais usados.
     */
    private fun loadMostUsedApps() {
        viewModelScope.launch {
            try {
                val stats = getMostUsedAppsUseCase(7, 5) // últimos 7 dias, top 5
                
                // Converter de AppUsageStats para App
                val apps = _allApps.value.filter { app ->
                    stats.any { it.packageName == app.packageName }
                }
                
                _mostUsedApps.value = apps
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar estatísticas de uso"
            }
        }
    }
    
    /**
     * Configura o filtro de pesquisa para aplicativos.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterApps()
    }
    
    /**
     * Filtra aplicativos com base na consulta de pesquisa.
     */
    private fun filterApps() {
        val query = _searchQuery.value.lowercase().trim()
        val allApps = _allApps.value
        
        if (query.isEmpty()) {
            _filteredApps.value = allApps
            return
        }
        
        _filteredApps.value = allApps.filter {
            it.appName.lowercase().contains(query) ||
            it.packageName.lowercase().contains(query)
        }
    }
    
    /**
     * Seleciona ou deseleciona um aplicativo para bloqueio.
     */
    fun toggleAppSelection(packageName: String) {
        val current = _selectedApps.value.toMutableSet()
        
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        
        _selectedApps.value = current
    }
    
    /**
     * Define a duração do bloqueio.
     */
    fun setBlockDuration(hours: Float) {
        _blockDuration.value = hours
    }
    
    /**
     * Define o nível de bloqueio.
     */
    fun setBlockLevel(level: AppBlock.BlockLevel) {
        _blockLevel.value = level
    }
    
    /**
     * Executa o bloqueio para os aplicativos selecionados.
     */
    fun blockSelectedApps() {
        if (_selectedApps.value.isEmpty()) {
            _error.value = "Selecione pelo menos um aplicativo para bloquear"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val duration = _blockDuration.value
                val level = _blockLevel.value
                
                var allSuccessful = true
                
                for (packageName in _selectedApps.value) {
                    val success = blockAppUseCase(packageName, duration, level)
                    if (!success) {
                        allSuccessful = false
                    }
                }
                
                if (allSuccessful) {
                    _blockSuccess.value = true
                    // Limpar seleções após sucesso
                    _selectedApps.value = emptySet()
                } else {
                    _error.value = "Falha ao bloquear alguns aplicativos"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao bloquear aplicativos"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Limpa o estado de erro.
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Limpa o estado de sucesso de bloqueio.
     */
    fun clearBlockSuccess() {
        _blockSuccess.value = false
    }
    
    /**
     * Observa o estado da sessão de foco.
     */
    private fun observeFocusSessionState() {
        viewModelScope.launch {
            getFocusSessionStateUseCase().collect { state ->
                _focusSessionState.value = state
                updateUIBasedOnState(state)
            }
        }
    }
    
    /**
     * Atualiza a UI baseada no estado da sessão de foco.
     */
    private fun updateUIBasedOnState(state: FocusSessionState) {
        when (state) {
            is FocusSessionState.Idle -> {
                _focusButtonText.value = "▶ Iniciar Foco"
                _timerText.value = "25:00"
            }
            is FocusSessionState.Running -> {
                _focusButtonText.value = "⏸ Pausar Foco"
                val minutes = state.remainingTimeMinutes
                val seconds = state.remainingTimeSeconds
                _timerText.value = String.format("%02d:%02d", minutes, seconds)
            }
            is FocusSessionState.Paused -> {
                _focusButtonText.value = "▶ Retomar Foco"
                val minutes = state.remainingTimeMinutes
                val seconds = state.remainingTimeSeconds
                _timerText.value = String.format("%02d:%02d", minutes, seconds)
            }
            is FocusSessionState.Completed -> {
                _focusButtonText.value = "▶ Iniciar Foco"
                _timerText.value = "00:00"
                // Mostrar notificação de conclusão
                // TODO: Implementar notificação
            }
        }
    }
    
    /**
     * Inicia uma sessão de foco.
     */
    fun startFocusSession(durationMinutes: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Usar apps selecionados ou apps mais usados como padrão
                val appsToBlock = if (_selectedApps.value.isNotEmpty()) {
                    _selectedApps.value.toList()
                } else {
                    _mostUsedApps.value.map { it.packageName }
                }
                
                val result = startFocusSessionUseCase(durationMinutes, appsToBlock)
                
                result.onFailure { error ->
                    _error.value = error.message ?: "Erro ao iniciar sessão de foco"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao iniciar sessão de foco"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Para a sessão de foco atual.
     */
    fun stopFocusSession() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val result = stopFocusSessionUseCase()
                
                result.onFailure { error ->
                    _error.value = error.message ?: "Erro ao parar sessão de foco"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao parar sessão de foco"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
