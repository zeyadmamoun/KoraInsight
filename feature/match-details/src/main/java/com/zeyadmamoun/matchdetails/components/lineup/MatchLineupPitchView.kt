package com.zeyadmamoun.matchdetails.components.lineup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.model.summary.MatchPlayer

/**
 * Responsive tactical pitch view displaying both teams' starting lineups on a regulation pitch.
 *
 * Positions the away team on the top half (attacking downward) and the home team on the
 * bottom half (attacking upward). Dynamically scales node sizes, typography, and spacing
 * to ensure readability on compact phones, standard devices, and tablets.
 *
 * @param matchDetails The complete match details containing formations, lineups, and events.
 * @param modifier Modifier applied to the outer container.
 * @param onPlayerClick Callback invoked when tapping a player node.
 */
@Composable
fun MatchLineupPitchView(
    matchDetails: MatchDetails,
    modifier: Modifier = Modifier,
    onPlayerClick: (MatchPlayer) -> Unit = {}
) {
    val homeStarters = remember(matchDetails.homeLineup) {
        matchDetails.homeLineup.filter { it.isStarter }
    }
    val awayStarters = remember(matchDetails.awayLineup) {
        matchDetails.awayLineup.filter { it.isStarter }
    }

    val homeColor = remember(matchDetails.homeTeamColor) {
        parseHexColor(matchDetails.homeTeamColor, Color(0xFF2563EB))
    }
    val awayColor = remember(matchDetails.awayTeamColor) {
        parseHexColor(matchDetails.awayTeamColor, Color(0xFFDC2626))
    }

    val allPositionedPlayers = remember(
        homeStarters,
        awayStarters,
        matchDetails.homeFormation,
        matchDetails.awayFormation
    ) {
        val homeNodes = parseAndPositionStarters(
            starters = homeStarters,
            formationString = matchDetails.homeFormation,
            isHome = true
        )
        val awayNodes = parseAndPositionStarters(
            starters = awayStarters,
            formationString = matchDetails.awayFormation,
            isHome = false
        )
        awayNodes + homeNodes
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 540.dp), // Max width cap for tablets to prevent excessive pitch height
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
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar: Teams and Formations Indicator
            LineupPitchHeader(
                matchDetails = matchDetails,
                homeColor = homeColor,
                awayColor = awayColor
            )

            Spacer(Modifier.height(10.dp))

            // The Pitch Canvas and Player Nodes Container
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.58f) // Optimal height-to-width ratio for tactical pitch clearance
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // Responsive node dimensions based on available width
                val isCompact = maxWidth < 360.dp
                val isLarge = maxWidth > 480.dp

                val nodeDiameter = if (isCompact) 25.dp else if (isLarge) 29.dp else 27.dp
                val fontSize = if (isCompact) 8.sp else if (isLarge) 9.5.sp else 9.sp
                val nameTagMaxWidth = if (isCompact) 48.dp else if (isLarge) 56.dp else 52.dp

                // 1. Football pitch background and regulation markings
                PitchMarkingsCanvas(modifier = Modifier.matchParentSize())

                // 2. Hardware-accelerated player node placement
                allPositionedPlayers.forEach { node ->
                    val teamColor = if (node.isHome) homeColor else awayColor

                    Box(
                        modifier = Modifier
                            .align(
                                BiasAlignment(
                                    horizontalBias = (node.xRatio * 2f) - 1f,
                                    verticalBias = (node.yRatio * 2f) - 1f
                                )
                            )
                    ) {
                        PitchPlayerNode(
                            player = node.player,
                            teamColor = teamColor,
                            events = matchDetails.events,
                            isGoalkeeper = node.isGoalkeeper,
                            nodeDiameter = nodeDiameter,
                            fontSize = fontSize,
                            nameTagMaxWidth = nameTagMaxWidth,
                            onClick = { onPlayerClick(node.player) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LineupPitchHeader(
    matchDetails: MatchDetails,
    homeColor: Color,
    awayColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Team Formation
        TeamFormationPill(
            teamName = matchDetails.homeTeamDisplayName.ifBlank { matchDetails.homeTeamName },
            formation = matchDetails.homeFormation,
            logoUrl = matchDetails.homeTeamLogo,
            dotColor = homeColor
        )

        Text(
            text = "vs",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Away Team Formation
        TeamFormationPill(
            teamName = matchDetails.awayTeamDisplayName.ifBlank { matchDetails.awayTeamName },
            formation = matchDetails.awayFormation,
            logoUrl = matchDetails.awayTeamLogo,
            dotColor = awayColor,
            isReversed = true
        )
    }
}

@Composable
private fun TeamFormationPill(
    teamName: String,
    formation: String?,
    logoUrl: String,
    dotColor: Color,
    modifier: Modifier = Modifier,
    isReversed: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isReversed) Arrangement.End else Arrangement.Start
    ) {
        if (!isReversed) {
            TeamDotOrLogo(logoUrl = logoUrl, dotColor = dotColor)
            Spacer(Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isReversed) Alignment.End else Alignment.Start
        ) {
            Text(
                text = teamName.ifBlank { "Team" },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!formation.isNullOrBlank()) {
                Text(
                    text = formation,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isReversed) {
            Spacer(Modifier.width(6.dp))
            TeamDotOrLogo(logoUrl = logoUrl, dotColor = dotColor)
        }
    }
}

@Composable
private fun TeamDotOrLogo(
    logoUrl: String,
    dotColor: Color
) {
    if (logoUrl.isNotBlank()) {
        AsyncImage(
            model = logoUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(20.dp)
        )
    } else {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(dotColor, shape = CircleShape)
        )
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
