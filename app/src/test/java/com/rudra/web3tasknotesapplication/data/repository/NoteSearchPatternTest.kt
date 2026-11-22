package com.rudra.web3tasknotesapplication.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NoteSearchPatternTest {
    @Test fun trimsAndEscapesOnlyLikeMetacharacters() {
        assertEquals("%50!%!_!!%", noteSearchPattern(" 50%_! "))
        assertEquals("%猫🙂'\\%", noteSearchPattern("猫🙂'\\"))
        assertNull(noteSearchPattern(" \t "))
    }
}
