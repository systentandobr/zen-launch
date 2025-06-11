package com.zenlauncher.presentation.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.zenlauncher.databinding.FragmentRankingBinding
import com.zenlauncher.presentation.ranking.adapters.RankingAdapter
import com.zenlauncher.presentation.ranking.model.RankingEntry
import com.zenlauncher.presentation.ranking.model.RankingPeriod
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragment para exibir ranking de usuários e gamificação.
 * 
 * Mostra:
 * - Streak atual do usuário
 * - Ranking semanal/mensal/amigos
 * - Próximas recompensas
 * - Conquistas desbloqueadas
 */
@AndroidEntryPoint
class RankingFragment : Fragment() {
    
    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RankingViewModel by viewModels()
    private lateinit var rankingAdapter: RankingAdapter
    private var currentPeriod = RankingPeriod.WEEKLY
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupTabButtons()
        loadMockData()
    }
    
    private fun setupRecyclerView() {
        rankingAdapter = RankingAdapter { rankingEntry ->
            // TODO: Ação ao clicar em um usuário do ranking
        }
        
        binding.rvRanking.apply {
            adapter = rankingAdapter
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
        }
    }
    
    private fun setupTabButtons() {
        binding.btnWeekly.setOnClickListener {
            selectPeriod(RankingPeriod.WEEKLY)
        }
        
        binding.btnMonthly.setOnClickListener {
            selectPeriod(RankingPeriod.MONTHLY)
        }
        
        binding.btnFriends.setOnClickListener {
            selectPeriod(RankingPeriod.FRIENDS)
        }
    }
    
    private fun selectPeriod(period: RankingPeriod) {
        if (currentPeriod == period) return
        
        currentPeriod = period
        updateTabButtonsState()
        loadRankingData(period)
    }
    
    private fun updateTabButtonsState() {
        // Reset all buttons to secondary state
        binding.btnWeekly.setBackgroundResource(com.zenlauncher.R.drawable.zen_button_secondary_background)
        binding.btnMonthly.setBackgroundResource(com.zenlauncher.R.drawable.zen_button_secondary_background)
        binding.btnFriends.setBackgroundResource(com.zenlauncher.R.drawable.zen_button_secondary_background)
        
        binding.btnWeekly.setTextColor(resources.getColor(com.zenlauncher.R.color.textColorSecondary, null))
        binding.btnMonthly.setTextColor(resources.getColor(com.zenlauncher.R.color.textColorSecondary, null))
        binding.btnFriends.setTextColor(resources.getColor(com.zenlauncher.R.color.textColorSecondary, null))
        
        // Set selected button to primary state
        when (currentPeriod) {
            RankingPeriod.WEEKLY -> {
                binding.btnWeekly.setBackgroundResource(com.zenlauncher.R.drawable.zen_button_primary_background)
                binding.btnWeekly.setTextColor(resources.getColor(com.zenlauncher.R.color.textColorPrimary, null))
            }
            RankingPeriod.MONTHLY -> {
                binding.btnMonthly.setBackgroundResource(com.zenlauncher.R.drawable.zen_button_primary_background)
                binding.btnMonthly.setTextColor(resources.getColor(com.zenlauncher.R.color.textColorPrimary, null))
            }
            RankingPeriod.FRIENDS -> {
                binding.btnFriends.setBackgroundResource(com.zenlauncher.R.drawable.zen_button_primary_background)
                binding.btnFriends.setTextColor(resources.getColor(com.zenlauncher.R.color.textColorPrimary, null))
            }
        }
    }
    
    private fun loadRankingData(period: RankingPeriod) {
        // TODO: Implementar carregamento real do ViewModel
        loadMockData()
    }
    
    private fun loadMockData() {
        // Dados mock para demonstração
        binding.streakNumber.text = "12"
        binding.bestStreak.text = "18"
        
        val mockRanking = listOf(
            RankingEntry(4, "Marina", "25% tempo de tela", 1100, 6),
            RankingEntry(5, "João", "22% tempo de tela", 1050, 3),
            RankingEntry(6, "Patricia", "18% tempo de tela", 980, 5),
            RankingEntry(7, "Roberto", "15% tempo de tela", 920, 2)
        )
        
        rankingAdapter.submitList(mockRanking)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
