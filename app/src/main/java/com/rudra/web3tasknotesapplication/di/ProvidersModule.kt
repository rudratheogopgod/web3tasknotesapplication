package com.rudra.web3tasknotesapplication.di

import com.rudra.web3tasknotesapplication.core.dispatchers.DefaultDispatcherProvider
import com.rudra.web3tasknotesapplication.core.dispatchers.DispatcherProvider
import com.rudra.web3tasknotesapplication.core.time.SystemTimeProvider
import com.rudra.web3tasknotesapplication.core.time.TimeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Supplies production implementations for the repository's clock and dispatcher. */
@Module
@InstallIn(SingletonComponent::class)
object ProvidersModule {
    @Provides
    fun provideTimeProvider(): TimeProvider = SystemTimeProvider

    @Provides
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider
}
