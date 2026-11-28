package com.zeyadmamoun.matchdetails.components.lineup

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.matchdetails.R
import com.zeyadmamoun.matchdetails.components.matchDetailsSegmentedButtonColors

private enum class LineupViewMode(val label: String) {
    Pitch("Pitch"),
    List("Roster")
}

/**
 * Top-level responsive Lineup segment for [MatchDetailsScreen].
 *
 * Provides:
 * - A toggle between Tactical Pitch view and Full Roster List view.
 * - On Tactical Pitch mode: Displays the pitch formation with the Substitutes card below it.
 * - On Roster List mode: Displays the comprehensive starting XI and bench lists.
 * - Graceful empty state when lineups have not yet been announced before kickoff.
 *
 * @param matchDetails The complete match details domain model.
 * @param modifier Modifier applied to the section.
 */
@Composable
fun MatchLineupSection(
    matchDetails: MatchDetails,
    modifier: Modifier = Modifier
) {
    val hasLineup = remember(matchDetails.homeLineup, matchDetails.awayLineup) {
        matchDetails.homeLineup.isNotEmpty() || matchDetails.awayLineup.isNotEmpty()
    }

    if (!hasLineup) {
        LineupEmptyStateCard(
            isCompleted = matchDetails.isCompleted,
            modifier = modifier
        )
        return
    }

    var selectedModeIndex by remember { mutableIntStateOf(LineupViewMode.Pitch.ordinal) }
    val modes = LineupViewMode.entries

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Lineup Mode Switcher: Pitch vs List
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .widthIn(max = 240.dp)
                .fillMaxWidth()
                .height(36.dp)
        ) {
            modes.forEachIndexed { index, mode ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = modes.size
                    ),
                    onClick = { selectedModeIndex = index },
                    selected = index == selectedModeIndex,
                    colors = matchDetailsSegmentedButtonColors(),
                    label = {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (index == selectedModeIndex) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    icon = {}
                )
            }
        }

        // View Mode Content Crossfade
        Crossfade(
            targetState = modes[selectedModeIndex],
            label = "LineupViewCrossfade"
        ) { mode ->
            when (mode) {
                LineupViewMode.Pitch -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MatchLineupPitchView(matchDetails = matchDetails)
                        MatchLineupSubstitutesCard(matchDetails = matchDetails)
                    }
                }

                LineupViewMode.List -> {
                    MatchLineupListView(matchDetails = matchDetails)
                }
            }
        }
    }
}

@Composable
private fun LineupEmptyStateCard(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp)
            .padding(vertical = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_football),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = if (isCompleted) "Lineup Unavailable" else "Lineups Not Announced",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isCompleted) {
                    "Official lineup details were not recorded for this fixture."
                } else {
                    "Starting formations and team squads will appear here once confirmed prior to kickoff."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
