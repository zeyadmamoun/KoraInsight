package com.zeyadmamoun.leagues.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug

/**
 * Horizontal scrolling row of league logo cards with a section title.
 * Corresponds to Figma node 462:6775.
 *
 * @param leagues List of [LeagueSlug] items to display.
 * @param modifier Optional [Modifier] for this component.
 * @param onLeagueClick Callback invoked when a league card is clicked.
 */
@Composable
fun LeaguesHorizontalScroll(
    leagues: List<LeagueSlug>,
    modifier: Modifier = Modifier,
    onLeagueClick: (LeagueSlug) -> Unit = {}
) {
    if (leagues.isEmpty()) return

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "League",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 0.32.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(
                items = leagues,
                key = { it.slug }
            ) { league ->
                LeagueLogoCard(
                    league = league,
                    onClick = { onLeagueClick(league) }
                )
            }
        }
    }
}

/**
 * Individual league logo card matching Figma spec:
 * Rounded card with subtle elevation and centered league icon.
 */
@Composable
fun LeagueLogoCard(
    league: LeagueSlug,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = modifier.size(64.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = if (isSystemInDarkTheme()) {
                    league.darkLogoUrl ?: league.logoUrl
                } else {
                    league.logoUrl
                },
                contentDescription = league.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaguesHorizontalScrollPreview() {
    val sampleLeagues = listOf(
        LeagueSlug(id = 1, slug = "eng.1", name = "Premier League", logoUrl = "https://a.espncdn.com/i/leaguelogos/soccer/500/23.png"),
        LeagueSlug(id = 2, slug = "esp.1", name = "La Liga", logoUrl = "https://a.espncdn.com/i/leaguelogos/soccer/500/15.png"),
        LeagueSlug(id = 3, slug = "fra.1", name = "Ligue 1", logoUrl = "https://a.espncdn.com/i/leaguelogos/soccer/500/9.png"),
        LeagueSlug(id = 4, slug = "ita.1", name = "Serie A", logoUrl = "https://a.espncdn.com/i/leaguelogos/soccer/500/12.png")
    )
    MaterialTheme {
        LeaguesHorizontalScroll(leagues = sampleLeagues)
    }
}
