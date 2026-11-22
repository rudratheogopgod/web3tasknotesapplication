package com.rudra.web3tasknotesapplication.data.repository

import com.rudra.web3tasknotesapplication.domain.model.Note
import org.junit.Assert.*
import org.junit.Test

class NoteSaveRulesTest {
    private val rules = NoteSaveRules({ 200L }) { "Untitled note" }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsWhitespaceOnlyNote() {
        rules.prepare(null, "  ", "\n\t")
    }

    @Test
    fun usesFallbackTitleAndSetsBothTimestamps() {
        assertEquals(Note(0, "Untitled note", " body ", 200, 200),
            rules.prepare(null, " ", " body "))
    }

    @Test
    fun editPreservesCreationTimeAndUnchangedSaveDoesNotReadClock() {
        val original = Note(7, "Title", "Body", 100, 150)
        assertEquals(original.copy(content = "Updated", updatedAt = 200),
            rules.prepare(original, "Title", "Updated"))
        val noOpRules = NoteSaveRules({ error("Unchanged save read the clock") }) { "Untitled note" }
        assertSame(original, noOpRules.prepare(original, "Title", "Body"))
    }
}
