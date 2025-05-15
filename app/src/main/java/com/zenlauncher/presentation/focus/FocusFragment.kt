package com.zenlauncher.presentation.focus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zenlauncher.R
import com.zenlauncher.databinding.FragmentFocusBinding
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.presentation.focus.adapters.AppBlockAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Fragment para a tela de Modo Foco (Focus Mode).
 */
@AndroidEntryPoint
class FocusFragment : Fragment() {
    
    private var _binding: FragmentFocusBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: FocusViewModel by viewModels()
    private lateinit var appAdapter: AppBlockAdapter
    
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
        
        setupRecyclerView()
        setupSearchView()
        setupDurationSeekBar()
        setupBlockLevelRadioGroup()
        setupBlockButton()
        observeViewModel()
    }
    
    /**
     * Configura o RecyclerView para exibir aplicativos.
     */
    private fun setupRecyclerView() {
        appAdapter = AppBlockAdapter(
            onAppClick = { app ->
                viewModel.toggleAppSelection(app.packageName)
            },
            selectedPackages = emptySet()
        )
        
        binding.appsRecyclerView.apply {
            adapter = appAdapter
            layoutManager = LinearLayoutManager(context)
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
     * Configura o SeekBar para definir a duração do bloqueio.
     */
    private fun setupDurationSeekBar() {
        binding.durationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Converter progresso (0-47) para horas (min 0.5h, max 24h)
                val hours = (progress / 2f) + 0.5f
                updateDurationText(hours)
                
                if (fromUser) {
                    viewModel.setBlockDuration(hours)
                }
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Configurar valor inicial (1 hora)
        binding.durationSeekBar.progress = 1
        updateDurationText(1f)
    }
    
    /**
     * Atualiza o texto de duração.
     */
    private fun updateDurationText(hours: Float) {
        val text = when {
            hours < 1 -> "${(hours * 60).roundToInt()} minutos"
            hours == 1f -> "1 hora"
            hours.toInt().toFloat() == hours -> "${hours.toInt()} horas"
            else -> {
                val h = hours.toInt()
                val m = ((hours - h) * 60).roundToInt()
                "$h horas e $m minutos"
            }
        }
        
        binding.durationText.text = text
    }
    
    /**
     * Configura o grupo de radio buttons para o nível de bloqueio.
     */
    private fun setupBlockLevelRadioGroup() {
        binding.blockLevelRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val level = when (checkedId) {
                R.id.radioSoft -> AppBlock.BlockLevel.SOFT
                R.id.radioMedium -> AppBlock.BlockLevel.MEDIUM
                R.id.radioHard -> AppBlock.BlockLevel.HARD
                else -> AppBlock.BlockLevel.MEDIUM
            }
            
            viewModel.setBlockLevel(level)
        }
        
        // Configurar valor inicial (Medium)
        binding.radioMedium.isChecked = true
    }
    
    /**
     * Configura o botão de bloqueio.
     */
    private fun setupBlockButton() {
        binding.blockButton.setOnClickListener {
            viewModel.blockSelectedApps()
        }
    }
    
    /**
     * Observa as mudanças no ViewModel e atualiza a UI.
     */
    private fun observeViewModel() {
        // Observar aplicativos filtrados
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredApps.collectLatest { apps ->
                appAdapter.submitList(apps)
                binding.emptyView.visibility = if (apps.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        // Observar aplicativos selecionados
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedApps.collect { selectedApps ->
                appAdapter.updateSelectedPackages(selectedApps)
                
                val count = selectedApps.size
                binding.selectedAppsCount.text = "Selecionados: $count"
                
                // Atualizar estado do botão de bloqueio
                binding.blockButton.isEnabled = count > 0
            }
        }
        
        // Observar carregamento
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        // Observar erros
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collect { error ->
                if (error != null) {
                    Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }
        
        // Observar sucesso de bloqueio
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.blockSuccess.collect { success ->
                if (success) {
                    Snackbar.make(
                        binding.root,
                        "Aplicativos bloqueados com sucesso!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    
                    viewModel.clearBlockSuccess()
                    
                    // Navegar de volta para a tela inicial
                    findNavController().popBackStack()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
