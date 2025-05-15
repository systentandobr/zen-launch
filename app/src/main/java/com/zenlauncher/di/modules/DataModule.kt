package com.zenlauncher.di.modules

import android.content.Context
import com.zenlauncher.data.datasources.local.AppPreferencesDataSource
import com.zenlauncher.data.datasources.system.AppSystemDataSource
import com.zenlauncher.data.datasources.system.UsageStatsDataSource
import com.zenlauncher.data.repositories.AppRepositoryImpl
import com.zenlauncher.data.repositories.UsageStatsRepositoryImpl
import com.zenlauncher.domain.repositories.AppRepository
import com.zenlauncher.domain.repositories.UsageStatsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer dependências da camada de dados.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    // Fontes de Dados
    
    @Provides
    @Singleton
    fun provideAppSystemDataSource(
        @ApplicationContext context: Context
    ): AppSystemDataSource {
        return AppSystemDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideAppPreferencesDataSource(
        @ApplicationContext context: Context
    ): AppPreferencesDataSource {
        return AppPreferencesDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideUsageStatsDataSource(
        @ApplicationContext context: Context
    ): UsageStatsDataSource {
        return UsageStatsDataSource(context)
    }
    
    // Repositórios
    
    @Provides
    @Singleton
    fun provideAppRepository(
        appSystemDataSource: AppSystemDataSource,
        appPreferencesDataSource: AppPreferencesDataSource
    ): AppRepository {
        return AppRepositoryImpl(appSystemDataSource, appPreferencesDataSource)
    }
    
    @Provides
    @Singleton
    fun provideUsageStatsRepository(
        usageStatsDataSource: UsageStatsDataSource
    ): UsageStatsRepository {
        return UsageStatsRepositoryImpl(usageStatsDataSource)
    }
}
