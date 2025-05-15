package com.zenlauncher.di.modules

import com.zenlauncher.domain.interfaces.ChargingStateListener
import com.zenlauncher.presentation.navigation.NavigationChargingListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer ouvintes de estado de carregamento.
 */
@Module
@InstallIn(SingletonComponent::class)
object ChargingModule {
    
    /**
     * Fornece o NavigationChargingListener como um ouvinte de estado de carregamento.
     */
    @Provides
    @IntoSet
    @Singleton
    fun provideNavigationChargingListener(listener: NavigationChargingListener): ChargingStateListener {
        return listener
    }
}
