package com.zenlauncher.data.datasources.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zenlauncher.domain.entities.AppBlock

/**
 * Entidade Room para armazenar informações de bloqueio de aplicativos.
 */
@Entity(tableName = "app_blocks")
data class AppBlockEntity(
    @PrimaryKey
    val packageName: String,
    val blockedUntilTimestamp: Long,
    val blockLevel: String // SOFT, MEDIUM, HARD
) {
    /**
     * Converte a entidade do banco de dados para a entidade de domínio.
     */
    fun toDomainModel(): AppBlock {
        return AppBlock(
            packageName = packageName,
            blockedUntil = java.util.Date(blockedUntilTimestamp),
            blockLevel = when (blockLevel) {
                "SOFT" -> AppBlock.BlockLevel.SOFT
                "MEDIUM" -> AppBlock.BlockLevel.MEDIUM
                "HARD" -> AppBlock.BlockLevel.HARD
                else -> AppBlock.BlockLevel.MEDIUM // Valor padrão
            }
        )
    }
    
    companion object {
        /**
         * Converte a entidade de domínio para a entidade do banco de dados.
         */
        fun fromDomainModel(appBlock: AppBlock): AppBlockEntity {
            return AppBlockEntity(
                packageName = appBlock.packageName,
                blockedUntilTimestamp = appBlock.blockedUntil.time,
                blockLevel = appBlock.blockLevel.name
            )
        }
    }
}
