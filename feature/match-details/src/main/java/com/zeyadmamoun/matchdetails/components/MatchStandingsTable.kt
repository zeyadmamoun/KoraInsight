package com.zeyadmamoun.matchdetails.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.data.model.standings.LeagueStandings
import com.zeyadmamoun.data.model.standings.LeagueTableEntry
import com.zeyadmamoun.data.model.summary.MatchDetails

enum class StandingsTableViewMode(val label: String) {
    Short("Short"),
    Full("Full"),
    Form("Form")
}

/**
 * Renders the league standings table inside Match Details (Table segment).
 * Matches the KoraInsight design system: outlined Card container, MaterialTheme color scheme,
 * full team statistics, qualification indicator stripes, and active match club highlights.
 */
@Composable
fun MatchStandingsTable(
    standings: LeagueStandings?,
    networkStatus: NetworkStatus,
    currentMatch: MatchDetails,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(StandingsTableViewMode.Full) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Sub-filters row: [Short] [Full] [Form] ... [Overall ▾]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Outlined pill filter selector
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    StandingsTableViewMode.entries.forEach { mode ->
                        val isSelected = mode == viewMode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                    else Color.Transparent
                                )
                                .clickable { viewMode = mode }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Right side: "Overall ▾" outlined chip
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Overall",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Filter scope",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Main Standings Card Container with Outlines matching KoraInsight Design System
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
            )
        ) {
            when (networkStatus) {
                is NetworkStatus.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                is NetworkStatus.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = networkStatus.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        ElevatedAssistChip(
                            onClick = onRetry,
                            label = { Text("Retry") }
                        )
                    }
                }

                is NetworkStatus.Success -> {
                    val entries = standings?.entries.orEmpty()
                    if (entries.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No standings data available for this league.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            // League Title Header
                            val leagueDisplayName = standings?.leagueName
                                ?: currentMatch.leagueName.ifBlank { "League Standings" }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Badge icon with primary color scheme
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (standings?.leagueAbbreviation ?: currentMatch.leagueSlug)
                                            .take(3)
                                            .uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = leagueDisplayName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            // Table Columns Header
                            StandingsColumnsHeader(viewMode = viewMode)

                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )

                            // Dynamic Qualification Legend based on real data
                            val leagueSlug = currentMatch.leagueSlug
                            val dynamicLegendItems = remember(standings, leagueSlug, leagueDisplayName) {
                                resolveDynamicLegendItems(standings, leagueSlug, leagueDisplayName)
                            }

                            // Rows
                            AnimatedContent(
                                targetState = viewMode,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "StandingsTableAnimation"
                            ) { activeMode ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    entries.forEachIndexed { index, entry ->
                                        val isCurrentMatchTeam = isTeamInMatch(entry, currentMatch)

                                        StandingsTableRow(
                                            entry = entry,
                                            viewMode = activeMode,
                                            isHighlighted = isCurrentMatchTeam,
                                            leagueSlug = leagueSlug,
                                            leagueName = leagueDisplayName
                                        )

                                        if (index < entries.lastIndex) {
                                            HorizontalDivider(
                                                thickness = 0.5.dp,
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }

                            if (dynamicLegendItems.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))

                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                                )

                                DynamicStandingsLegend(
                                    items = dynamicLegendItems,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Column header row with aligned stat titles.
 */
@Composable
private fun StandingsColumnsHeader(
    viewMode: StandingsTableViewMode,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank (#)
        Text(
            text = "#",
            modifier = Modifier.width(26.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Team Name
        Text(
            text = "Team",
            modifier = Modifier
                .weight(1f)
                .padding(start = 28.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (viewMode) {
            StandingsTableViewMode.Full -> {
                StatHeaderItem(text = "Pl", width = 24.dp)
                StatHeaderItem(text = "W", width = 20.dp)
                StatHeaderItem(text = "D", width = 20.dp)
                StatHeaderItem(text = "L", width = 20.dp)
                StatHeaderItem(text = "+/-", width = 42.dp)
                StatHeaderItem(text = "GD", width = 30.dp)
                StatHeaderItem(text = "PTS", width = 34.dp)
            }
            StandingsTableViewMode.Short -> {
                StatHeaderItem(text = "Pl", width = 32.dp)
                StatHeaderItem(text = "GD", width = 38.dp)
                StatHeaderItem(text = "PTS", width = 38.dp)
            }
            StandingsTableViewMode.Form -> {
                StatHeaderItem(text = "Pl", width = 32.dp)
                StatHeaderItem(text = "Record", width = 64.dp)
                StatHeaderItem(text = "PTS", width = 38.dp)
            }
        }
    }
}

@Composable
private fun StatHeaderItem(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/**
 * Individual row displaying a team's rank, crest, name, qualification bar, and stats.
 */
@Composable
private fun StandingsTableRow(
    entry: LeagueTableEntry,
    viewMode: StandingsTableViewMode,
    isHighlighted: Boolean,
    leagueSlug: String,
    leagueName: String,
    modifier: Modifier = Modifier
) {
    val stripeColor = getQualificationColor(entry, leagueSlug, leagueName)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else Color.Transparent
            )
            .padding(vertical = 6.dp)
    ) {
        // Colored qualification stripe on the left edge
        if (stripeColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(24.dp)
                    .background(
                        color = stripeColor,
                        shape = RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank (#)
            Text(
                text = entry.rank.toString(),
                modifier = Modifier.width(26.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold
                ),
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            // Team: Crest + Display Name
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = entry.teamLogoUrl,
                    contentDescription = entry.teamName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = entry.teamDisplayName.ifBlank { entry.teamName },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Stats columns based on mode
            when (viewMode) {
                StandingsTableViewMode.Full -> {
                    StatValueItem(text = entry.gamesPlayed.toString(), width = 24.dp)
                    StatValueItem(text = entry.wins.toString(), width = 20.dp)
                    StatValueItem(text = entry.draws.toString(), width = 20.dp)
                    StatValueItem(text = entry.losses.toString(), width = 20.dp)

                    // Goals: "+/-" (e.g. 21-4)
                    StatValueItem(
                        text = "${entry.goalsFor}-${entry.goalsAgainst}",
                        width = 42.dp
                    )

                    // Goal Difference: "GD" (e.g. +17, 0, -2)
                    val gdText = when {
                        entry.goalDifference > 0 -> "+${entry.goalDifference}"
                        else -> entry.goalDifference.toString()
                    }
                    Text(
                        text = gdText,
                        modifier = Modifier.width(30.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Points: "PTS" Pill Badge
                    Box(
                        modifier = Modifier.width(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = entry.points.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                StandingsTableViewMode.Short -> {
                    StatValueItem(text = entry.gamesPlayed.toString(), width = 32.dp)
                    val gdText = when {
                        entry.goalDifference > 0 -> "+${entry.goalDifference}"
                        else -> entry.goalDifference.toString()
                    }
                    Text(
                        text = gdText,
                        modifier = Modifier.width(38.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier.width(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = entry.points.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                StandingsTableViewMode.Form -> {
                    StatValueItem(text = entry.gamesPlayed.toString(), width = 32.dp)
                    StatValueItem(
                        text = entry.overallRecord ?: "${entry.wins}-${entry.draws}-${entry.losses}",
                        width = 64.dp
                    )
                    Box(
                        modifier = Modifier.width(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isHighlighted) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = entry.points.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatValueItem(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

data class QualificationLegendItem(
    val color: Color,
    val label: String
)

/**
 * Dynamic qualification legend.
 * Uses real competition notes from ESPN when available, or Champions League stages
 * if the league is UCL. Never shows UCL stages for non-UCL leagues.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DynamicStandingsLegend(
    items: List<QualificationLegendItem>,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items.forEach { item ->
            LegendDotItem(color = item.color, label = item.label)
        }
    }
}

@Composable
private fun LegendDotItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Resolves legend items dynamically from real ESPN entry notes.
 * If no notes are provided, only shows stages for UEFA Champions League;
 * for all other leagues (CAF, domestic cups, etc.), returns empty to hide the legend.
 */
private fun resolveDynamicLegendItems(
    standings: LeagueStandings?,
    leagueSlug: String,
    leagueName: String
): List<QualificationLegendItem> {
    val entries = standings?.entries.orEmpty()
    val fromNotes = entries
        .filter { !it.qualificationNote.isNullOrBlank() }
        .mapNotNull { entry ->
            val color = getQualificationColor(entry, leagueSlug, leagueName)
            if (color != Color.Transparent) {
                QualificationLegendItem(color = color, label = entry.qualificationNote!!)
            } else null
        }
        .distinctBy { it.label }

    if (fromNotes.isNotEmpty()) {
        return fromNotes
    }

    // Only UEFA Champions League gets the UCL specific stages fallback
    val isUcl = leagueSlug.equals("uefa.champions", ignoreCase = true) ||
            leagueName.contains("UEFA Champions League", ignoreCase = true)
    if (isUcl) {
        return listOf(
            QualificationLegendItem(Color(0xFF3B82F6), "Round of 16 (1-8)"),
            QualificationLegendItem(Color(0xFFF59E0B), "Playoffs (9-24)"),
            QualificationLegendItem(Color(0xFFEF4444), "Eliminated (25-36)")
        )
    }

    // All other leagues without notes: return empty so no false UCL legend is shown
    return emptyList()
}

/**
 * Determines whether a given standing table entry represents one of the two clubs playing in the match.
 */
private fun isTeamInMatch(entry: LeagueTableEntry, match: MatchDetails): Boolean {
    if (entry.teamId.isNotBlank() && (entry.teamId == match.homeTeamId || entry.teamId == match.awayTeamId)) {
        return true
    }
    val entryName = entry.teamDisplayName.ifBlank { entry.teamName }
    return entryName.equals(match.homeTeamDisplayName, ignoreCase = true) ||
            entryName.equals(match.homeTeamName, ignoreCase = true) ||
            entryName.equals(match.awayTeamDisplayName, ignoreCase = true) ||
            entryName.equals(match.awayTeamName, ignoreCase = true)
}

/**
 * Parses hex color strings safely, handling '#' or '##' prefixes from ESPN.
 */
private fun parseQualificationColor(hex: String?): Color? {
    if (hex.isNullOrBlank()) return null
    val clean = hex.trim().removePrefix("#").removePrefix("#")
    return try {
        Color(android.graphics.Color.parseColor("#$clean"))
    } catch (_: Exception) {
        null
    }
}

/**
 * Returns the qualification color stripe matching the app's palette and real ESPN notes.
 */
private fun getQualificationColor(
    entry: LeagueTableEntry,
    leagueSlug: String,
    leagueName: String
): Color {
    // 1. Explicit color provided by ESPN
    parseQualificationColor(entry.qualificationColor)?.let { return it }

    // 2. Explicit note description from ESPN
    val note = entry.qualificationNote
    if (!note.isNullOrBlank()) {
        return when {
            note.contains("Champions League", ignoreCase = true) ||
                    note.contains("round of 16", ignoreCase = true) -> Color(0xFF3B82F6) // Blue
            note.contains("Europa", ignoreCase = true) ||
                    note.contains("seeded", ignoreCase = true) -> Color(0xFFF59E0B) // Amber
            note.contains("Conference", ignoreCase = true) ||
                    note.contains("unseeded", ignoreCase = true) -> Color(0xFF10B981) // Emerald
            note.contains("Relegation", ignoreCase = true) ||
                    note.contains("Eliminated", ignoreCase = true) -> Color(0xFFEF4444) // Red
            else -> Color(0xFF3B82F6)
        }
    }

    // 3. Fallback ONLY for UEFA Champions League
    val isUcl = leagueSlug.equals("uefa.champions", ignoreCase = true) ||
            leagueName.contains("UEFA Champions League", ignoreCase = true)
    if (isUcl) {
        return when (entry.rank) {
            in 1..8 -> Color(0xFF3B82F6)
            in 9..24 -> Color(0xFFF59E0B)
            in 25..36 -> Color(0xFFEF4444)
            else -> Color.Transparent
        }
    }

    // 4. Fallback for major domestic 20-team leagues (eng.1, esp.1, ger.1, ita.1)
    if (leagueSlug.contains(".1") && !leagueSlug.startsWith("caf", ignoreCase = true)) {
        return when (entry.rank) {
            in 1..4 -> Color(0xFF3B82F6)
            5 -> Color(0xFFF59E0B)
            6 -> Color(0xFF10B981)
            in 18..20 -> Color(0xFFEF4444)
            else -> Color.Transparent
        }
    }

    // Otherwise (e.g. CAF tournaments where groups have ranks 1..4): transparent
    return Color.Transparent
}
