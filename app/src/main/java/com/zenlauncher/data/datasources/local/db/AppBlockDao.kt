package com.zenlauncher.data.datasources.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operações de bloqueio de aplicativos no banco de dados.
 */
@Dao
interface AppBlockDao {
    
    /**
     * Obtém todos os bloqueios ativos (onde blockedUntilTimestamp > tempo atual).
     */
    @Query("SELECT * FROM app_blocks WHERE blockedUntilTimestamp > :currentTimestamp")
    fun getActiveBlocks(currentTimestamp: Long = System.currentTimeMillis()): Flow<List<AppBlockEntity>>
    
    /**
     * Obtém o bloqueio para um aplicativo específico.
     */
    @Query("SELECT * FROM app_blocks WHERE packageName = :packageName")
    fun getAppBlock(packageName: String): Flow<AppBlockEntity?>
    
    /**
     * Obtém o bloqueio para um aplicativo específico (versão síncrona).
     */
    @Query("SELECT * FROM app_blocks WHERE packageName = :packageName")
    suspend fun getAppBlockSync(packageName: String): AppBlockEntity?
    
    /**
     * Verifica se um aplicativo está bloqueado (blockedUntilTimestamp > tempo atual).
     */
    @Query("SELECT COUNT(*) > 0 FROM app_blocks WHERE packageName = :packageName AND blockedUntilTimestamp > :currentTimestamp")
    suspend fun isAppBlocked(packageName: String, currentTimestamp: Long = System.currentTimeMillis()): Boolean
    
    /**
     * Insere ou atualiza um bloqueio.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBlock(appBlock: AppBlockEntity)
    
    /**
     * Remove um bloqueio.
     */
    @Delete
    suspend fun deleteBlock(appBlock: AppBlockEntity)
    
    /**
     * Remove um bloqueio pelo nome do pacote.
     */
    @Query("DELETE FROM app_blocks WHERE packageName = :packageName")
    suspend fun deleteBlockByPackage(packageName: String)
    
    /**
     * Atualiza o tempo de bloqueio para um aplicativo.
     */
    @Query("UPDATE app_blocks SET blockedUntilTimestamp = :blockedUntilTimestamp WHERE packageName = :packageName")
    suspend fun updateBlockTime(packageName: String, blockedUntilTimestamp: Long)
    
    /**
     * Remove todos os bloqueios expirados.
     */
    @Query("DELETE FROM app_blocks WHERE blockedUntilTimestamp <= :currentTimestamp")
    suspend fun deleteExpiredBlocks(currentTimestamp: Long = System.currentTimeMillis())
}
