package com.zenlauncher.domain.repositories

import com.zenlauncher.domain.entities.AppBlock
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repositório para gerenciar bloqueios de aplicativos.
 */
interface AppBlockRepository {
    
    /**
     * Obtém todos os bloqueios ativos.
     * @return Flow com lista de bloqueios ativos
     */
    fun getActiveBlocks(): Flow<List<AppBlock>>
    
    /**
     * Obtém o bloqueio para um aplicativo específico, se existir.
     * @param packageName Nome do pacote do aplicativo
     * @return Flow com o bloqueio ou null se não estiver bloqueado
     */
    fun getAppBlock(packageName: String): Flow<AppBlock?>
    
    /**
     * Cria ou atualiza um bloqueio para um aplicativo.
     * @param packageName Nome do pacote do aplicativo
     * @param blockedUntil Data até quando o aplicativo deve permanecer bloqueado
     * @param blockLevel Nível de rigor do bloqueio
     * @return true se a operação foi bem-sucedida
     */
    suspend fun blockApp(
        packageName: String, 
        blockedUntil: Date, 
        blockLevel: AppBlock.BlockLevel
    ): Boolean
    
    /**
     * Remove um bloqueio existente.
     * @param packageName Nome do pacote do aplicativo
     * @return true se a operação foi bem-sucedida
     */
    suspend fun unblockApp(packageName: String): Boolean
    
    /**
     * Verifica se um aplicativo está bloqueado.
     * @param packageName Nome do pacote do aplicativo
     * @return true se o aplicativo estiver bloqueado
     */
    suspend fun isAppBlocked(packageName: String): Boolean
    
    /**
     * Estende o tempo de bloqueio de um aplicativo.
     * @param packageName Nome do pacote do aplicativo
     * @param additionalTimeMs Tempo adicional em milissegundos
     * @return true se a operação foi bem-sucedida
     */
    suspend fun extendBlockTime(packageName: String, additionalTimeMs: Long): Boolean
}
