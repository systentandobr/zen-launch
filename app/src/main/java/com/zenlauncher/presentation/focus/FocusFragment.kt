package com.zenlauncher.presentation.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zenlauncher.databinding.FragmentFocusBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment para o Deep Focus Mode redesenhado.
 * 
 * Interface minimalista focada no timer e controle de duração.
 */
@AndroidEntryPoint
class FocusFragment : Fragment() {
    
    private var _binding: FragmentFocusBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FocusViewModel by viewModels()
    
    // Estados do timer
    private var isTimerRunning = false
    private var currentDurationMinutes = 25
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFocusBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupDurationSlider()
        setupStartButton()
        setupBlockedApps()
        observeData()
        
        // Configuração inicial
        updateTimerDisplay(currentDurationMinutes)
        updateDurationLabel(currentDurationMinutes)
    }
    
    private fun setupDurationSlider() {
        binding.durationSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && !isTimerRunning) {
                    // Converter progress (0-105) para minutos (15-120)
                    val minutes = 15 + progress
                    currentDurationMinutes = minutes
                    updateTimerDisplay(minutes)
                    updateDurationLabel(minutes)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Configurar valor inicial (25 minutos = progress 10)
        binding.durationSlider.progress = 10
    }
    
    private fun setupStartButton() {
        binding.startFocusButton.setOnClickListener {
            if (!isTimerRunning) {
                startFocusSession()
            } else {
                stopFocusSession()
            }
        }
    }
    
    /**
     * Atualiza a UI baseada no estado da sessão de foco.
     */
    private fun updateUIBasedOnFocusState(state: com.zenlauncher.domain.entities.FocusSessionState) {
        when (state) {
            is com.zenlauncher.domain.entities.FocusSessionState.Idle -> {
                isTimerRunning = false
                binding.durationSlider.isEnabled = true
            }
            is com.zenlauncher.domain.entities.FocusSessionState.Running -> {
                isTimerRunning = true
                binding.durationSlider.isEnabled = false
            }
            is com.zenlauncher.domain.entities.FocusSessionState.Paused -> {
                isTimerRunning = false
                binding.durationSlider.isEnabled = false
            }
            is com.zenlauncher.domain.entities.FocusSessionState.Completed -> {
                isTimerRunning = false
                binding.durationSlider.isEnabled = true
                // TODO: Mostrar celebração ou feedback de conclusão
            }
        }
    }
    
    private fun setupBlockedApps() {
        // TODO: Carregar apps que serão bloqueados dinamicamente
        // Por enquanto usando os placeholders do XML
    }
    
    private fun startFocusSession() {
        isTimerRunning = true
        binding.startFocusButton.text = "⏸ Pausar Foco"
        binding.durationSlider.isEnabled = false
        
        // Iniciar sessão de foco através do ViewModel
        viewModel.startFocusSession(currentDurationMinutes)
    }
    
    private fun stopFocusSession() {
        isTimerRunning = false
        binding.startFocusButton.text = "▶ Iniciar Foco"
        binding.durationSlider.isEnabled = true
        
        // Parar sessão de foco através do ViewModel
        viewModel.stopFocusSession()
    }
    
    private fun updateTimerDisplay(minutes: Int) {
        val displayTime = String.format("%02d:00", minutes)
        binding.timerText.text = displayTime
    }
    
    private fun updateDurationLabel(minutes: Int) {
        val text = when {
            minutes < 60 -> "Duração: $minutes minutos"
            minutes == 60 -> "Duração: 1 hora"
            else -> {
                val hours = minutes / 60
                val remainingMinutes = minutes % 60
                if (remainingMinutes == 0) {
                    "Duração: $hours horas"
                } else {
                    "Duração: ${hours}h ${remainingMinutes}min"
                }
            }
        }
        binding.durationLabel.text = text
    }
    
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observar estado da sessão de foco
            viewModel.focusSessionState.collect { state ->
                updateUIBasedOnFocusState(state)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observar texto do timer
            viewModel.timerText.collect { timerText ->
                binding.timerText.text = timerText
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observar texto do botão
            viewModel.focusButtonText.collect { buttonText ->
                binding.startFocusButton.text = buttonText
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Observar erros
            viewModel.error.collect { error ->
                error?.let {
                    // TODO: Mostrar toast ou snackbar com erro
                    // Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
