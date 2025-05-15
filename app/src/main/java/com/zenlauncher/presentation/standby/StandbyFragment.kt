package com.zenlauncher.presentation.standby

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.zenlauncher.R
import com.zenlauncher.databinding.FragmentStandbyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment para o modo Standby/Always On quando o dispositivo está carregando.
 * Esta tela mantém-se ligada e exibe hora, data e informações de bateria.
 */
@AndroidEntryPoint
class StandbyFragment : Fragment() {
    
    private var _binding: FragmentStandbyBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StandbyViewModel by viewModels()
    
    private lateinit var batteryReceiver: BroadcastReceiver
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandbyBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Manter a tela ligada
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Ocultar a barra de navegação e status para modo imersivo
        enableImmersiveMode()
        
        setupBatteryReceiver()
        setupBackButton()
        setupDoubleTapToExit()
        observeViewModel()
        
        viewModel.startClock()
    }
    
    /**
     * Habilita o modo imersivo para ocultar barras do sistema.
     */
    private fun enableImmersiveMode() {
        val window = requireActivity().window
        
        ViewCompat.getWindowInsetsController(requireView())?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = 
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    
    /**
     * Configura o receptor para informações de bateria.
     */
    private fun setupBatteryReceiver() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                val batteryPct = (level * 100) / scale
                
                val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                 status == BatteryManager.BATTERY_STATUS_FULL
                
                viewModel.updateBatteryInfo(batteryPct, isCharging)
                
                // Se não estiver carregando, sair do modo standby
                if (!isCharging) {
                    navigateBack()
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        requireContext().registerReceiver(batteryReceiver, filter)
    }
    
    /**
     * Configura o botão de voltar.
     */
    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            navigateBack()
        }
    }
    
    /**
     * Configura detector de duplo toque para sair.
     */
    private fun setupDoubleTapToExit() {
        binding.standbyRootLayout.setOnClickListener {
            // Opcional: implementar detector de duplo toque
        }
    }
    
    /**
     * Observa mudanças no ViewModel e atualiza a UI.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentTime.collect { time ->
                        binding.clockTextView.text = time
                    }
                }
                
                launch {
                    viewModel.currentDate.collect { date ->
                        binding.dateTextView.text = date
                    }
                }
                
                launch {
                    viewModel.batteryLevel.collect { level ->
                        binding.batteryLevelText.text = "$level%"
                        binding.batteryProgressBar.progress = level
                    }
                }
                
                launch {
                    viewModel.isCharging.collect { isCharging ->
                        if (!isCharging) {
                            navigateBack()
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Navega de volta para a tela inicial.
     */
    private fun navigateBack() {
        findNavController().popBackStack()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        
        // Remover flag para manter tela ligada
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Desregistrar receiver
        try {
            requireContext().unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            // Ignorar se não estiver registrado
        }
        
        viewModel.stopClock()
        _binding = null
    }
}
