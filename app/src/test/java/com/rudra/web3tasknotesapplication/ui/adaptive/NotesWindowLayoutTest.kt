package com.rudra.web3tasknotesapplication.ui.adaptive

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class NotesWindowLayoutTest {
    @Test
    fun reservesTwoPanesAtMediumWidthOrAcrossASeparatingVerticalHinge() {
        assertEquals(1, NotesWindowLayout.paneCount(false, false, false))
        assertEquals(2, NotesWindowLayout.paneCount(true, false, false))
        assertEquals(2, NotesWindowLayout.paneCount(false, true, false))
        assertEquals(1, NotesWindowLayout.paneCount(true, false, true))
    }

    @Test
    fun preferredPaneWidthsFitAtTheMinimumDualPaneBreakpoint() {
        assertEquals(NotesWindowLayout.TwoPaneMinWidth.dp,
            NotesWindowLayout.listWidth(expanded = false) + NotesWindowLayout.DetailPreferredWidth)
        assertEquals(400.dp, NotesWindowLayout.listWidth(expanded = true))
        assertEquals(720.dp, NotesWindowLayout.EditorMaxWidth)
    }
}
