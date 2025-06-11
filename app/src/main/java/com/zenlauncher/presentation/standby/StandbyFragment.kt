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
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zenlauncher.databinding.FragmentStandbyBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

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
                    navigateToHome()
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        requireContext().registerReceiver(batteryReceiver, filter)
    }

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandbyBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClock()
        setupClickListeners()
        observeData()

        // Manter a tela ligada
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Ocultar a barra de navegação e status para modo imersivo
        enableImmersiveMode()

        setupBatteryReceiver()
        observeViewModel()

        viewModel.startClock()

    }

    private fun setupClock() {
        // Atualiza o relógio a cada minuto
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    updateDateTime()
                }
            }
        }, 0, 60000) // Atualiza a cada minuto

        // Atualização inicial
        updateDateTime()
    }

    private fun updateDateTime() {
        val now = Calendar.getInstance().time
        // binding.tvTime.text = timeFormat.format(now)
        // binding.tvDate.text = dateFormat.format(now)
    }

    private fun setupClickListeners() {
        // TODO: Implementar cliques nos cards de atividades
        // Por exemplo:
        // binding.cardSleep.setOnClickListener {
        //     // Ação para dormir
        // }

        // Click no card de streak para mais detalhes
        // binding.streakCircle.setOnClickListener {
            // TODO: Navegar para tela de detalhes do streak
        // }
    }

    private fun observeData() {
        // Observar dados do ViewModel quando implementado
        viewLifecycleOwner.lifecycleScope.launch {
            // viewModel.streakData.collect { streak ->
            //     binding.streakCircle.text = streak.toString()
            // }
        }

        // TODO: Observar outras estatísticas
    }

    /**
     * Observa mudanças no ViewModel e atualiza a UI.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.batteryLevel.collect { level ->
                        binding.batteryLevelText.text = "$level%"
                        binding.batteryProgressBar.progress = level
                    }
                }
                
                launch {
                    viewModel.isCharging.collect { isCharging ->
                        if (!isCharging) {
                            navigateToHome()
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Navega de volta para a tela inicial.
     */
    private fun navigateToHome() {
        // Como agora usamos Bottom Navigation, precisamos finalizar a StandbyActivity
        // para voltar para a MainActivity
        requireActivity().finish()
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
