package com.rudra.web3tasknotesapplication.data.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rudra.web3tasknotesapplication.domain.model.NoteCategory
import com.rudra.web3tasknotesapplication.domain.model.NoteColor
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesMigrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @get:Rule
    val helper = MigrationTestHelper(instrumentation, NotesDatabase::class.java)

    @Test
    fun migrateV1PreservesRowsAndAddsMetadataDefaults() = runBlocking {
        val name = "notes-migration-v1-v2"
        val context = instrumentation.targetContext
        try {
            helper.createDatabase(name, 1).use { db ->
                db.execSQL(
                    "INSERT INTO notes (id, title, content, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any>(7L, "Legacy title", "Legacy content", 100L, 200L),
                )
                db.execSQL(
                    "INSERT INTO notes (id, title, content, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?)",
                    arrayOf<Any>(42L, "Another note", "Unicode: café", 210L, 210L),
                )
            }
            helper.runMigrationsAndValidate(name, 2, true, MIGRATION_1_2).close()
            val db = Room.databaseBuilder(context, NotesDatabase::class.java, name)
                .addMigrations(MIGRATION_1_2)
                .build()
            try {
                val dao = db.noteDao()
                val legacy = NoteEntity(
                    id = 7, title = "Legacy title", content = "Legacy content",
                    createdAt = 100, updatedAt = 200, isPinned = false,
                    colorId = NoteColor.DEFAULT, category = NoteCategory.QUICK_NOTE,
                )
                assertEquals(legacy, dao.getById(7))
                assertEquals(
                    legacy.copy(id = 42, title = "Another note", content = "Unicode: café",
                        createdAt = 210, updatedAt = 210),
                    dao.getById(42),
                )
                val updated = legacy.copy(
                    isPinned = true, colorId = NoteColor.LAVENDER, category = NoteCategory.JOURNAL,
                )
                assertEquals(1, dao.update(updated))
                assertEquals(updated, dao.getById(7))
            } finally {
                db.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
        Unit
    }
}
