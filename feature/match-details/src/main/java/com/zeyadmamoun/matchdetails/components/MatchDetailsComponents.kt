package com.zeyadmamoun.matchdetails.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun matchDetailsSegmentedButtonColors(
    isDark: Boolean = isSystemInDarkTheme()
): SegmentedButtonColors {
    val (activeContainerColor, activeContentColor) = if (isDark) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) to MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }

    return SegmentedButtonDefaults.colors(
        activeContainerColor = activeContainerColor,
        activeContentColor = activeContentColor
    )
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    tabs: List<MatchDetailsSegments> = MatchDetailsSegments.entries,
    selectedSegmentIndex: Int,
    onSegmentSelected: (Int) -> Unit = {}
) {
    val colors = matchDetailsSegmentedButtonColors()

    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
    ) {
        tabs.forEachIndexed { index, segment ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = tabs.size
                ),
                onClick = {
                    onSegmentSelected(index)
                },
                selected = index == selectedSegmentIndex,
                label = {
                    Text(
                        text = segment.name,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = colors,
                icon = {}
            )
        }
    }
}

/**
 * Material 3 TabRow for navigating between Match Details sections
 * (Summary, Lineup, Standings, News).
 *
 * Prevents text wrapping issues by using horizontal tab slots with
 * an animated bottom primary indicator.
 */
@Composable
fun MatchDetailsTabRow(
    selectedSegmentIndex: Int,
    onSegmentSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabs: List<MatchDetailsSegments> = MatchDetailsSegments.entries
) {
    TabRow(
        selectedTabIndex = selectedSegmentIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )
        }
    ) {
        tabs.forEachIndexed { index, segment ->
            val isSelected = index == selectedSegmentIndex
            Tab(
                selected = isSelected,
                onClick = { onSegmentSelected(index) },
                text = {
                    Text(
                        text = segment.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        softWrap = false
                    )
                }
            )
        }
    }
}

