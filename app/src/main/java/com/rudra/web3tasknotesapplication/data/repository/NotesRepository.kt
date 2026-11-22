package com.rudra.web3tasknotesapplication.data.repository

import com.rudra.web3tasknotesapplication.domain.model.Note
import kotlinx.coroutines.flow.Flow

/** Exposes Room-backed state without exposing persistence types to callers. */
interface NotesRepository {
    fun observeAll(): Flow<List<Note>>
    fun observeById(id: Long): Flow<Note?>

    /** Saves a new note when [id] is null, or updates an existing note atomically. */
    suspend fun save(id: Long?, title: String, content: String): Result<Note>

    suspend fun delete(note: Note): Result<Unit>
}
