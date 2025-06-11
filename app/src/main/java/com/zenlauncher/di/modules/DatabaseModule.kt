package com.zenlauncher.di.modules

import android.content.Context
import androidx.room.Room
import com.zenlauncher.data.datasources.local.db.AppBlockDao
import com.zenlauncher.data.datasources.local.db.MindfulLauncherDatabase
import com.zenlauncher.data.repositories.AppBlockRepositoryImpl
import com.zenlauncher.domain.repositories.AppBlockRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer dependências relacionadas ao banco de dados.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): MindfulLauncherDatabase {
        return Room.databaseBuilder(
            context,
            MindfulLauncherDatabase::class.java,
            MindfulLauncherDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideAppBlockDao(database: MindfulLauncherDatabase): AppBlockDao {
        return database.appBlockDao()
    }
    
    @Provides
    @Singleton
    fun provideAppBlockRepository(
        appBlockDao: AppBlockDao
    ): AppBlockRepository {
        return AppBlockRepositoryImpl(appBlockDao)
    }
}
