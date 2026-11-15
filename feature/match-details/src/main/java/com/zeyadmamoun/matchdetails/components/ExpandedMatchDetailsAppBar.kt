package com.zeyadmamoun.matchdetails.components

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun ExpandedMatchDetailsAppBar(
    modifier: Modifier = Modifier,
    matchDetails: MatchDetails = MatchDetails(),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Teams' logos and scores
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TeamBadge(
                logoUrl = matchDetails.homeTeamLogo,
                teamName = matchDetails.homeTeamName,
                teamLabel = "Home",
                modifier = Modifier.weight(1f)
            )
            ScoreTextOrDateText(
                mainText = matchDetails.scoreText,
                minutesPlayed = matchDetails.currentMinute,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(Modifier.width(8.dp))
            TeamBadge(
                logoUrl = matchDetails.awayTeamLogo,
                teamName = matchDetails.awayTeamName,
                teamLabel = "Away",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))

        val showGoalsList = matchDetails.events.any { it.isGoal }

        // Goals list for both teams
        if (showGoalsList){
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                GoalsList(
                    matchDetails.homeTeamId,
                    matchDetails.events,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                Icon(
                    painter = painterResource(com.zeyadmamoun.matchdetails.R.drawable.ic_football),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                GoalsList(
                    matchDetails.awayTeamId,
                    matchDetails.events,
                    modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TeamBadge(
    logoUrl: String,
    teamName: String,
    teamLabel: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier.wrapContentSize()
    ) {
        AsyncImage(
            model = logoUrl,
            placeholder = painterResource(R.drawable.flag),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(52.dp)
        )
        Text(
            text = teamName,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = teamLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ScoreTextOrDateText(
    modifier: Modifier = Modifier,
    mainText: String = "VS",
    minutesPlayed: String = "",
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.wrapContentSize()
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )
        if (minutesPlayed.isNotBlank()) {
            MatchMinuteIndicator(minutesPlayed)
        }
    }
}

@Composable
fun GoalsList(
    teamId: String,
    events: List<MatchTimelineEvent>,
    modifier: Modifier = Modifier
) {
    val goals = events.filter { it.teamId == teamId && it.isGoal }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        goals.forEach { goal ->
            Text(
                text = "${goal.playerName} ${goal.minute}'",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpandedMatchDetailsAppBarPreview() {
    MaterialTheme {
        ExpandedMatchDetailsAppBar(
            matchDetails = MatchDetails(
                homeTeamName = "Man City",
                awayTeamName = "Liverpool",
                homeTeamScore = "2",
                awayTeamScore = "1",
                isLive = true,
                currentMinute = "67'"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TeamBadgePreview() {
    TeamBadge(
        logoUrl = "https://media.api-sports.io/football/teams/40.png",
        teamName = "Man City",
        teamLabel = "Home"
    )
}
