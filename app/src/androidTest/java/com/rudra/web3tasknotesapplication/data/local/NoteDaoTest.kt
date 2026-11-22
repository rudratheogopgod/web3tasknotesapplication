package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Room
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
