package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Stores the fields supported by the first database schema. */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
)
