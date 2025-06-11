package com.zenlauncher.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zenlauncher.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment da tela inicial do MindfulLauncher.
 *
 * Este fragmento exibe:
 * - Relógio e data atualizados
 * - Tempo total de tela de hoje
 * - Progressão da meta semanal
 * - Acesso rápido ao Deep Focus Mode
 * - Lista dos apps mais usados
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
        setupRecyclerView()
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
    
    private fun setupRecyclerView() {
        // Por enquanto, sem adapter até criar o AppUsageAdapter
        binding.rvMostUsedApps.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }
    
    private fun setupClickListeners() {
        // Card Deep Focus Mode
        binding.cardDeepFocus.setOnClickListener {
            // TODO: Navegar para Deep Focus Mode
            // Implementar quando tivermos a navegação pronta
        }
        
        // Ver todos os apps
        binding.tvSeeAllApps.setOnClickListener {
            // TODO: Navegar para lista completa de apps
            // Implementar quando tivermos a navegação pronta
        }
    }
    
    private fun observeData() {
        // Observar dados do ViewModel quando implementado
        viewLifecycleOwner.lifecycleScope.launch {
            // TODO: Implementar observadores para:
            // - Tempo de tela total
            // - Progressão da meta semanal
            // - Apps mais usados
            
            // Exemplo de dados mock por enquanto
            updateMockData()
        }
    }
    
    private fun updateMockData() {
        // Dados temporários para teste
        binding.tvScreenTime.text = "1h 27min"
        binding.tvWeeklyProgress.text = "75%"
        binding.progressWeekly.progress = 75
        
        // TODO: Atualizar com dados reais do ViewModel
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        timer = null
        _binding = null
    }
}
