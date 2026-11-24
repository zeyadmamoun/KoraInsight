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
import androidx.compose.foundation.shape.CircleShape
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
 * Full roster list view displaying Starting XI and Substitutes.
 *
 * Responsive layout:
 * - Wide screens (>= 600dp): Renders Home and Away squads side-by-side.
 * - Compact screens (< 600dp): Uses a team tab switcher with full-width rows.
 */
@Composable
fun MatchLineupListView(
    matchDetails: MatchDetails,
    modifier: Modifier = Modifier
) {
    val homeStarters = remember(matchDetails.homeLineup) { matchDetails.homeLineup.filter { it.isStarter } }
    val homeSubs = remember(matchDetails.homeLineup) { matchDetails.homeLineup.filterNot { it.isStarter } }

    val awayStarters = remember(matchDetails.awayLineup) { matchDetails.awayLineup.filter { it.isStarter } }
    val awaySubs = remember(matchDetails.awayLineup) { matchDetails.awayLineup.filterNot { it.isStarter } }

    val homeName = matchDetails.homeTeamDisplayName.ifBlank { matchDetails.homeTeamName.ifBlank { "Home Team" } }
    val awayName = matchDetails.awayTeamDisplayName.ifBlank { matchDetails.awayTeamName.ifBlank { "Away Team" } }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 680.dp)
    ) {
        if (maxWidth >= 600.dp) {
            // Wide Screen: Dual Columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SquadCard(
                    teamName = homeName,
                    formation = matchDetails.homeFormation,
                    starters = homeStarters,
                    substitutes = homeSubs,
                    events = matchDetails.events,
                    teamColor = parseHexColor(matchDetails.homeTeamColor, Color(0xFF2563EB)),
                    modifier = Modifier.weight(1f)
                )
                SquadCard(
                    teamName = awayName,
                    formation = matchDetails.awayFormation,
                    starters = awayStarters,
                    substitutes = awaySubs,
                    events = matchDetails.events,
                    teamColor = parseHexColor(matchDetails.awayTeamColor, Color(0xFFDC2626)),
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            // Compact Screen: Tab Switcher
            var selectedTeamIndex by remember { mutableIntStateOf(0) }

            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTeamIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTeamIndex == 0,
                        onClick = { selectedTeamIndex = 0 },
                        text = {
                            Text(
                                text = homeName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTeamIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                    Tab(
                        selected = selectedTeamIndex == 1,
                        onClick = { selectedTeamIndex = 1 },
                        text = {
                            Text(
                                text = awayName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selectedTeamIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }

                Spacer(Modifier.height(10.dp))

                if (selectedTeamIndex == 0) {
                    SquadCard(
                        teamName = homeName,
                        formation = matchDetails.homeFormation,
                        starters = homeStarters,
                        substitutes = homeSubs,
                        events = matchDetails.events,
                        teamColor = parseHexColor(matchDetails.homeTeamColor, Color(0xFF2563EB)),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    SquadCard(
                        teamName = awayName,
                        formation = matchDetails.awayFormation,
                        starters = awayStarters,
                        substitutes = awaySubs,
                        events = matchDetails.events,
                        teamColor = parseHexColor(matchDetails.awayTeamColor, Color(0xFFDC2626)),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SquadCard(
    teamName: String,
    formation: String?,
    starters: List<MatchPlayer>,
    substitutes: List<MatchPlayer>,
    events: List<MatchTimelineEvent>,
    teamColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(teamColor, shape = CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = teamName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!formation.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = formation,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Starting XI Section
            Text(
                text = "Starting XI (${starters.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))

            starters.forEachIndexed { index, player ->
                ListPlayerRow(player = player, events = events)
                if (index < starters.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        thickness = 0.5.dp
                    )
                }
            }

            if (substitutes.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                    thickness = 1.dp
                )
                Spacer(Modifier.height(10.dp))

                // Substitutes Section
                Text(
                    text = "Substitutes (${substitutes.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))

                substitutes.forEachIndexed { index, player ->
                    ListPlayerRow(player = player, events = events)
                    if (index < substitutes.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListPlayerRow(
    player: MatchPlayer,
    events: List<MatchTimelineEvent>,
    modifier: Modifier = Modifier
) {
    val goalCount = remember(player.name, events) {
        val pName = player.name.trim().lowercase()
        events.count { ev ->
            ev.isGoal && ev.playerName.trim().lowercase().let { it.contains(pName) || pName.contains(it) }
        }
    }
    val hasYellow = remember(player.name, events) {
        val pName = player.name.trim().lowercase()
        events.any { ev ->
            ev.isYellowCard && ev.playerName.trim().lowercase().let { it.contains(pName) || pName.contains(it) }
        }
    }
    val hasRed = remember(player.name, events) {
        val pName = player.name.trim().lowercase()
        events.any { ev ->
            (ev.isRedCard || ev.isYellowRedCard) && ev.playerName.trim().lowercase().let { it.contains(pName) || pName.contains(it) }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = player.jersey.ifBlank { "•" },
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(26.dp)
            )

            Text(
                text = player.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Live event indicators
            if (goalCount > 0) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_football),
                    contentDescription = "Goal",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(11.dp)
                )
                if (goalCount > 1) {
                    Text(
                        text = "$goalCount",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 1.dp)
                    )
                }
            }

            if (hasYellow && !hasRed) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_card_yellow),
                    contentDescription = "Yellow Card",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(10.dp)
                )
            }

            if (hasRed) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_card_red),
                    contentDescription = "Red Card",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(10.dp)
                )
            }

            if (player.isSubbedOut) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_substitute_out),
                    contentDescription = "Subbed Out",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(10.dp)
                )
            } else if (player.isSubbedIn) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_substitute_in),
                    contentDescription = "Subbed In",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(10.dp)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = player.position.ifBlank { "—" },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun parseHexColor(hex: String?, fallback: Color): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val clean = hex.trim().removePrefix("#")
        Color(android.graphics.Color.parseColor("#$clean"))
    } catch (_: Exception) {
        fallback
    }
}
