package com.rudra.web3tasknotesapplication.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.NoteColor

/** The persisted note, including default metadata for rows migrated from v1. */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    @ColumnInfo(defaultValue = "0") val isPinned: Boolean = false,
    @ColumnInfo(defaultValue = "'DEFAULT'") val colorId: NoteColor = NoteColor.DEFAULT,
    @ColumnInfo(defaultValue = "'QUICK_NOTE'") val category: NoteCategory = NoteCategory.QUICK_NOTE,
)
