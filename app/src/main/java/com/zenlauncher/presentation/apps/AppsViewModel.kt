package com.zenlauncher.presentation.apps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.usecases.GetAllAppsUseCase
import com.zenlauncher.domain.usecases.LaunchAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela de listagem de todos os aplicativos.
 */
@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getAllAppsUseCase: GetAllAppsUseCase,
    private val launchAppUseCase: LaunchAppUseCase
) : ViewModel() {
    
    // Estado para a lista de aplicativos
    private val _apps = MutableStateFlow<List<App>>(emptyList())
    val apps: StateFlow<List<App>> = _apps.asStateFlow()
    
    // Estado para o filtro de pesquisa
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Estado para indicar carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado para erros
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Lista filtrada de aplicativos
    private val _filteredApps = MutableStateFlow<List<App>>(emptyList())
    val filteredApps: StateFlow<List<App>> = _filteredApps.asStateFlow()
    
    init {
        loadApps()
    }
    
    /**
     * Carrega a lista de todos os aplicativos instalados.
     */
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            getAllAppsUseCase()
                .catch { e ->
                    _error.value = e.message ?: "Erro ao carregar aplicativos"
                    _isLoading.value = false
                }
                .collect { appList ->
                    _apps.value = appList
                    filterApps()
                    _isLoading.value = false
                }
        }
    }
    
    /**
     * Atualiza o filtro de pesquisa e filtra a lista de aplicativos.
     * 
     * @param query Texto para pesquisa
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterApps()
    }
    
    /**
     * Filtra a lista de aplicativos com base na consulta de pesquisa.
     */
    private fun filterApps() {
        val query = _searchQuery.value.lowercase().trim()
        val allApps = _apps.value
        
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
     * Lança um aplicativo.
     * 
     * @param packageName Nome do pacote do aplicativo
     */
    fun launchApp(packageName: String) {
        viewModelScope.launch {
            launchAppUseCase(packageName)
        }
    }
    
    /**
     * Limpa o estado de erro.
     */
    fun clearError() {
        _error.value = null
    }
}
