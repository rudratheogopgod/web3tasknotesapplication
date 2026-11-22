package com.rudra.web3tasknotesapplication.data.mapper

import com.rudra.web3tasknotesapplication.data.local.NoteEntity
import com.rudra.web3tasknotesapplication.domain.model.Note
import org.junit.Assert.assertEquals
import org.junit.Test

class NoteMapperTest {
    @Test
    fun keepsIdentityTextAndDistinctTimestampsInBothDirections() {
        val entity = NoteEntity(9, "Journal", "A draft", 15, 40)
        val note = Note(9, "Journal", "A draft", 15, 40)
        assertEquals(note, entity.toNote())
        assertEquals(entity, note.toEntity())
    }
}
