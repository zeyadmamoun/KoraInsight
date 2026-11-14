package com.zeyadmamoun.matchdetails.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zeyadmamoun.core.R
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.data.model.summary.toMatchScore

@Composable
fun CollapsedMatchDetailsAppBar(
    matchDetails: MatchDetails = MatchDetails()
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.weight(0.5f))
        // home team logo
        AsyncImage(
            model = matchDetails.homeTeamLogo.ifBlank { "https://media.api-sports.io/football/teams/40.png" },
            placeholder = painterResource(R.drawable.flag),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.weight(0.5f))
        // match score / kickoff time
        Text(
            text = matchDetails.toMatchScore(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.weight(0.5f))
        //away team logo
        AsyncImage(
            model = matchDetails.awayTeamLogo.ifBlank { "https://media.api-sports.io/football/teams/40.png" },
            placeholder = painterResource(R.drawable.flag),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.weight(1f))
        if (matchDetails.isLive) {
            MatchMinuteIndicator(minute = matchDetails.currentMinute)
        }
    }
}

/**
 * Displays the current match minute with a primary-colored indicator bar underneath.
 *
 * @param minute Formatted match clock text (e.g. "81’").
 * @param modifier Optional modifier for the indicator container.
 */
@Composable
fun MatchMinuteIndicator(
    minute: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = minute,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 14.sp,
                lineHeight = 19.6.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp
            )
        )
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(2.dp)
                .background(MaterialTheme.colorScheme.primary)
        )
    }
}