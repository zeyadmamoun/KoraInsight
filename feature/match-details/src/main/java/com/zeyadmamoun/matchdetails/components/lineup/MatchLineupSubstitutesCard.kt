package com.zeyadmamoun.matchdetails.components.lineup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.model.summary.MatchPlayer
import com.zeyadmamoun.data.model.summary.MatchTimelineEvent
import com.zeyadmamoun.matchdetails.R

/**
 * Responsive substitutes & bench card displayed below the tactical pitch.
 *
 * Automatically adapts:
 * - On compact screens (< 600dp): Provides a tabbed switcher between Home and Away benches.
 * - On wide screens (>= 600dp): Renders Home and Away benches side-by-side in dual columns.
 */
@Composable
fun MatchLineupSubstitutesCard(
    matchDetails: MatchDetails,
    modifier: Modifier = Modifier
) {
    val homeSubs = remember(matchDetails.homeLineup) {
        matchDetails.homeLineup.filterNot { it.isStarter }
    }
    val awaySubs = remember(matchDetails.awayLineup) {
        matchDetails.awayLineup.filterNot { it.isStarter }
    }

    if (homeSubs.isEmpty() && awaySubs.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 540.dp),
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
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Substitutes & Bench",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${homeSubs.size + awaySubs.size} Players",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(10.dp))

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth >= 600.dp) {
                    // Wide / Tablet layout: Dual side-by-side columns
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BenchTeamList(
                            teamName = matchDetails.homeTeamDisplayName.ifBlank { matchDetails.homeTeamName },
                            substitutes = homeSubs,
                            events = matchDetails.events,
                            modifier = Modifier.weight(1f)
                        )
                        BenchTeamList(
                            teamName = matchDetails.awayTeamDisplayName.ifBlank { matchDetails.awayTeamName },
                            substitutes = awaySubs,
                            events = matchDetails.events,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Compact / Phone layout: Tabbed team toggle
                    var selectedTabIndex by remember { mutableIntStateOf(0) }
                    val homeName = matchDetails.homeTeamDisplayName.ifBlank { matchDetails.homeTeamName.ifBlank { "Home" } }
                    val awayName = matchDetails.awayTeamDisplayName.ifBlank { matchDetails.awayTeamName.ifBlank { "Away" } }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            contentColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            divider = {}
                        ) {
                            Tab(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                text = {
                                    Text(
                                        text = "$homeName (${homeSubs.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                text = {
                                    Text(
                                        text = "$awayName (${awaySubs.size})",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        if (selectedTabIndex == 0) {
                            BenchTeamList(
                                teamName = homeName,
                                substitutes = homeSubs,
                                events = matchDetails.events,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            BenchTeamList(
                                teamName = awayName,
                                substitutes = awaySubs,
                                events = matchDetails.events,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BenchTeamList(
    teamName: String,
    substitutes: List<MatchPlayer>,
    events: List<MatchTimelineEvent>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (substitutes.isEmpty()) {
            Text(
                text = "No substitutes listed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            substitutes.forEachIndexed { index, player ->
                BenchPlayerRow(
                    player = player,
                    events = events
                )
                if (index < substitutes.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun BenchPlayerRow(
    player: MatchPlayer,
    events: List<MatchTimelineEvent>,
    modifier: Modifier = Modifier
) {
    val subInEvent = remember(player.name, events) {
        val pName = player.name.trim().lowercase()
        events.firstOrNull { ev ->
            ev.isSubstitution && ev.subPlayerIn?.trim()?.lowercase()?.let { subIn ->
                subIn.contains(pName) || pName.contains(subIn)
            } == true
        }
    }

    val goalCount = remember(player.name, events) {
        val pName = player.name.trim().lowercase()
        events.count { ev ->
            ev.isGoal && ev.playerName.trim().lowercase().let { it.contains(pName) || pName.contains(it) }
        }
    }

    val hasCard = remember(player.name, events) {
        val pName = player.name.trim().lowercase()
        events.any { ev ->
            (ev.isYellowCard || ev.isRedCard) && ev.playerName.trim().lowercase().let { it.contains(pName) || pName.contains(it) }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Jersey Number
            Text(
                text = player.jersey.ifBlank { "•" },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(26.dp)
            )

            // Player Full / Display Name
            Text(
                text = player.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Badges (Sub In minute, Goals, Cards)
            if (subInEvent != null || player.isSubbedIn) {
                Spacer(Modifier.width(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_substitute_in),
                        contentDescription = "Subbed In",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(11.dp)
                    )
                    val minute = subInEvent?.minute.orEmpty()
                    if (minute.isNotBlank()) {
                        Text(
                            text = minute,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            if (goalCount > 0) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_football),
                    contentDescription = "Goal",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(11.dp)
                )
            }

            if (hasCard) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_card_yellow),
                    contentDescription = "Card",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Position Badge
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.position.ifBlank { "SUB" },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
