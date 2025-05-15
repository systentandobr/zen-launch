package com.zenlauncher.domain.repositories

import com.zenlauncher.domain.entities.App
import kotlinx.coroutines.flow.Flow

/**
 * Repositório para gerenciar aplicativos instalados.
 */
interface AppRepository {
    
    /**
     * Obtém todos os aplicativos instalados.
     * @return Flow com lista de aplicativos
     */
    fun getAllApps(): Flow<List<App>>
    
    /**
     * Obtém apenas aplicativos favoritos.
     * @return Flow com lista de aplicativos favoritos
     */
    fun getFavoriteApps(): Flow<List<App>>
    
    /**
     * Adiciona ou remove um aplicativo dos favoritos.
     * @param packageName Nome do pacote do aplicativo
     * @param isFavorite Novo status de favorito
     */
    suspend fun setAppFavorite(packageName: String, isFavorite: Boolean)
    
    /**
     * Obtém informações de um aplicativo específico.
     * @param packageName Nome do pacote do aplicativo
     * @return App ou null se não encontrado
     */
    suspend fun getAppByPackage(packageName: String): App?
    
    /**
     * Lança um aplicativo.
     * @param packageName Nome do pacote do aplicativo
     * @return true se o aplicativo foi lançado com sucesso
     */
    suspend fun launchApp(packageName: String): Boolean
    
    /**
     * Busca aplicativos pelo nome.
     * @param query Texto para pesquisa
     * @return Lista de aplicativos que correspondem à consulta
     */
    suspend fun searchApps(query: String): List<App>
}
