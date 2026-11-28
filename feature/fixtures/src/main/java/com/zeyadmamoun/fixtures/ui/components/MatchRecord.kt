package com.zeyadmamoun.fixtures.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.zeyadmamoun.data.model.scoreboard.Match
import com.zeyadmamoun.fixtures.util.isLive
import com.zeyadmamoun.fixtures.util.toDisplayBadge
import com.zeyadmamoun.fixtures.util.toScoreText

@Composable
fun MatchRecord(
    match: Match,
    league: String,
    onRecordClicked: (String, String) -> Unit = {_, _ ->}
){
    val homeTeam = remember(match.id) { match.homeTeam }
    val awayTeam = remember(match.id) { match.awayTeam }
    val scoreText = remember(match.id) { match.toScoreText() }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 14.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    onRecordClicked(match.id, league)
                }
            )
    ) {
        // Match status badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(24.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (match.status.isLive()) Color(0xFFE53935)
                    else MaterialTheme.colorScheme.primary
                )
                .padding(horizontal = 2.dp)
        ) {
            Text(
                text = match.status.toDisplayBadge(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 6.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Home team name (strict size & trimmed)
        Text(
            text = homeTeam?.displayName ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Home team logo
        AsyncImage(
            model = homeTeam?.logo,
            contentDescription = match.homeTeam?.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Score / Time box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .widthIn(min = 44.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = scoreText,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Away team logo
        AsyncImage(
            model = awayTeam?.logo,
            contentDescription = match.awayTeam?.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Away team name (strict size & trimmed)
        Text(
            text = awayTeam?.displayName ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.width(80.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
