package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/** Exports each schema version so future upgrades can preserve existing notes. */
@Database(entities = [NoteEntity::class], version = 2, exportSchema = true)
@TypeConverters(NoteConverters::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
