package com.zeyadmamoun.leagues.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.zeyadmamoun.data.model.standings.LeagueStandings
import com.zeyadmamoun.data.model.standings.LeagueTableEntry

@Composable
fun StandingsTable(
    standing: LeagueStandings,
    modifier: Modifier = Modifier,
    maxRows: Int = 6,
    showLegend: Boolean = true,
    customLegend: (@Composable () -> Unit)? = null
) {
    val displayEntries = standing.entries.take(maxRows)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Table Column Headers
        StandingsTableHeader()

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )

        Spacer(Modifier.height(2.dp))

        // Table Rows
        displayEntries.forEach { entry ->
            StandingsTableRow(entry = entry)
        }

        Spacer(Modifier.height(4.dp))

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
        )

        // Bottom Legend
        if (customLegend != null) {
            customLegend()
        } else if (showLegend) {
            StandingsQualificationLegend()
        }
    }
}

@Composable
fun StandingsTableHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            modifier = Modifier.width(26.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Club",
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "P",
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "GD",
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "PTS",
            modifier = Modifier.width(36.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StandingsTableRow(
    entry: LeagueTableEntry,
    modifier: Modifier = Modifier
) {
    val parsedColor = entry.qualificationColor?.let { colorStr ->
        try {
            Color(android.graphics.Color.parseColor(colorStr))
        } catch (_: Exception) {
            null
        }
    }

    val stripeColor = when {
        parsedColor != null -> parsedColor
        entry.qualificationNote?.contains("Champions League", ignoreCase = true) == true || entry.rank in 1..4 -> Color(0xFF3B82F6) // Blue (UCL)
        entry.qualificationNote?.contains("Europa", ignoreCase = true) == true || entry.rank == 5 -> Color(0xFFF59E0B)          // Amber (UEL)
        entry.qualificationNote?.contains("Conference", ignoreCase = true) == true || entry.rank == 6 -> Color(0xFF10B981)      // Emerald (UECL)
        entry.qualificationNote?.contains("Relegation", ignoreCase = true) == true -> Color(0xFFEF4444)                          // Red
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Left qualification stripe
        if (stripeColor != Color.Transparent) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(20.dp)
                    .background(
                        color = stripeColor,
                        shape = RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp)
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank (#)
            Text(
                text = entry.rank.toString(),
                modifier = Modifier.width(26.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Club: Crest + Name
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
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Matches Played (P)
            Text(
                text = entry.gamesPlayed.toString(),
                modifier = Modifier.width(28.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Goal Difference (GD)
            val gdText = if (entry.goalDifference > 0) "+${entry.goalDifference}" else "${entry.goalDifference}"
            Text(
                text = gdText,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Points (PTS) Pill Badge
            Box(
                modifier = Modifier.width(36.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = entry.points.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun StandingsQualificationLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp, start = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendDotItem(color = Color(0xFF3B82F6), label = "Champions League")
        LegendDotItem(color = Color(0xFFF59E0B), label = "5 Europa")
        LegendDotItem(color = Color(0xFF10B981), label = "6 Conf League")
    }
}

@Composable
private fun LegendDotItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
