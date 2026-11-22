package com.rudra.web3tasknotesapplication.ui.adaptive

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.separatingVerticalHingeBounds
import androidx.compose.material3.adaptive.layout.HingePolicy
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/** Dimensions shared across the list and editor so resizing respects the same content limits. */
internal object NotesWindowLayout {
    const val TwoPaneMinWidth = 600
    const val ExpandedMinWidth = 840
    const val CompactHeight = 480

    val DetailPreferredWidth = 240.dp
    val ListWidth = 360.dp
    val ExpandedListWidth = 400.dp
    val ListMaxWidth = 840.dp
    val SearchMaxWidth = 640.dp
    val SheetMaxWidth = 640.dp
    val EditorMaxWidth = 720.dp
    val SegmentsMaxWidth = 480.dp
    val CardMinWidth = 152.dp
    val RecentCardWidth = 160.dp

    fun listWidth(expanded: Boolean) = if (expanded) ExpandedListWidth else ListWidth
    fun paneCount(wideEnough: Boolean, separatingVerticalHinge: Boolean, tabletop: Boolean) =
        if (!tabletop && (wideEnough || separatingVerticalHinge)) 2 else 1
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun notesWindowDirective(info: WindowAdaptiveInfo) = with(info) {
    val directive = calculatePaneScaffoldDirective(info, HingePolicy.AvoidSeparating)
    val tabletop = info.windowPosture.isTabletop
    directive.copy(
        maxHorizontalPartitions = NotesWindowLayout.paneCount(
            info.windowSizeClass.isWidthAtLeastBreakpoint(NotesWindowLayout.TwoPaneMinWidth),
            info.windowPosture.separatingVerticalHingeBounds.isNotEmpty(),
            tabletop,
        ),
        horizontalPartitionSpacerSize = 0.dp,
    )
}
