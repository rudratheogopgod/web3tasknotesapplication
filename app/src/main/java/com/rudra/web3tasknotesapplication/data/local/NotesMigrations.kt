package com.rudra.web3tasknotesapplication.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Adds editor metadata in place so every existing note keeps its ID and content. */
internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE notes ADD COLUMN colorId TEXT NOT NULL DEFAULT 'DEFAULT'")
        db.execSQL("ALTER TABLE notes ADD COLUMN category TEXT NOT NULL DEFAULT 'QUICK_NOTE'")
    }
}
