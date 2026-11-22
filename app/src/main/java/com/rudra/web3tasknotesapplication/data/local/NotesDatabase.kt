package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** Exports the initial schema so future upgrades can preserve existing notes. */
@Database(entities = [NoteEntity::class], version = 1, exportSchema = true)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
