package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Room
import com.rudra.web3tasknotesapplication.data.repository.noteSearchPattern
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {
    private lateinit var database: NotesDatabase
    private lateinit var dao: NoteDao

    @Before
    fun openDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java).build()
        dao = database.noteDao()
    }

    @Test
    fun searchMatchesTitleAndBodyWithLiteralWildcardsAndDeterministicSort() = runTest {
        suspend fun insert(title: String, content: String, updated: Long) =
            dao.insert(NoteEntity(title = title, content = content, createdAt = 1, updatedAt = updated))

        val first = insert("alpha 50%_!", "", 4)
        val second = insert("ALPHA 50%_!", "", 4)
        val older = insert("Alpha 50%_!", "", 3)
        val body = insert("Zebra", "alpha 50%_!", 9)
        val decoy = insert("Alpha 50xx!", "", 10)
        val literal = noteSearchPattern("  aLpHa 50%_!  ")
        assertEquals(listOf(body, second, first, older), dao.observeAll(literal).first().map { it.id })
        assertEquals(listOf(second, first, older, body), dao.observeByTitle(literal).first().map { it.id })
        assertEquals(listOf(decoy, body, second, first, older), dao.observeAll().first().map { it.id })
    }

    @Test
    fun recentNotesAreLimitedAndEditsDoNotChangeCreationOrder() = runTest {
        val ids = (1L..12L).map { day ->
            dao.insert(NoteEntity(title = "Note $day", content = "", createdAt = day, updatedAt = day))
        }
        val oldest = checkNotNull(dao.getById(ids.first()))
        dao.update(oldest.copy(updatedAt = 100))
        assertEquals(ids.takeLast(10).reversed(), dao.observeCreated(10).first().map { it.id })
        assertEquals(ids.reversed(), dao.observeCreated(-1).first().map { it.id })
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun observesInsertUpdateAndDeleteInUpdatedOrder() = runTest {
        val first = NoteEntity(title = "First", content = "", createdAt = 1, updatedAt = 1)
        val second = first.copy(title = "Second", updatedAt = 2)
        val firstId = dao.insert(first)
        val secondId = dao.insert(second)
        assertTrue(firstId > 0)
        assertEquals(listOf(secondId, firstId), dao.observeAll().first().map { it.id })
        assertEquals(first.copy(id = firstId), dao.getById(firstId))

        val updated = first.copy(id = firstId, content = "Edited", updatedAt = 3)
        assertEquals(1, dao.update(updated))
        assertEquals(updated, dao.observeById(firstId).first())
        assertEquals(listOf(firstId, secondId), dao.observeAll().first().map { it.id })

        assertEquals(1, dao.delete(updated))
        assertNull(dao.observeById(firstId).first())
        assertEquals(listOf(secondId), dao.observeAll().first().map { it.id })
    }
}
