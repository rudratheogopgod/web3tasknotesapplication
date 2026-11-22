package com.rudra.web3tasknotesapplication.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.rudra.web3tasknotesapplication.data.local.NoteDao
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.rudra.web3tasknotesapplication.R
import com.rudra.web3tasknotesapplication.core.dispatchers.DispatcherProvider
import com.rudra.web3tasknotesapplication.core.time.TimeProvider
import com.rudra.web3tasknotesapplication.data.local.NotesDatabase
import com.rudra.web3tasknotesapplication.data.mapper.toEntity
import com.rudra.web3tasknotesapplication.data.mapper.toNote
import com.rudra.web3tasknotesapplication.domain.model.Note
import com.rudra.web3tasknotesapplication.domain.model.NoteOptions
import com.rudra.web3tasknotesapplication.domain.model.NoteSortOrder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** Runs each read-and-write save in one transaction so comparisons use the latest row. */
class NotesRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    private val database: NotesDatabase,
    private val dispatchers: DispatcherProvider,
    time: TimeProvider,
    @ApplicationContext context: Context,
) : NotesRepository {
    private val appContext = context.applicationContext
    private val rules = NoteSaveRules(time) { appContext.getString(R.string.untitled_note) }

    override fun observeAll(query: String, order: NoteSortOrder): Flow<List<Note>> {
        val pattern = noteSearchPattern(query)
        val rows = when (order) {
            NoteSortOrder.LAST_UPDATED -> dao.observeAll(pattern)
            NoteSortOrder.TITLE -> dao.observeByTitle(pattern)
        }
        return rows.map { entities -> entities.map { it.toNote() } }
    }

    override fun observeCreated(limit: Int) = dao.observeCreated(limit).map { rows -> rows.map { it.toNote() } }

    override fun observeById(id: Long) = dao.observeById(id).map { it?.toNote() }

    override suspend fun save(
        id: Long?,
        title: String,
        content: String,
        options: NoteOptions?,
    ): Result<Note> = write {
        database.withTransaction {
            val existing = id?.let { checkNotNull(dao.getById(it)).toNote() }
            val note = rules.prepare(existing, title, content, options)
            when {
                existing == null -> note.copy(id = dao.insert(note.toEntity()))
                note === existing -> existing
                else -> {
                    check(dao.update(note.toEntity()) == 1)
                    note
                }
            }
        }
    }

    override suspend fun delete(id: Long): Result<Note> = write {
        database.withTransaction {
            val row = checkNotNull(dao.getById(id))
            check(dao.delete(row) == 1)
            row.toNote()
        }
    }

    override suspend fun restore(note: Note): Result<Unit> = write {
        dao.insert(note.toEntity())
    }

    private suspend fun <T> write(operation: suspend () -> T): Result<T> =
        withContext(dispatchers.io()) {
            try {
                Result.success(operation())
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                Result.failure(failure)
            }
        }
}
