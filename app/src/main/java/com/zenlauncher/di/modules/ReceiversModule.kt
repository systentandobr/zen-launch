package com.zenlauncher.di.modules

import android.content.Context
import com.zenlauncher.data.receivers.BootCompletedReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para fornecer os receivers.
 */
@Module
@InstallIn(SingletonComponent::class)
object ReceiversModule {
    
    @Provides
    @Singleton
    fun provideBootCompletedReceiver(@ApplicationContext context: Context): BootCompletedReceiver {
        return BootCompletedReceiver()
    }
}
