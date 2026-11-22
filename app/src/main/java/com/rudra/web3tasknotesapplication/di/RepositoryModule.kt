package com.rudra.web3tasknotesapplication.di

import com.rudra.web3tasknotesapplication.data.repository.NotesRepository
import com.rudra.web3tasknotesapplication.data.repository.NotesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Exposes the data layer through its repository interface. */
@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindNotesRepository(implementation: NotesRepositoryImpl): NotesRepository
}
