package com.zenlauncher.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zenlauncher.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment da tela inicial do MindfulLauncher redesenhado.
 *
 * Este fragmento exibe a nova interface principal com:
 * - Relógio e data centralizados
 * - Card de streak circular
 * - Grid de sugestões de atividades
 * - Estatísticas de uso
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    private var timer: Timer? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClock()
        setupClickListeners()
        observeData()
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
        binding.tvTime.text = timeFormat.format(now)
        binding.tvDate.text = dateFormat.format(now)
    }
    
    private fun setupClickListeners() {
        // TODO: Implementar cliques nos cards de atividades
        // Por exemplo:
        // binding.cardSleep.setOnClickListener { 
        //     // Ação para dormir
        // }
        
        // Click no card de streak para mais detalhes
        binding.streakCircle.setOnClickListener {
            // TODO: Navegar para tela de detalhes do streak
        }
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
        _binding = null
    }
}
