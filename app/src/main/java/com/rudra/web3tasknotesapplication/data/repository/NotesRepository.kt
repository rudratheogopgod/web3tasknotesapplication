package com.rudra.web3tasknotesapplication.data.repository

import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteSortOrder
import kotlinx.coroutines.flow.Flow

/** Exposes Room-backed state without exposing persistence types to callers. */
interface NotesRepository {
    fun observeAll(query: String = "", order: NoteSortOrder = NoteSortOrder.LAST_UPDATED): Flow<List<Note>>
    /** Emits notes newest by creation time. An omitted limit returns every note. */
    fun observeCreated(limit: Int = -1): Flow<List<Note>>
    fun observeById(id: Long): Flow<Note?>

    /** Saves a new note when [id] is null, or updates an existing note atomically. */
    suspend fun save(id: Long?, title: String, content: String): Result<Note>

    /** Deletes the latest persisted row and returns its snapshot for Undo. */
    suspend fun delete(id: Long): Result<Note>

    /** Reinserts a deleted note with its original identity and timestamps. */
    suspend fun restore(note: Note): Result<Unit>
}
