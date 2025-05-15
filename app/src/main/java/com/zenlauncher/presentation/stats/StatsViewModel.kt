package com.zenlauncher.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.entities.AppUsageStats
import com.zenlauncher.domain.repositories.UsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel para a tela de estatísticas de uso.
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    private val usageStatsRepository: UsageStatsRepository
) : ViewModel() {
    
    // Estado para estatísticas de uso
    private val _appStats = MutableStateFlow<List<AppUsageStats>>(emptyList())
    val appStats: StateFlow<List<AppUsageStats>> = _appStats.asStateFlow()
    
    // Estado para tempo total de uso
    private val _totalUsageTime = MutableStateFlow(0L)
    val totalUsageTime: StateFlow<Long> = _totalUsageTime.asStateFlow()
    
    // Estado para período selecionado (0 = hoje, 1 = semana, 2 = mês)
    private val _selectedPeriod = MutableStateFlow(0)
    val selectedPeriod: StateFlow<Int> = _selectedPeriod.asStateFlow()
    
    // Estado para carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado para mensagens de erro
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Estado para verificação de permissão
    private val _hasPermission = MutableStateFlow(true)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()
    
    init {
        loadData()
    }
    
    /**
     * Carrega os dados de uso para o período selecionado.
     */
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val (startTime, endTime) = getPeriodDates(_selectedPeriod.value)
                
                // Carregar estatísticas de uso
                val stats = usageStatsRepository.getMostUsedApps(
                    startTime, endTime, 10
                )
                
                if (stats.isEmpty()) {
                    // Se não há estatísticas, pode ser devido a permissões
                    _hasPermission.value = false
                } else {
                    _hasPermission.value = true
                    _appStats.value = stats
                }
                
                // Calcular tempo total de uso
                val totalTime = usageStatsRepository.getTotalUsageTime(startTime, endTime)
                _totalUsageTime.value = totalTime
                
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao carregar estatísticas de uso"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Define o período para as estatísticas (0 = hoje, 1 = semana, 2 = mês).
     */
    fun setPeriod(period: Int) {
        if (period in 0..2 && period != _selectedPeriod.value) {
            _selectedPeriod.value = period
            loadData()
        }
    }
    
    /**
     * Obtém as datas de início e fim para o período selecionado.
     */
    private fun getPeriodDates(period: Int): Pair<Date, Date> {
        val endTime = Date()
        val calendar = Calendar.getInstance()
        calendar.time = endTime
        
        when (period) {
            0 -> { // Hoje
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            1 -> { // Semana
                calendar.add(Calendar.DAY_OF_YEAR, -7)
            }
            2 -> { // Mês
                calendar.add(Calendar.MONTH, -1)
            }
        }
        
        val startTime = calendar.time
        return Pair(startTime, endTime)
    }
    
    /**
     * Formata a duração em milissegundos para formato legível.
     */
    fun formatDuration(durationMs: Long): String {
        val hours = durationMs / (1000 * 60 * 60)
        val minutes = (durationMs % (1000 * 60 * 60)) / (1000 * 60)
        
        return if (hours > 0) {
            "$hours h $minutes min"
        } else {
            "$minutes min"
        }
    }
    
    /**
     * Limpa o estado de erro.
     */
    fun clearError() {
        _error.value = null
    }
}
