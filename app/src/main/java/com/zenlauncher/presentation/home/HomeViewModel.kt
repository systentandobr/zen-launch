package com.zenlauncher.presentation.home

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.usecases.GetFavoriteAppsUseCase
import com.zenlauncher.domain.usecases.LaunchAppUseCase
import com.zenlauncher.domain.usecases.GetAllAppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para a tela inicial.
 *
 * Gerencia os dados necessários para a tela inicial, incluindo
 * estatísticas de uso, aplicativos favoritos e estado do modo de foco.
 */

// Tipos de atalho
enum class ShortcutType { PHONE, CAMERA, CUSTOM }
data class ShortcutApp(val id: Int, val type: ShortcutType, val packageName: String, val label: String, val position: Int)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFavoriteAppsUseCase: GetFavoriteAppsUseCase,
    private val launchAppUseCase: LaunchAppUseCase,
    private val getAllAppsUseCase: GetAllAppsUseCase,
    application: Application
) : AndroidViewModel(application) {
    
    // Estado para estatísticas de uso
    private val _usageStats = MutableStateFlow<Map<String, Long>>(emptyMap())
    val usageStats: StateFlow<Map<String, Long>> = _usageStats.asStateFlow()
    
    // Estado para aplicativos favoritos
    private val _favoriteApps = MutableStateFlow<List<App>>(emptyList())
    val favoriteApps: StateFlow<List<App>> = _favoriteApps.asStateFlow()
    
    // Estado do modo foco
    private val _focusModeActive = MutableStateFlow(false)
    val focusModeActive: StateFlow<Boolean> = _focusModeActive.asStateFlow()
    
    // Estado para erros
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val prefs = application.getSharedPreferences("shortcuts", Context.MODE_PRIVATE)
    private val _shortcuts = MutableStateFlow<List<ShortcutApp>>(emptyList())
    val shortcuts: StateFlow<List<ShortcutApp>> = _shortcuts.asStateFlow()
    
    init {
        loadFavoriteApps()
        loadUsageStats()
        checkFocusModeStatus()
        loadShortcuts()
    }
    
    /**
     * Carrega os aplicativos favoritos do usuário.
     */
    private fun loadFavoriteApps() {
        viewModelScope.launch {
            getFavoriteAppsUseCase()
                .catch { e ->
                    _error.value = e.message ?: "Erro ao carregar favoritos"
                }
                .collectLatest { apps ->
                    _favoriteApps.value = apps
                }
        }
    }
    
    /**
     * Carrega estatísticas de uso dos aplicativos.
     */
    private fun loadUsageStats() {
        viewModelScope.launch {
            // Implementação para carregar estatísticas de uso
            // Será implementado posteriormente
        }
    }
    
    /**
     * Verifica o status atual do modo foco.
     */
    private fun checkFocusModeStatus() {
        viewModelScope.launch {
            // Implementação para verificar estado do modo foco
            // Será implementado posteriormente
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

    private fun loadShortcuts() {
        // Exemplo: carrega atalhos do SharedPreferences (simplificado)
        val shortcuts = mutableListOf<ShortcutApp>()
        val phonePkg = prefs.getString("shortcut_phone", "com.android.dialer") ?: "com.android.dialer"
        val cameraPkg = prefs.getString("shortcut_camera", "com.android.camera") ?: "com.android.camera"
        shortcuts.add(ShortcutApp(1, ShortcutType.PHONE, phonePkg, "Telefone", 0))
        shortcuts.add(ShortcutApp(2, ShortcutType.CAMERA, cameraPkg, "Câmera", 1))
        _shortcuts.value = shortcuts
    }

    fun updateShortcut(type: ShortcutType, packageName: String, label: String) {
        val updated = _shortcuts.value.map {
            if (it.type == type) it.copy(packageName = packageName, label = label) else it
        }
        _shortcuts.value = updated
        // Persistir
        when(type) {
            ShortcutType.PHONE -> prefs.edit().putString("shortcut_phone", packageName).apply()
            ShortcutType.CAMERA -> prefs.edit().putString("shortcut_camera", packageName).apply()
            else -> {}
        }
    }

    fun getCompatibleApps(type: ShortcutType, onResult: (List<App>) -> Unit) {
        viewModelScope.launch {
            getAllAppsUseCase().collectLatest { apps ->
                val filtered = when(type) {
                    ShortcutType.PHONE -> apps.filter { it.appName.contains("telefone", true) || it.appName.contains("call", true) }
                    ShortcutType.CAMERA -> apps.filter { it.appName.contains("câmera", true) || it.appName.contains("camera", true) }
                    else -> apps
                }
                onResult(filtered)
            }
        }
    }
}
