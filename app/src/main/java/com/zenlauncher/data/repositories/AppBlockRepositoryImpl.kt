package com.zenlauncher.data.repositories

import com.zenlauncher.data.datasources.local.db.AppBlockDao
import com.zenlauncher.data.datasources.local.db.AppBlockEntity
import com.zenlauncher.domain.entities.AppBlock
import com.zenlauncher.domain.repositories.AppBlockRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do repositório de bloqueio de aplicativos.
 */
@Singleton
class AppBlockRepositoryImpl @Inject constructor(
    private val appBlockDao: AppBlockDao
) : AppBlockRepository {
    
    /**
     * Obtém todos os bloqueios ativos.
     */
    override fun getActiveBlocks(): Flow<List<AppBlock>> {
        return appBlockDao.getActiveBlocks()
            .map { entities ->
                entities.map { entity -> entity.toDomainModel() }
            }
    }
    
    /**
     * Obtém o bloqueio para um aplicativo específico, se existir.
     */
    override fun getAppBlock(packageName: String): Flow<AppBlock?> {
        return appBlockDao.getAppBlock(packageName)
            .map { entity -> entity?.toDomainModel() }
    }
    
    /**
     * Cria ou atualiza um bloqueio para um aplicativo.
     */
    override suspend fun blockApp(
        packageName: String,
        blockedUntil: Date,
        blockLevel: AppBlock.BlockLevel
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val appBlock = AppBlock(
                packageName = packageName,
                blockedUntil = blockedUntil,
                blockLevel = blockLevel
            )
            
            val entity = AppBlockEntity.fromDomainModel(appBlock)
            appBlockDao.insertOrUpdateBlock(entity)
            
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Remove um bloqueio existente.
     */
    override suspend fun unblockApp(packageName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            appBlockDao.deleteBlockByPackage(packageName)
            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Verifica se um aplicativo está bloqueado.
     */
    override suspend fun isAppBlocked(packageName: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext appBlockDao.isAppBlocked(packageName)
    }
    
    /**
     * Estende o tempo de bloqueio de um aplicativo.
     */
    override suspend fun extendBlockTime(
        packageName: String,
        additionalTimeMs: Long
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val blockEntity = appBlockDao.getAppBlockSync(packageName)
            
            if (blockEntity != null) {
                val newBlockedUntil = blockEntity.blockedUntilTimestamp + additionalTimeMs
                appBlockDao.updateBlockTime(packageName, newBlockedUntil)
                return@withContext true
            }
            
            return@withContext false
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
    
    /**
     * Limpa bloqueios expirados.
     */
    suspend fun cleanupExpiredBlocks() = withContext(Dispatchers.IO) {
        appBlockDao.deleteExpiredBlocks()
    }
}
