package com.zenlauncher.presentation.standby

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

/**
 * ViewModel para o modo Standby/Always On.
 */
@HiltViewModel
class StandbyViewModel @Inject constructor() : ViewModel() {
    
    // Estado para horário atual
    private val _currentTime = MutableStateFlow("")
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()
    
    // Estado para data atual
    private val _currentDate = MutableStateFlow("")
    val currentDate: StateFlow<String> = _currentDate.asStateFlow()
    
    // Estado para porcentagem de bateria
    private val _batteryLevel = MutableStateFlow(0)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()
    
    // Estado para indicar se está carregando
    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow()
    
    // Formatos de data e hora
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    
    // Timer para atualizar relógio
    private var timer: Timer? = null
    
    /**
     * Inicia o monitoramento de hora e data.
     */
    fun startClock() {
        updateDateTime()
        
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateDateTime()
            }
        }, 0, 60000) // Atualiza a cada minuto
    }
    
    /**
     * Atualiza hora e data.
     */
    private fun updateDateTime() {
        val now = Calendar.getInstance().time
        _currentTime.value = timeFormat.format(now)
        _currentDate.value = dateFormat.format(now)
    }
    
    /**
     * Para o monitoramento de hora e data.
     */
    fun stopClock() {
        timer?.cancel()
        timer = null
    }
    
    /**
     * Atualiza informações de bateria.
     */
    fun updateBatteryInfo(level: Int, isCharging: Boolean) {
        _batteryLevel.value = level
        _isCharging.value = isCharging
    }
    
    override fun onCleared() {
        super.onCleared()
        stopClock()
    }
}
