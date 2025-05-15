package com.zenlauncher.presentation.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.zenlauncher.databinding.FragmentAppsBinding
import com.zenlauncher.presentation.common.adapters.AppAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Fragment para exibir todos os aplicativos instalados.
 */
@AndroidEntryPoint
class AppsFragment : Fragment() {
    
    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AppsViewModel by viewModels()
    private lateinit var appAdapter: AppAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupSearchView()
        observeViewModel()
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadApps()
        }
    }
    
    /**
     * Configura o RecyclerView para exibir os aplicativos.
     */
    private fun setupRecyclerView() {
        appAdapter = AppAdapter(
            onAppClick = { app ->
                viewModel.launchApp(app.packageName)
            },
            onAppLongClick = { app ->
                // Implementar ação de pressionar longo (ex: adicionar aos favoritos)
                // TODO: Implementar
            }
        )
        
        binding.appsRecyclerView.apply {
            adapter = appAdapter
            layoutManager = GridLayoutManager(context, 4) // 4 apps por linha
        }
    }
    
    /**
     * Configura a caixa de pesquisa.
     */
    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }
    
    /**
     * Observa as mudanças no ViewModel e atualiza a UI.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredApps.collectLatest { apps ->
                appAdapter.submitList(apps)
                binding.emptyView.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
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
