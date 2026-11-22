package com.rudra.web3tasknotesapplication.data.mapper

import com.rudra.web3tasknotesapplication.data.local.NoteEntity
import com.rudra.web3tasknotesapplication.domain.model.Note

internal fun NoteEntity.toNote() = Note(id, title, content, createdAt, updatedAt)

internal fun Note.toEntity() = NoteEntity(id, title, content, createdAt, updatedAt)
