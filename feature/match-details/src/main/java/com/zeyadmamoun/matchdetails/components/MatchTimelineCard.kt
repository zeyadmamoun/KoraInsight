package com.zeyadmamoun.matchdetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zeyadmamoun.core.R
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.model.summary.MatchTimelineEvent

/**
 * Match Timeline Card corresponding to Figma frame_89.
 *
 * Displays all match timeline events:
 * - Goals (regular goals with football icon and assists)
 * - Penalties (penalty goals/misses with penalty indicators)
 * - Yellow cards (yellow card icon)
 * - Red cards (red card icon)
 * - Second yellow cards (combined yellow/red card icon)
 * - Substitutions (substitute icon with player in and player out)
 * - Halftime (HT), Fulltime (FT), and Extra Time (ET / AET) dividers
 */
@Composable
fun MatchTimelineCard(
    matchDetails: MatchDetails,
    modifier: Modifier = Modifier,
    customEvents: List<MatchTimelineEvent>? = null
) {
    val timelineEvents = customEvents ?: matchDetails.events
    val displayItems = buildTimelineDisplayItems(timelineEvents, matchDetails)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Teams & Match Status
            TimelineCardHeader(matchDetails)

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                thickness = 1.dp
            )

            if (displayItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (matchDetails.isCompleted) "No key events recorded." else "Match timeline will appear once the game kicks off.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    displayItems.forEachIndexed { index, item ->
                        when (item) {
                            is TimelineDisplayItem.EventItem -> {
                                val isFirst = index == 0
                                val isLast = index == displayItems.lastIndex
                                TimelineEventRow(
                                    event = item.event,
                                    isHome = item.isHome,
                                    isFirst = isFirst,
                                    isLast = isLast
                                )
                            }
                            is TimelineDisplayItem.PeriodDividerItem -> {
                                PeriodDividerRow(
                                    label = item.label,
                                    score = item.score
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineCardHeader(matchDetails: MatchDetails) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home Team
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            AsyncImage(
                model = matchDetails.homeTeamLogo,
                placeholder = painterResource(R.drawable.flag),
                contentDescription = matchDetails.homeTeamName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = matchDetails.homeTeamName.ifBlank { "Home" },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Match Info / Title
        Text(
            text = "TIMELINE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Away Team
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = matchDetails.awayTeamName.ifBlank { "Away" },
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
            Spacer(Modifier.width(8.dp))
            AsyncImage(
                model = matchDetails.awayTeamLogo,
                placeholder = painterResource(R.drawable.flag),
                contentDescription = matchDetails.awayTeamName,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TimelineEventRow(
    event: MatchTimelineEvent,
    isHome: Boolean,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column (Home Team Event Content)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (isHome) {
                EventContent(event = event, isHome = true)
            }
        }

        // Center Column (Vertical Line + Icon + Minute)
        TimelineCenterNode(
            event = event,
            isFirst = isFirst,
            isLast = isLast
        )

        // Right Column (Away Team Event Content)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (!isHome) {
                EventContent(event = event, isHome = false)
            }
        }
    }
}

@Composable
private fun TimelineCenterNode(
    event: MatchTimelineEvent,
    isFirst: Boolean,
    isLast: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(36.dp)
            .fillMaxHeight()
    ) {
        // Upper connecting line
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(
                    if (isFirst) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
        )

        // Event Icon with background circle
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            EventIcon(event)
        }

        // Lower connecting line
        Box(
            modifier = Modifier
                .width(2.dp)
                .weight(1f)
                .background(
                    if (isLast) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
        )
    }
}

@Composable
private fun EventIcon(event: MatchTimelineEvent) {
    when {
        event.isGoal -> {
            Icon(
                painter = painterResource(com.zeyadmamoun.matchdetails.R.drawable.ic_football),
                contentDescription = if (event.isPenalty) "Penalty Goal" else "Goal",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(16.dp)
            )
        }
        event.isYellowRedCard -> {
            Icon(
                painter = painterResource(com.zeyadmamoun.matchdetails.R.drawable.ic_card_yellow_red),
                contentDescription = "Second Yellow Card",
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
        }
        event.isRedCard -> {
            Icon(
                painter = painterResource(com.zeyadmamoun.matchdetails.R.drawable.ic_card_red),
                contentDescription = "Red Card",
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
        }
        event.isYellowCard -> {
            Icon(
                painter = painterResource(com.zeyadmamoun.matchdetails.R.drawable.ic_card_yellow),
                contentDescription = "Yellow Card",
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
        }
        event.isSubstitution -> {
            Icon(
                painter = painterResource(com.zeyadmamoun.matchdetails.R.drawable.ic_substitute),
                contentDescription = "Substitution",
                tint = Color.Unspecified,
                modifier = Modifier.size(16.dp)
            )
        }
        else -> {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun EventContent(event: MatchTimelineEvent, isHome: Boolean) {
    val textAlign = if (isHome) TextAlign.End else TextAlign.Start
    val horizontalAlignment = if (isHome) Alignment.End else Alignment.Start

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (event.isSubstitution) {
            // For substitutions: only show the minute badge on top to avoid repeating the player name
            MinuteBadge(minute = event.minute)
            Spacer(Modifier.height(2.dp))
            val playerIn = event.subPlayerIn?.ifBlank { null } ?: event.playerName
            if (playerIn.isNotBlank()) {
                Text(
                    text = "▲ In: $playerIn",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = Color(0xFF1F9C20),
                    textAlign = textAlign,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (!event.subPlayerOut.isNullOrBlank()) {
                Text(
                    text = "▼ Out: ${event.subPlayerOut}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFD90D2D),
                    textAlign = textAlign,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            // Player Name + Minute
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isHome) Arrangement.End else Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isHome) {
                    Text(
                        text = event.playerName.ifBlank { event.text },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                        textAlign = TextAlign.End
                    )
                    Spacer(Modifier.width(6.dp))
                    MinuteBadge(minute = event.minute)
                } else {
                    MinuteBadge(minute = event.minute)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = event.playerName.ifBlank { event.text },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false),
                        textAlign = TextAlign.Start
                    )
                }
            }

            // Secondary details: Penalty label, Assist, 2nd Yellow
            when {
                event.isPenalty -> {
                    Text(
                        text = if (event.isGoal) "Penalty Scored" else "Penalty Missed",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (event.isGoal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        textAlign = textAlign
                    )
                }
                event.assistPlayerName != null -> {
                    Text(
                        text = "Assist: ${event.assistPlayerName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = textAlign
                    )
                }
                event.isYellowRedCard -> {
                    Text(
                        text = "2nd Yellow (Red Card)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFD90D2D),
                        textAlign = textAlign
                    )
                }
            }
        }
    }
}

@Composable
private fun MinuteBadge(minute: String) {
    if (minute.isBlank()) return
    val formatted = if (minute.endsWith("'")) minute else "$minute'"
    Text(
        text = formatted,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        softWrap = false
    )
}

@Composable
private fun PeriodDividerRow(label: String, score: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 1.dp
        )
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            val fullText = if (!score.isNullOrBlank()) "$label • $score" else label
            Text(
                text = fullText,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 1.dp
        )
    }
}

sealed interface TimelineDisplayItem {
    data class EventItem(val event: MatchTimelineEvent, val isHome: Boolean) : TimelineDisplayItem
    data class PeriodDividerItem(val label: String, val score: String? = null) : TimelineDisplayItem
}

/**
 * Builds chronological display items with period dividers (HT, FT, ET, AET).
 */
fun buildTimelineDisplayItems(
    events: List<MatchTimelineEvent>,
    matchDetails: MatchDetails
): List<TimelineDisplayItem> {
    if (events.isEmpty()) return emptyList()

    val homeTeamId = matchDetails.homeTeamId

    val selectedEvents = events.filter { event ->
        event.isGoal || event.isYellowCard || event.isYellowRedCard || event.isRedCard || event.isSubstitution
    }

    // Helper to parse minute integer for sorting
    fun parseMinute(minuteStr: String): Int {
        return minuteStr.takeWhile { it.isDigit() }.toIntOrNull() ?: 0
    }

    val sorted = selectedEvents.sortedBy { parseMinute(it.minute) }
    val result = mutableListOf<TimelineDisplayItem>()

    var htInserted = false
    var ftInserted = false
    var etHtInserted = false

    sorted.forEach { event ->
        if (event.isPeriodDivider) {
            result.add(TimelineDisplayItem.PeriodDividerItem(event.periodLabel ?: event.type, event.periodScore))
            return@forEach
        }

        val min = parseMinute(event.minute)

        // Insert HT divider before first event after minute 45
        if (!htInserted && min > 45) {
            result.add(TimelineDisplayItem.PeriodDividerItem("HT", "Half Time"))
            htInserted = true
        }

        // Insert FT divider before first event after minute 90 (Extra Time)
        if (!ftInserted && min > 90) {
            result.add(TimelineDisplayItem.PeriodDividerItem("FT", "Full Time"))
            ftInserted = true
        }

        // Insert ET HT divider after minute 105
        if (!etHtInserted && min > 105) {
            result.add(TimelineDisplayItem.PeriodDividerItem("ET HT", "Extra Time HT"))
            etHtInserted = true
        }

        val isHome = if (homeTeamId.isNotBlank() && event.teamId.isNotBlank()) {
            event.teamId == homeTeamId
        } else {
            // Fallback: check if teamName matches home team
            event.teamName.equals(matchDetails.homeTeamName, ignoreCase = true)
        }

        result.add(TimelineDisplayItem.EventItem(event = event, isHome = isHome))
    }

    // If match is finished or second half started but no event past 45 yet
    if (!htInserted && (matchDetails.isCompleted || matchDetails.currentMinute.takeWhile { it.isDigit() }.toIntOrNull() ?: 0 > 45)) {
        result.add(TimelineDisplayItem.PeriodDividerItem("HT", "Half Time"))
    }

    // If match is completed, append FT or AET
    if (matchDetails.isCompleted) {
        val hasExtraTime = sorted.any { parseMinute(it.minute) > 90 }
        if (hasExtraTime) {
            result.add(TimelineDisplayItem.PeriodDividerItem("AET", "${matchDetails.homeTeamScore} - ${matchDetails.awayTeamScore}"))
        } else {
            result.add(TimelineDisplayItem.PeriodDividerItem("FT", "${matchDetails.homeTeamScore} - ${matchDetails.awayTeamScore}"))
        }
    }

    return result
}

@Preview(showBackground = true)
@Composable
fun MatchTimelineCardPreview() {
    val sampleEvents = listOf(
        // 1. Regular Goal (Home)
        MatchTimelineEvent(
            id = "1",
            minute = "23'",
            type = "Goal",
            isGoal = true,
            playerName = "E. Haaland",
            assistPlayerName = "K. De Bruyne",
            teamId = "home"
        ),
        // 2. Yellow Card (Away)
        MatchTimelineEvent(
            id = "2",
            minute = "34'",
            type = "Yellow Card",
            isYellowCard = true,
            playerName = "Gabriel",
            teamId = "away"
        ),
        // Period Divider: HT
        MatchTimelineEvent(
            id = "ht",
            minute = "45'",
            type = "HT",
            isPeriodDivider = true,
            periodLabel = "HT",
            periodScore = "1 - 0"
        ),
        // 3. Penalty Goal (Away)
        MatchTimelineEvent(
            id = "3",
            minute = "57'",
            type = "Penalty Goal",
            isGoal = true,
            isPenalty = true,
            playerName = "B. Saka",
            teamId = "away"
        ),
        // 4. Substitution (Home)
        MatchTimelineEvent(
            id = "4",
            minute = "65'",
            type = "Substitution",
            isSubstitution = true,
            subPlayerIn = "P. Foden",
            subPlayerOut = "J. Grealish",
            teamId = "home"
        ),
        // 5. Second Yellow Card -> Red (Away)
        MatchTimelineEvent(
            id = "5",
            minute = "78'",
            type = "Second Yellow",
            isYellowRedCard = true,
            playerName = "Gabriel",
            teamId = "away"
        ),
        // 6. Direct Red Card (Home)
        MatchTimelineEvent(
            id = "6",
            minute = "84'",
            type = "Red Card",
            isRedCard = true,
            playerName = "Rodri",
            teamId = "home"
        ),
        // Period Divider: FT
        MatchTimelineEvent(
            id = "ft",
            minute = "90'",
            type = "FT",
            isPeriodDivider = true,
            periodLabel = "FT",
            periodScore = "1 - 1"
        ),
        // 7. Extra Time Goal (Home)
        MatchTimelineEvent(
            id = "7",
            minute = "104'",
            type = "Goal",
            isGoal = true,
            playerName = "J. Alvarez",
            teamId = "home"
        ),
        // Period Divider: ET HT
        MatchTimelineEvent(
            id = "et_ht",
            minute = "105'",
            type = "ET HT",
            isPeriodDivider = true,
            periodLabel = "ET HT",
            periodScore = "2 - 1"
        ),
        // Period Divider: AET (After Extra Time)
        MatchTimelineEvent(
            id = "aet",
            minute = "120'",
            type = "AET",
            isPeriodDivider = true,
            periodLabel = "AET",
            periodScore = "2 - 1"
        )
    )

    MaterialTheme {
        MatchTimelineCard(
            matchDetails = MatchDetails(
                homeTeamId = "home",
                homeTeamName = "Man City",
                homeTeamLogo = "https://media.api-sports.io/football/teams/50.png",
                homeTeamScore = "2",
                awayTeamId = "away",
                awayTeamName = "Arsenal",
                awayTeamLogo = "https://media.api-sports.io/football/teams/42.png",
                awayTeamScore = "1",
                isCompleted = true
            ),
            customEvents = sampleEvents
        )
    }
}
