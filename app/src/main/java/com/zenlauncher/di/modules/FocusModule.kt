package com.zenlauncher.di.modules

import com.zenlauncher.data.repositories.FocusSessionRepositoryImpl
import com.zenlauncher.domain.repositories.FocusSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Dagger Hilt para injeção de dependências relacionadas ao sistema de foco.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FocusModule {
    
    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(
        focusSessionRepositoryImpl: FocusSessionRepositoryImpl
    ): FocusSessionRepository
}
