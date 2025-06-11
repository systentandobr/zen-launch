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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.zenlauncher.R
import com.zenlauncher.databinding.FragmentAppsBinding
import com.zenlauncher.domain.entities.AppInfo
import com.zenlauncher.domain.entities.AppInfoParcelable
import com.zenlauncher.presentation.common.adapters.AppAdapter
import com.zenlauncher.presentation.apps.adapters.CategoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.provider.Settings
import com.zenlauncher.presentation.focus.dialog.BlockConfirmationDialog
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.presentation.common.dialogs.AppContextMenuDialog
import com.zenlauncher.presentation.common.dialogs.BlockedAppInfoDialog
import com.zenlauncher.presentation.common.views.AlphabeticalFastScroller

/**
 * Fragment para exibir todos os aplicativos instalados.
 */
@AndroidEntryPoint
class AppsFragment : Fragment() {
    
    private var _binding: FragmentAppsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: AppsViewModel by viewModels()
    private lateinit var appAdapter: AppAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    
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
        
        setupRecyclerViews()
        setupSearchView()
        setupFilterButton()
        observeViewModel()
        
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadApps()
        }
    }

    fun setBookmark(app: AppInfo) {
        viewModel.toggleFavorite(app.packageName)
        val message = if (app.isFavorite) {
            "Removido dos favoritos: ${app.label}"
        } else {
            "Adicionado aos favoritos: ${app.label}"
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
    
    fun openDialogChoice(app: AppInfo) {
        val dialog = AppContextMenuDialog.newInstance(app)
        dialog.setOnMenuActionListener(object : AppContextMenuDialog.OnMenuActionListener {
            override fun onToggleTimeReminder(enabled: Boolean) {
                // TODO: Implementar persistência do lembrete de tempo
                Snackbar.make(
                    binding.root, 
                    if (enabled) "Lembrete de tempo ativado para ${app.displayLabel}" 
                    else "Lembrete de tempo desativado para ${app.displayLabel}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            
            override fun onToggleFavorite() {
                setBookmark(app)
            }
            
            override fun onBlockApp() {
                showBlockDialog(app)
            }
            
            override fun onRenameApp() {
                showRenameDialog(app)
            }
            
            override fun onHideApp() {
                hideApp(app)
            }
            
            override fun onMoveToFolder() {
                moveToFolder(app)
            }
            
            override fun onUninstallApp() {
                uninstallApp(app)
            }
            
            override fun onShowAppInfo() {
                showAppInfo(app)
            }
        })
        dialog.show(parentFragmentManager, "AppContextMenuDialog")
    }
    
    fun showBlockDialog(app: AppInfo) {
        val dialog = BlockConfirmationDialog.newInstance(
            1, // apenas 1 app
            1.0f, // duração padrão 1 hora
            AppBlock.BlockLevel.MEDIUM // nível padrão
        )
        dialog.setOnConfirmListener { blockLevel ->
            // Aqui você pode chamar o método do ViewModel para bloquear o app
            // Exemplo: viewModel.blockApp(app.packageName, blockLevel)
            Snackbar.make(binding.root, "App bloqueado: ${app.label} ($blockLevel)", Snackbar.LENGTH_SHORT).show()
        }
        dialog.show(parentFragmentManager, "BlockConfirmationDialog")
    }
    
    fun showRenameDialog(app: AppInfo) {
        val input = android.widget.EditText(requireContext())
        input.setText(app.label)
        AlertDialog.Builder(requireContext())
            .setTitle("Renomear app")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newName = input.text.toString()
                // Chame o ViewModel ou lógica para renomear
                // viewModel.renameApp(app.packageName, newName)
                Snackbar.make(binding.root, "App renomeado para: $newName", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    fun hideApp(app: AppInfo) {
        // viewModel.hideApp(app.packageName)
        Snackbar.make(binding.root, "App ocultado: ${app.label}", Snackbar.LENGTH_SHORT).show()
    }
    
    fun moveToFolder(app: AppInfo) {
        // Aqui você pode abrir um dialog para escolher a pasta
        Snackbar.make(binding.root, "Mover para pasta: ${app.label}", Snackbar.LENGTH_SHORT).show()
    }
    
    fun uninstallApp(app: AppInfo) {
        val intent = Intent(Intent.ACTION_DELETE)
        intent.data = android.net.Uri.parse("package:" + app.packageName)
        startActivity(intent)
    }
    
    fun showAppInfo(app: AppInfo) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = android.net.Uri.parse("package:" + app.packageName)
        startActivity(intent)
    }
    
    /**
     * Mostra as informações de um app bloqueado (tempo restante, motivo, etc).
     */
    fun showBlockedAppInfo(app: AppInfo) {
        val appInfoParcelable = AppInfoParcelable.fromAppInfo(app)
        val dialog = BlockedAppInfoDialog.newInstance(appInfoParcelable)
        dialog.show(parentFragmentManager, "BlockedAppInfoDialog")
    }

    /**
     * Configura os RecyclerViews para exibir os aplicativos.
     */
    private fun setupRecyclerViews() {
        // Configurar o adaptador para a visualização em lista
        appAdapter = AppAdapter(
            onAppClick = { app ->
                // TODO: consultar as regras de bloqueio do APP
                viewModel.launchApp(app.packageName)
            },
            onAppLongClick = { app ->
                openDialogChoice(app)
            },
            onBlockedAppLongClick = { app ->
                showBlockedAppInfo(app)
            }
        )
        
        binding.appsRecyclerView.apply {
            adapter = appAdapter
            layoutManager = LinearLayoutManager(context) // Mudar para lista vertical
        }
        
        // Configurar rolagem alfabética rápida
        binding.alphabeticalScroller.apply {
            attachToRecyclerView(binding.appsRecyclerView)
            setOnLetterSelectedListener(object : AlphabeticalFastScroller.OnLetterSelectedListener {
                override fun onLetterSelected(letter: Char) {
                    scrollToLetter(letter)
                }
            })
        }
        
        // Configurar o adaptador para a visualização em categorias
        categoryAdapter = CategoryAdapter(
            onAppClick = { app ->
                viewModel.launchApp(app.packageName)
            },
            onAppLongClick = { app ->
                // Implementar a mesma ação para pressionar longo
                viewModel.toggleFavorite(app.packageName)
                
                val message = if (app.isFavorite) {
                    "Removido dos favoritos: ${app.label}"
                } else {
                    "Adicionado aos favoritos: ${app.label}"
                }
                
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        )
        
        binding.categoriesRecyclerView.apply {
            adapter = categoryAdapter
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
     * Configura o botão de filtro para alternar entre visualizações.
     */
    private fun setupFilterButton() {
        binding.filterButton.setOnClickListener {
            viewModel.toggleViewMode()
        }
    }
    
    /**
     * Observa as mudanças no ViewModel e atualiza a UI.
     */
    private fun observeViewModel() {
        // Observar a lista filtrada de aplicativos (para visualização em lista)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredApps.collectLatest { apps ->
                appAdapter.submitList(apps)
                updateEmptyViewVisibility(apps.isEmpty())
            }
        }
        
        // Observar as categorias de aplicativos (para visualização em categorias)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collectLatest { categories ->
                categoryAdapter.submitList(categories)
                updateEmptyViewVisibility(categories.isEmpty())
            }
        }
        
        // Observar o modo de visualização
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewMode.collect { viewMode ->
                updateViewMode(viewMode)
            }
        }
        
        // Observar o estado de carregamento
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
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
    
    /**
     * Atualiza a visibilidade da visualização vazia.
     */
    private fun updateEmptyViewVisibility(isEmpty: Boolean) {
        binding.emptyView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    
    /**
     * Atualiza o modo de visualização (lista ou categorias).
     */
    private fun updateViewMode(viewMode: AppsViewModel.ViewMode) {
        when (viewMode) {
            AppsViewModel.ViewMode.LIST -> {
                binding.categoriesRecyclerView.visibility = View.GONE
                binding.swipeRefresh.visibility = View.VISIBLE
                binding.alphabeticalScroller.visibility = View.VISIBLE
                binding.filterButton.setImageResource(android.R.drawable.ic_menu_sort_by_size)
            }
            AppsViewModel.ViewMode.CATEGORIES -> {
                binding.categoriesRecyclerView.visibility = View.VISIBLE
                binding.swipeRefresh.visibility = View.GONE
                binding.alphabeticalScroller.visibility = View.GONE
                binding.filterButton.setImageResource(android.R.drawable.ic_menu_sort_alphabetically)
            }
        }
    }
    
    /**
     * Rola a lista para a primeira app que começa com a letra especificada
     */
    private fun scrollToLetter(letter: Char) {
        val apps = appAdapter.currentList
        val position = apps.indexOfFirst { 
            it.displayLabel.firstOrNull()?.uppercaseChar() == letter 
        }
        
        if (position >= 0) {
            (binding.appsRecyclerView.layoutManager as? LinearLayoutManager)
                ?.scrollToPositionWithOffset(position, 0)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
