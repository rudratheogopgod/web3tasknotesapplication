package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Keeps queries and ordering rules close to the persisted data. */
@Dao
interface NoteDao {
    @Query("""
        SELECT * FROM notes
        WHERE :pattern IS NULL OR title LIKE :pattern ESCAPE '!' OR content LIKE :pattern ESCAPE '!'
        ORDER BY updatedAt DESC, id DESC
    """)
    fun observeAll(pattern: String? = null): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes
        WHERE :pattern IS NULL OR title LIKE :pattern ESCAPE '!' OR content LIKE :pattern ESCAPE '!'
        ORDER BY title COLLATE NOCASE ASC, updatedAt DESC, id DESC
    """)
    fun observeByTitle(pattern: String?): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY createdAt DESC, id DESC LIMIT :limit")
    fun observeCreated(limit: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observeById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getById(id: Long): NoteEntity?

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity): Int

    @Delete
    suspend fun delete(note: NoteEntity): Int
}
