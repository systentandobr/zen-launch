package com.zenlauncher.di.modules

import com.zenlauncher.domain.interfaces.ChargingStateListener
import com.zenlauncher.presentation.MainActivity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoSet

/**
 * Módulo para injetar listeners de carregamento.
 */
@Module
@InstallIn(ActivityComponent::class)
abstract class ChargingListenerModule {
    
    @Binds
    @IntoSet
    abstract fun bindMainActivityAsChargingListener(
        mainActivity: MainActivity
    ): ChargingStateListener
}
