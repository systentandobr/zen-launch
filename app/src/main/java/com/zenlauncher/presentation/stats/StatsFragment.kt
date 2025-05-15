package com.zenlauncher.presentation.stats

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.zenlauncher.R
import com.zenlauncher.databinding.FragmentStatsBinding
import com.zenlauncher.presentation.stats.adapters.AppUsageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Fragment para exibir estatísticas de uso.
 */
@AndroidEntryPoint
class StatsFragment : Fragment() {
    
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: StatsViewModel by viewModels()
    private lateinit var usageAdapter: AppUsageAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupTabLayout()
        setupRecyclerView()
        observeViewModel()
        checkUsageStatsPermission()
    }
    
    /**
     * Configura o TabLayout para alternar entre períodos (Hoje, Semana, Mês).
     */
    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.setPeriod(tab.position)
            }
            
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    
    /**
     * Configura o RecyclerView para exibir estatísticas de uso de aplicativos.
     */
    private fun setupRecyclerView() {
        usageAdapter = AppUsageAdapter()
        binding.appStatsRecyclerView.adapter = usageAdapter
    }
    
    /**
     * Observa mudanças no ViewModel e atualiza a UI.
     */
    private fun observeViewModel() {
        // Observar estatísticas de aplicativos
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appStats.collect { stats ->
                usageAdapter.submitList(stats)
                binding.emptyView.visibility = if (stats.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        // Observar tempo total de uso
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.totalUsageTime.collect { totalTime ->
                binding.totalTimeText.text = viewModel.formatDuration(totalTime)
            }
        }
        
        // Observar carregamento
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        // Observar mensagens de erro
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
        
        // Observar status de permissão
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hasPermission.collect { hasPermission ->
                if (!hasPermission) {
                    showPermissionDialog()
                }
            }
        }
    }
    
    /**
     * Verifica se o aplicativo tem permissão para acessar estatísticas de uso.
     */
    private fun checkUsageStatsPermission() {
        val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            requireContext().packageName
        )
        
        if (mode != AppOpsManager.MODE_ALLOWED) {
            showPermissionDialog()
        }
    }
    
    /**
     * Exibe diálogo solicitando permissão para acessar estatísticas de uso.
     */
    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permission_usage_stats_title)
            .setMessage(R.string.permission_usage_stats_message)
            .setPositiveButton(R.string.permission_grant) { _, _ ->
                openUsageAccessSettings()
            }
            .setNegativeButton(R.string.permission_cancel, null)
            .setCancelable(false)
            .show()
    }
    
    /**
     * Abre as configurações de acesso a estatísticas de uso.
     */
    private fun openUsageAccessSettings() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
    
    /**
     * Atualiza as estatísticas ao retornar para o Fragment.
     */
    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
