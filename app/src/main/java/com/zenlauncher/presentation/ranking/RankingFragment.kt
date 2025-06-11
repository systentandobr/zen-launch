package com.zenlauncher.presentation.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zenlauncher.databinding.FragmentRankingBinding
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
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        // TODO: Implementar observadores quando ViewModel for criado
        // viewModel.userStats.observe(viewLifecycleOwner) { stats ->
        //     updateUserStats(stats)
        // }
        
        // viewModel.ranking.observe(viewLifecycleOwner) { ranking ->
        //     updateRanking(ranking)
        // }
    }
    
    private fun setupClickListeners() {
        // TODO: Implementar cliques nos botões de período (Semanal/Mensal/Amigos)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
