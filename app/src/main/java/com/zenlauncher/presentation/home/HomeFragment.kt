package com.zenlauncher.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.zenlauncher.R
import com.zenlauncher.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment da tela inicial do ZenLauncher.
 *
 * Este fragmento exibe a interface principal com relógio, data,
 * widgets minimalistas e acesso rápido a aplicativos favoritos.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    
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
        setupFavoriteApps()
        setupAppDrawerButton()
        setupFocusModeButton()
        observeData()
    }
    
    private fun setupClock() {
        // Atualiza o relógio a cada minuto
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
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
        binding.clockTextView.text = timeFormat.format(now)
        binding.dateTextView.text = dateFormat.format(now)
    }
    
    private fun setupFavoriteApps() {
        // Implementação para exibir aplicativos favoritos
        // Será implementado posteriormente
    }
    
    private fun setupAppDrawerButton() {
        binding.allAppsButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_apps)
        }
    }
    
    private fun setupFocusModeButton() {
        binding.focusModeButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_focus)
        }
    }
    
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.usageStats.collect { stats ->
                // Atualizar UI com estatísticas de uso
                // Será implementado posteriormente
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
