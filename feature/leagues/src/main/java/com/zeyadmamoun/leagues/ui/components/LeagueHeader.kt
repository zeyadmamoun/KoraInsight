package com.zeyadmamoun.leagues.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.standings.LeagueStandings

@Composable
fun LeagueHeader(
    league: LeagueSlug,
    standing: LeagueStandings,
    modifier: Modifier = Modifier,
    onFullLeagueClicked: () -> Unit = {}
) {
    val numberOfRounds = if (league.slug.contains("champions", ignoreCase = true)) {
        8
    } else {
        (standing.entries.size * 2) - 2
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = if (isSystemInDarkTheme()) league.darkLogoUrl ?: league.logoUrl else league.logoUrl,
            contentDescription = league.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(4.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = league.name,
                style = MaterialTheme.typography.labelMedium
            )
            val roundSubtitle = if (standing.currentRound > 0) {
                "Round ${standing.currentRound} of $numberOfRounds - ${standing.seasonYear} / ${standing.seasonYear?.plus(1)}"
            } else {
                "${standing.seasonDisplayName ?: "League Phase"} - ${standing.seasonYear} / ${standing.seasonYear?.plus(1)}"
            }
            Text(
                text = roundSubtitle,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onFullLeagueClicked()
                }
        ) {
            Text(
                text = "Full League",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
