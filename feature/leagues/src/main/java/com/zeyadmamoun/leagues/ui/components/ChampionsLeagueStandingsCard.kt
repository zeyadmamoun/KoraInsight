package com.zeyadmamoun.leagues.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zeyadmamoun.core.common.datetime.toLocalTimeString
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.scoreboard.Match
import com.zeyadmamoun.data.model.standings.LeagueStandings

enum class UclCardTab {
    Table,
    Matches
}

/**
 * Dedicated UEFA Champions League card supporting both a 16-row Standings Table
 * and active/upcoming Knockout or League Phase Matches via segmented pill tabs.
 *
 * @param league The [LeagueSlug] for the UEFA Champions League.
 * @param standing The [LeagueStandings] data for the Champions League.
 * @param matches The list of [Match] fixtures for the Champions League.
 * @param modifier Optional [Modifier] for this card.
 * @param onFullLeagueClicked Callback invoked when clicking "Full League".
 */
@Composable
fun ChampionsLeagueStandingsCard(
    league: LeagueSlug,
    standing: LeagueStandings,
    matches: List<Match> = emptyList(),
    modifier: Modifier = Modifier,
    onFullLeagueClicked: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(UclCardTab.Table) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LeagueHeader(
                league = league,
                standing = standing,
                onFullLeagueClicked = onFullLeagueClicked
            )

            // Segmented Pill Tab Bar (Table | Matches)
            UclSegmentedTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(vertical = 6.dp)
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            when (selectedTab) {
                UclCardTab.Table -> {
                    StandingsTable(
                        standing = standing,
                        maxRows = 16,
                        customLegend = {
                            UclQualificationLegend()
                        }
                    )
                }

                UclCardTab.Matches -> {
                    UclMatchesList(
                        matches = matches,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun UclSegmentedTabBar(
    selectedTab: UclCardTab,
    onTabSelected: (UclCardTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(3.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UclSegmentedTabItem(
            title = "Table",
            isSelected = selectedTab == UclCardTab.Table,
            onClick = { onTabSelected(UclCardTab.Table) },
            modifier = Modifier.weight(1f)
        )
        UclSegmentedTabItem(
            title = "Matches",
            isSelected = selectedTab == UclCardTab.Matches,
            onClick = { onTabSelected(UclCardTab.Matches) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UclSegmentedTabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        label = "tabBgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "tabTextColor"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}

@Composable
fun UclMatchesList(
    matches: List<Match>,
    modifier: Modifier = Modifier
) {
    if (matches.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Text(
                text = "No matches scheduled",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        matches.take(8).forEachIndexed { index, match ->
            UclMatchItem(match = match)
            if (index < matches.take(8).lastIndex) {
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun UclMatchItem(
    match: Match,
    modifier: Modifier = Modifier
) {
    val homeTeam = match.homeTeam
    val awayTeam = match.awayTeam

    val isFinished = match.status.completed || match.status.state.equals("post", ignoreCase = true)
    val isLive = match.status.state.equals("in", ignoreCase = true)

    val scoreDisplay = when {
        isFinished || isLive -> {
            val hScore = homeTeam?.score ?: "0"
            val aScore = awayTeam?.score ?: "0"
            "$hScore - $aScore"
        }
        else -> match.date.toLocalTimeString()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 2.dp)
    ) {
        // Optional Knockout Headline / Round Tag (e.g. "Round of 16 · 1st Leg")
        val roundBadge = match.roundTitle ?: match.leg
        if (!roundBadge.isNullOrBlank()) {
            Row(
                modifier = Modifier.padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = listOfNotNull(match.roundTitle, match.leg).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Teams & Score Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Team
            Text(
                text = homeTeam?.displayName ?: homeTeam?.name.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(6.dp))

            AsyncImage(
                model = homeTeam?.logo,
                contentDescription = homeTeam?.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(8.dp))

            // Score Pill
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when {
                            isLive -> Color(0xFFEF4444)
                            isFinished -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = scoreDisplay,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isLive || isFinished) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(8.dp))

            AsyncImage(
                model = awayTeam?.logo,
                contentDescription = awayTeam?.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(6.dp))

            // Away Team
            Text(
                text = awayTeam?.displayName ?: awayTeam?.name.orEmpty(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }

        // Aggregate note or penalty note if present
        val progressionNote = match.note
        if (!progressionNote.isNullOrBlank()) {
            Text(
                text = progressionNote,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp)
            )
        }
    }
}

@Composable
fun UclQualificationLegend(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp, start = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF81D6AC))
            )
            Text(
                text = "1-8 Round of 16",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFB5E7CE))
            )
            Text(
                text = "9-24 Knockout Play-offs",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
