package com.zenlauncher.presentation.apps

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zenlauncher.domain.entities.App
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.entities.AppCategory
import com.zenlauncher.domain.entities.AppInfo
import com.zenlauncher.domain.repositories.AppRepository
import com.zenlauncher.domain.repositories.AppBlockRepository
import com.zenlauncher.domain.usecases.GetAllAppsUseCase
import com.zenlauncher.domain.usecases.GetAppUsageStatsUseCase
import com.zenlauncher.domain.usecases.LaunchAppUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import kotlin.math.log

/**
 * ViewModel para a tela de listagem de todos os aplicativos.
 */
@HiltViewModel
class AppsViewModel @Inject constructor(
    private val getAllAppsUseCase: GetAllAppsUseCase,
    private val getAppUsageStatsUseCase: GetAppUsageStatsUseCase,
    private val launchAppUseCase: LaunchAppUseCase,
    private val appRepository: AppRepository,
    private val appBlockRepository: AppBlockRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG: String = AppsViewModel::class.simpleName.toString();

    // Estado para a lista de aplicativos
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    // Estado para o filtro de pesquisa
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Estado para indicar carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado para erros
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Lista filtrada de aplicativos (para visualização em lista)
    private val _filteredApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = _filteredApps.asStateFlow()
    
    // Categorias de aplicativos (para visualização em categorias)
    private val _categories = MutableStateFlow<List<AppCategory>>(emptyList())
    val categories: StateFlow<List<AppCategory>> = _categories.asStateFlow()
    
    // Estado para o modo de visualização (lista ou categorias)
    private val _viewMode = MutableStateFlow(ViewMode.CATEGORIES)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()
    
    init {
        loadApps()
    }
    
    /**
     * Carrega a lista de todos os aplicativos instalados.
     */
    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            getAllAppsUseCase()
                .catch { e ->
                    _error.value = e.message ?: "Erro ao carregar aplicativos"
                    _isLoading.value = false
                }
                .collect { appList ->
                    // Converter lista de App para AppInfo
                    val appInfoList = appList.mapNotNull { app ->
                        try {
                            AppInfo(
                                packageName = app.packageName,
                                label = app.appName,
                                icon = app.icon ?: context.packageManager.getApplicationIcon(app.packageName),
                                isSystemApp = app.isSystemApp,
                                isFavorite = app.isFavorite,
                                isBlocked = false // Será atualizado na próxima etapa
                            )
                        } catch (e: Exception) {
                            // Se não conseguir carregar o ícone, continue com os aplicativos que têm ícone
                            null
                        }
                    }
                    
                    // Atualizar status de bloqueio
                    updateBlockedStatus(appInfoList)
                }
        }
    }

    /**
    * 
    * @param packageName
    */
    fun blockApp(packageName: String, date: Date, blockLevel: AppBlock.BlockLevel) {
        // set app to Block
        toggleBlocked(packageName, date, blockLevel);
    }

    suspend fun isBlockedApp(packageName: String): Boolean {
        var isBlocked = appBlockRepository.isAppBlocked(packageName);
        Log.w(TAG, "isBlocked >> " + isBlocked + "packageName >> " + packageName);
        return isBlocked;
    }

    /**
     * Atualiza o filtro de pesquisa e filtra a lista de aplicativos.
     * 
     * @param query Texto para pesquisa
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterApps()
        // Também atualiza as categorias se necessário
        if (_viewMode.value == ViewMode.CATEGORIES) {
            categorizeApps()
        }
    }
    
    /**
     * Alterna entre os modos de visualização (lista ou categorias).
     */
    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == ViewMode.LIST) ViewMode.CATEGORIES else ViewMode.LIST
    }
    
    /**
     * Filtra a lista de aplicativos com base na consulta de pesquisa.
     */
    private fun filterApps() {
        val query = _searchQuery.value.lowercase().trim()
        val allApps = _apps.value
        
        if (query.isEmpty()) {
            _filteredApps.value = allApps.sortedBy { it.label }
            return
        }
        
        _filteredApps.value = allApps.filter {
            it.label.lowercase().contains(query) ||
            it.packageName.lowercase().contains(query)
        }.sortedBy { it.label }
    }
    
    /**
     * Categoriza os aplicativos em grupos.
     */
    private fun categorizeApps() {
        viewModelScope.launch {
            val allApps = if (_searchQuery.value.isNotEmpty()) {
                _filteredApps.value
            } else {
                _apps.value
            }
            
            try {
                // Obter estatísticas de uso para identificar os mais usados
                val stats = getAppUsageStatsUseCase()

                    val categories = mutableListOf<AppCategory>()
                    
                    // O tipo totalTimeUsed não está disponível em AppUsageStat, não em stats
                    val topApps = allApps.filter { app ->
                        stats.any { it.packageName == app.packageName }
                    }.sortedByDescending { app ->
                        stats.find { it.packageName == app.packageName }?.totalTimeUsed ?: 0L
                    }.take(5)
                    
                    if (topApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "top_5",
                            title = "Mais Usados",
                            apps = topApps
                        ))
                    }
                    
                    // Categoria: Produtividade
                    val productivityApps = allApps.filter { app ->
                        isProductivityApp(app.packageName)
                    }.sortedBy { it.label }
                    
                    if (productivityApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "productivity",
                            title = "Produtividade",
                            apps = productivityApps
                        ))
                    }
                    
                    // Categoria: Finanças
                    val financeApps = allApps.filter { app ->
                        isFinanceApp(app.packageName)
                    }.sortedBy { it.label }
                    
                    if (financeApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "finance",
                            title = "Finanças",
                            apps = financeApps
                        ))
                    }
                    
                    // Categoria: Entretenimento
                    val entertainmentApps = allApps.filter { app ->
                        isEntertainmentApp(app.packageName)
                    }.sortedBy { it.label }
                    
                    if (entertainmentApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "entertainment",
                            title = "Entretenimento",
                            apps = entertainmentApps
                        ))
                    }
                    
                    // Categoria: Saúde & Bem-estar
                    val healthApps = allApps.filter { app ->
                        isHealthApp(app.packageName)
                    }.sortedBy { it.label }
                    
                    if (healthApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "health",
                            title = "Saúde & Bem-estar",
                            apps = healthApps
                        ))
                    }
                    
                    // Categoria: Idiomas
                    val languageApps = allApps.filter { app ->
                        isLanguageApp(app.packageName)
                    }.sortedBy { it.label }
                    
                    if (languageApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "language",
                            title = "Idiomas",
                            apps = languageApps
                        ))
                    }
                    
                    // Categoria: Outros (qualquer app que não esteja em outra categoria)
                    val categorizedPackages = mutableSetOf<String>()
                    categories.forEach { category ->
                        category.apps.forEach { app ->
                            categorizedPackages.add(app.packageName)
                        }
                    }
                    
                    val otherApps = allApps.filter { app ->
                        !categorizedPackages.contains(app.packageName)
                    }.sortedBy { it.label }
                    
                    if (otherApps.isNotEmpty()) {
                        categories.add(AppCategory(
                            id = "other",
                            title = "Outros",
                            apps = otherApps
                        ))
                    }
                    
                    _categories.value = categories
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao obter estatísticas de uso"
            }
        }
    }
    
    /**
     * Alterna o status de favorito de um aplicativo.
     * 
     * @param packageName Nome do pacote do aplicativo
     */
    fun toggleFavorite(packageName: String) {
        viewModelScope.launch {
            try {
                // Encontrar o app na lista atual
                val app = _apps.value.find { it.packageName == packageName }
                    ?: return@launch
                
                // Alternar seu status de favorito
                val newFavoriteStatus = !app.isFavorite
                
                // Atualizar no repositório
                appRepository.setAppFavorite(packageName, newFavoriteStatus)
                
                // Atualizar listas locais
                updateAppFavoriteStatus(packageName, newFavoriteStatus)
                
            } catch (e: Exception) {
                _error.value = "Erro ao atualizar favoritos: ${e.message}"
            }
        }
    }

    /**
     * Alterna o status de favorito de um aplicativo.
     *
     * @param packageName Nome do pacote do aplicativo
     */
    private fun toggleBlocked(packageName: String, date: Date, blockLevel: AppBlock.BlockLevel) {
        viewModelScope.launch {
            try {
                // Encontrar o app na lista atual
                val app = _apps.value.find { it.packageName == packageName }
                    ?: return@launch

                // Atualizar no repositório
                appBlockRepository.blockApp(app.packageName, date, blockLevel);

            } catch (e: Exception) {
                _error.value = "Erro ao atualizar app bloqueados: ${e.message}"
            }
        }
    }
    
    /**
     * Atualiza o status de favorito de um aplicativo em todas as listas.
     */
    private fun updateAppFavoriteStatus(packageName: String, isFavorite: Boolean) {
        // Atualizar na lista principal
        _apps.value = _apps.value.map {
            if (it.packageName == packageName) it.copy(isFavorite = isFavorite) else it
        }
        
        // Atualizar na lista filtrada
        _filteredApps.value = _filteredApps.value.map {
            if (it.packageName == packageName) it.copy(isFavorite = isFavorite) else it
        }
        
        // Atualizar nas categorias
        _categories.value = _categories.value.map { category ->
            val updatedApps = category.apps.map {
                if (it.packageName == packageName) it.copy(isFavorite = isFavorite) else it
            }
            category.copy(apps = updatedApps)
        }
    }
    
    /**
     * Verifica se um aplicativo é de produtividade com base em seu pacote.
     */
    private fun isProductivityApp(packageName: String): Boolean {
        val productivityKeywords = listOf(
            "office", "docs", "sheets", "slides", "document", "word", "excel", "powerpoint",
            "calendar", "task", "note", "evernote", "notion", "trello", "asana", "todoist",
            "mail", "outlook", "gmail", "drive", "dropbox", "files", "editor", "pdf"
        )
        
        return productivityKeywords.any { keyword ->
            packageName.lowercase().contains(keyword)
        }
    }
    
    /**
     * Verifica se um aplicativo é de finanças com base em seu pacote.
     */
    private fun isFinanceApp(packageName: String): Boolean {
        val financeKeywords = listOf(
            "bank", "finance", "money", "wallet", "payment", "pix", "nubank", "itau", 
            "bradesco", "santander", "caixa", "banco", "pay", "transfer", "mercadopago", 
            "picpay", "pagseguro", "financ", "invest", "stock", "crypto", "bitcoin"
        )
        
        return financeKeywords.any { keyword ->
            packageName.lowercase().contains(keyword)
        }
    }
    
    /**
     * Verifica se um aplicativo é de entretenimento com base em seu pacote.
     */
    private fun isEntertainmentApp(packageName: String): Boolean {
        val entertainmentKeywords = listOf(
            "game", "play", "music", "spotify", "deezer", "youtube", "netflix", "prime",
            "hbo", "disney", "globoplay", "video", "tv", "movie", "show", "stream",
            "entertainment", "jogo", "game", "media", "player", "radio", "podcast"
        )
        
        return entertainmentKeywords.any { keyword ->
            packageName.lowercase().contains(keyword)
        }
    }
    
    /**
     * Verifica se um aplicativo é de saúde e bem-estar com base em seu pacote.
     */
    private fun isHealthApp(packageName: String): Boolean {
        val healthKeywords = listOf(
            "health", "fitness", "workout", "exercise", "meditation", "yoga", "sleep",
            "nutrition", "diet", "food", "water", "weight", "steps", "run", "walking",
            "saude", "bem-estar", "academia", "treino", "exercicio", "meditacao", "sono"
        )
        
        return healthKeywords.any { keyword ->
            packageName.lowercase().contains(keyword)
        }
    }
    
    /**
     * Verifica se um aplicativo é de aprendizado de idiomas com base em seu pacote.
     */
    private fun isLanguageApp(packageName: String): Boolean {
        val languageKeywords = listOf(
            "language", "learn", "duolingo", "babbel", "rosetta", "translate", "dictionary",
            "idioma", "lingua", "tradutor", "dicionario", "babel", "ingles", "english", 
            "spanish", "espanhol", "french", "frances", "german", "alemao"
        )
        
        return languageKeywords.any { keyword ->
            packageName.lowercase().contains(keyword)
        }
    }
    
    /**
     * Lança um aplicativo.
     * 
     * @param packageName Nome do pacote do aplicativo
     */
    fun launchApp(packageName: String) {
        viewModelScope.launch {
            launchAppUseCase(packageName)
        }
    }
    
    /**
     * Limpa o estado de erro.
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Modos de visualização para a tela de aplicativos.
     */
    enum class ViewMode {
        LIST,       // Visualização em lista alfabética
        CATEGORIES  // Visualização em categorias
    }
    
    /**
     * Atualiza o status de bloqueio para uma lista de aplicativos.
     */
    private suspend fun updateBlockedStatus(appList: List<AppInfo>) {
        try {
            appBlockRepository.getActiveBlocks().collect { blockedApps ->
                val blockedPackages = blockedApps.filter { it.isActive() }.map { it.packageName }.toSet()
                
                // Atualizar status de bloqueio em todas as listas
                val updatedApps = appList.map { app ->
                    app.copy(isBlocked = blockedPackages.contains(app.packageName))
                }
                
                _apps.value = updatedApps
                filterApps()
                categorizeApps()
                _isLoading.value = false
            }
        } catch (e: Exception) {
            // Em caso de erro, continuar sem o status de bloqueio
            Timber.w(e, "Erro ao verificar status de bloqueio")
            _apps.value = appList
            filterApps()
            categorizeApps()
            _isLoading.value = false
        }
    }
}
