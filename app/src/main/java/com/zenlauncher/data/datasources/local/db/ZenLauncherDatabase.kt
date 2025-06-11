package com.zenlauncher.data.datasources.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Banco de dados Room para o MindfulLauncher.
 */
@Database(
    entities = [AppBlockEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MindfulLauncherDatabase : RoomDatabase() {
    
    /**
     * Acesso ao DAO de bloqueio de aplicativos.
     */
    abstract fun appBlockDao(): AppBlockDao
    
    companion object {
        const val DATABASE_NAME = "zen_launcher_db"
    }
}
