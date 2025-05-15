package com.zenlauncher.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zenlauncher.data.model.AppInfo
import com.zenlauncher.di.ServiceLocator
import com.zenlauncher.domain.usecase.GetAllAppsUseCase
import com.zenlauncher.domain.usecase.GetFavoriteAppsUseCase
import com.zenlauncher.domain.usecase.LaunchAppUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para a tela principal do launcher
 */
class HomeViewModel(
    application: Application,
    private val getAllAppsUseCase: GetAllAppsUseCase,
    private val launchAppUseCase: LaunchAppUseCase,
    private val getFavoriteAppsUseCase: GetFavoriteAppsUseCase
) : AndroidViewModel(application) {
    
    // Lista de aplicativos favoritos para o dock
    private val _favoriteApps = MutableLiveData<List<AppInfo>>()
    val favoriteApps: LiveData<List<AppInfo>> = _favoriteApps
    
    // Estado de carregamento
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Formato do relógio
    private val _use24HourFormat = MutableLiveData(true)
    val use24HourFormat: LiveData<Boolean> = _use24HourFormat
    
    // Orientação da tela (portrait/landscape)
    private val _isLandscape = MutableLiveData(false)
    val isLandscape: LiveData<Boolean> = _isLandscape
    
    // Tema escuro
    private val _darkThemeEnabled = MutableLiveData(true)
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled
    
    init {
        loadFavoriteApps()
        loadClockPreferences()
        loadThemePreferences()
    }
    
    /**
     * Carrega as preferências do relógio
     */
    private fun loadClockPreferences() {
        viewModelScope.launch {
            val preferences = ServiceLocator.provideLauncherPreferences()
            _use24HourFormat.value = preferences.getClockFormat24h()
        }
    }
    
    /**
     * Carrega as preferências de tema
     */
    private fun loadThemePreferences() {
        viewModelScope.launch {
            val preferences = ServiceLocator.provideLauncherPreferences()
            _darkThemeEnabled.value = preferences.getDarkThemeEnabled()
        }
    }
    
    /**
     * Alterna entre formato 12h e 24h
     */
    fun toggleClockFormat() {
        val currentFormat = _use24HourFormat.value ?: true
        _use24HourFormat.value = !currentFormat
        
        viewModelScope.launch {
            val preferences = ServiceLocator.provideLauncherPreferences()
            preferences.setClockFormat24h(!currentFormat)
        }
    }
    
    /**
     * Alterna entre tema claro e escuro
     */
    fun toggleDarkTheme() {
        val currentTheme = _darkThemeEnabled.value ?: true
        _darkThemeEnabled.value = !currentTheme
        
        viewModelScope.launch {
            val preferences = ServiceLocator.provideLauncherPreferences()
            preferences.setDarkThemeEnabled(!currentTheme)
        }
    }
    
    /**
     * Atualiza o estado da orientação da tela
     */
    fun setLandscapeOrientation(isLandscape: Boolean) {
        _isLandscape.value = isLandscape
    }
    
    /**
     * Carrega os aplicativos favoritos para o dock
     */
    fun loadFavoriteApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Obtém a lista de pacotes favoritos
                val favoritePackages = getFavoriteAppsUseCase()
                
                // Obtém todos os aplicativos
                val allApps = getAllAppsUseCase(false).first()
                
                // Filtra apenas os favoritos
                val favorites = allApps.filter { app ->
                    app.packageName in favoritePackages
                }.sortedBy { app ->
                    // Ordena na mesma ordem que a lista de pacotes
                    favoritePackages.indexOf(app.packageName)
                }
                
                _favoriteApps.value = favorites
            } catch (e: Exception) {
                // Em caso de erro, retorna lista vazia
                _favoriteApps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Inicia um aplicativo
     * @param packageName nome do pacote do aplicativo
     * @return true se o aplicativo foi iniciado com sucesso
     */
    fun launchApp(packageName: String): Boolean {
        return launchAppUseCase(packageName)
    }
}