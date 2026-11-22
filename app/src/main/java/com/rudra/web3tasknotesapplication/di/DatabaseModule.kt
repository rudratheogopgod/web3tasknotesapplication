package com.rudra.web3tasknotesapplication.di

import android.content.Context
import androidx.room.Room
import com.rudra.web3tasknotesapplication.data.local.MIGRATION_1_2
import com.rudra.web3tasknotesapplication.data.local.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Shares one database across all consumers in the app process. */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NotesDatabase =
        Room.databaseBuilder(context, NotesDatabase::class.java, "notes.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideNoteDao(database: NotesDatabase) = database.noteDao()
}
