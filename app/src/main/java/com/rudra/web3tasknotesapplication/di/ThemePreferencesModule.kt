package com.rudra.web3tasknotesapplication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.themePreferences by preferencesDataStore(name = "theme_preferences")

/** Shares one DataStore instance across activity recreation. */
@Module
@InstallIn(SingletonComponent::class)
internal object ThemePreferencesModule {
    @Provides
    @Singleton
    fun provideThemePreferences(@ApplicationContext context: Context): DataStore<Preferences> =
        context.themePreferences
}
