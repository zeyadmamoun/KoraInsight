package com.zeyadmamoun.matchdetails.components.lineup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zeyadmamoun.data.model.summary.MatchPlayer
import com.zeyadmamoun.data.model.summary.MatchTimelineEvent
import com.zeyadmamoun.matchdetails.R

/**
 * Tactical node representing a single player on the pitch.
 *
 * Displays:
 * - Jersey circle with team kit color and shirt number (with dynamic contrast for light/white kits).
 * - Player last name pill with dark translucent background.
 * - Live match badges: ⚽ Goals, 🟨 Yellow cards, 🟥 Red cards, 🔄 Substitutions.
 *
 * @param player The player domain model.
 * @param teamColor Outfield team jersey kit color.
 * @param events Match timeline events to display in-game badges.
 * @param isGoalkeeper Whether this player is a goalkeeper (receives amber jersey kit).
 * @param nodeDiameter Diameter of the jersey circle.
 * @param fontSize Typography size for player name and jersey number.
 * @param nameTagMaxWidth Max width of the name capsule to prevent node overlap.
 * @param onClick Optional callback when tapping a player.
 */
@Composable
fun PitchPlayerNode(
    player: MatchPlayer,
    teamColor: Color,
    events: List<MatchTimelineEvent>,
    isGoalkeeper: Boolean,
    modifier: Modifier = Modifier,
    nodeDiameter: Dp = 27.dp,
    fontSize: TextUnit = 9.sp,
    nameTagMaxWidth: Dp = 52.dp,
    onClick: () -> Unit = {}
) {
    // 1. Calculate live player events (goals, cards, substitutions)
    val playerEvents = remember(player.name, events) {
        extractPlayerEvents(player, events)
    }

    // Goalkeeper jersey color is amber/yellow; outfield uses team kit color
    val jerseyBgColor = if (isGoalkeeper) Color(0xFFD97706) else teamColor

    // Compute contrast for jersey numbers (ensures white jerseys have dark, readable text)
    val isLightKit = remember(jerseyBgColor) {
        val luminance = 0.299f * jerseyBgColor.red + 0.587f * jerseyBgColor.green + 0.114f * jerseyBgColor.blue
        luminance > 0.55f
    }
    val jerseyTextColor = if (isLightKit) Color(0xFF0F172A) else Color.White
    val circleBorderColor = if (isLightKit) Color(0xFF334155).copy(alpha = 0.7f) else Color.White

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(1.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Jersey Circle with Number
            Box(
                modifier = Modifier
                    .size(nodeDiameter)
                    .background(color = jerseyBgColor, shape = CircleShape)
                    .border(width = 1.5.dp, color = circleBorderColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.jersey.ifBlank { "•" },
                    color = jerseyTextColor,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Top-End Badge Overlay (Goals, Cards, Subs)
            if (playerEvents.hasBadges) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-4).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (playerEvents.goals > 0) {
                        Icon(
                            painter = painterResource(R.drawable.ic_football),
                            contentDescription = "Goal",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(11.dp)
                        )
                        if (playerEvents.goals > 1) {
                            Text(
                                text = "${playerEvents.goals}",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 1.dp)
                            )
                        }
                    }
                    if (playerEvents.hasYellowCard && !playerEvents.hasRedCard) {
                        Icon(
                            painter = painterResource(R.drawable.ic_card_yellow),
                            contentDescription = "Yellow Card",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    if (playerEvents.hasRedCard) {
                        Icon(
                            painter = painterResource(R.drawable.ic_card_red),
                            contentDescription = "Red Card",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    if (playerEvents.isSubbedOut) {
                        Icon(
                            painter = painterResource(R.drawable.ic_substitute_out),
                            contentDescription = "Subbed Out",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(1.dp))

        // Name Capsule Pill
        Box(
            modifier = Modifier
                .widthIn(max = nameTagMaxWidth)
                .background(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(3.dp)
                )
                .padding(horizontal = 3.dp, vertical = 0.5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = player.toShortName(),
                color = Color.White,
                fontSize = fontSize,
                lineHeight = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Formats a player's full name into a compact surname / short name for pitch rendering.
 */
private fun MatchPlayer.toShortName(): String {
    val trimmed = name.trim()
    if (trimmed.isBlank()) return jersey
    val parts = trimmed.split(" ").filter { it.isNotBlank() }
    return if (parts.size > 1) parts.last() else trimmed
}

private data class PlayerEventStatus(
    val goals: Int = 0,
    val hasYellowCard: Boolean = false,
    val hasRedCard: Boolean = false,
    val isSubbedOut: Boolean = false
) {
    val hasBadges: Boolean get() = goals > 0 || hasYellowCard || hasRedCard || isSubbedOut
}

private fun extractPlayerEvents(
    player: MatchPlayer,
    events: List<MatchTimelineEvent>
): PlayerEventStatus {
    var goals = 0
    var hasYellow = false
    var hasRed = false
    var isSubbedOut = player.isSubbedOut

    val pName = player.name.trim().lowercase()
    if (pName.isBlank()) return PlayerEventStatus(isSubbedOut = isSubbedOut)

    events.forEach { event ->
        val evPlayer = event.playerName.trim().lowercase()
        val evSubOut = event.subPlayerOut?.trim()?.lowercase().orEmpty()

        val isTarget = evPlayer.contains(pName) || pName.contains(evPlayer)

        if (isTarget) {
            if (event.isGoal) goals++
            if (event.isYellowCard) hasYellow = true
            if (event.isRedCard || event.isYellowRedCard) hasRed = true
        }

        if (evSubOut.isNotBlank() && (evSubOut.contains(pName) || pName.contains(evSubOut))) {
            isSubbedOut = true
        }
    }

    return PlayerEventStatus(
        goals = goals,
        hasYellowCard = hasYellow,
        hasRedCard = hasRed,
        isSubbedOut = isSubbedOut
    )
}
