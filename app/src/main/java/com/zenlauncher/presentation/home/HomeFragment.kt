package com.zenlauncher.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zenlauncher.R
import com.zenlauncher.databinding.FragmentHomeBinding
import com.zenlauncher.domain.entities.App
import com.zenlauncher.presentation.home.adapters.FavoriteAppsAdapter
import com.zenlauncher.presentation.home.adapters.ImageAppsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlertDialog
import com.zenlauncher.presentation.home.ShortcutType

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
    private lateinit var favoriteAppsAdapter: FavoriteAppsAdapter
    private lateinit var imageAppsAdapter: ImageAppsAdapter

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
        setupShortcutsBar()
        // setupAppDrawerButton()
        // setupFocusModeButton()
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
    
    /**
     * Configura a exibição de aplicativos favoritos.
     */
    private fun setupFavoriteApps() {
        favoriteAppsAdapter = FavoriteAppsAdapter(
            onAppClick = { app ->
                viewModel.launchApp(app.packageName)
            },
            onAppLongClick = { app ->
                // Ao fazer um pressionar longo em um app favorito, podemos permitir a remoção
                Snackbar.make(
                    binding.root,
                    "Remover ${app.appName} dos favoritos?",
                    Snackbar.LENGTH_LONG
                ).setAction("Remover") {
                    // Implementar ação de remoção
                    // Será adicionado posteriormente
                }.show()
            }
        )
        
        binding.favoritesRecyclerView.apply {
            adapter = favoriteAppsAdapter
            layoutManager = LinearLayoutManager(
                context, 
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }
    
    private fun setupShortcutsBar() {
        // Observa os atalhos e configura os botões
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shortcuts.collectLatest { shortcuts ->
                val phone = shortcuts.find { it.type == ShortcutType.PHONE }
                val camera = shortcuts.find { it.type == ShortcutType.CAMERA }

                binding.phoneButton.setOnClickListener {
                    phone?.let { viewModel.launchApp(it.packageName) }
                }
                binding.cameraButton.setOnClickListener {
                    camera?.let { viewModel.launchApp(it.packageName) }
                }

                binding.phoneButton.setOnLongClickListener {
                    showShortcutPickerDialog(ShortcutType.PHONE)
                    true
                }
                binding.cameraButton.setOnLongClickListener {
                    showShortcutPickerDialog(ShortcutType.CAMERA)
                    true
                }
            }
        }
    }

    private fun showShortcutPickerDialog(type: ShortcutType) {
        viewModel.getCompatibleApps(type) { filtered ->
            if (filtered.isEmpty()) return@getCompatibleApps
            val labels = filtered.map { it.appName }.toTypedArray()
            AlertDialog.Builder(requireContext())
                .setTitle("Escolha o app para o atalho")
                .setItems(labels) { _, which ->
                    val app = filtered[which]
                    viewModel.updateShortcut(type, app.packageName, app.appName)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    private fun setupAppDrawerButton() {
        // binding.allAppsButton.setOnClickListener {
        //     findNavController().navigate(R.id.action_home_to_apps)
        // }
    }
    
    private fun setupFocusModeButton() {
        // binding.focusModeButton.setOnClickListener {
        //    findNavController().navigate(R.id.action_home_to_focus)
        // }
    }
    
    private fun observeData() {
        // Observar aplicativos favoritos
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteApps.collectLatest { apps ->
                favoriteAppsAdapter.submitList(apps)
                
                // Atualizar visibilidade da seção de favoritos
                val favoritesSectionVisible = apps.isNotEmpty()
                // binding.favoritesTitle.visibility = if (favoritesSectionVisible) View.VISIBLE else View.GONE
                binding.favoritesRecyclerView.visibility = if (favoritesSectionVisible) View.VISIBLE else View.GONE
            }
        }
        
        // Observar estatísticas de uso
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.usageStats.collect { stats ->
                // Atualizar UI com estatísticas de uso
                // Será implementado posteriormente
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
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
