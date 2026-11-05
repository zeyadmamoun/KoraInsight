package com.zeyadmamoun.leagues.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.model.standings.LeagueStandings

@Composable
fun LeagueStandingsCard(
    leagues: List<LeagueSlug>,
    leagueStandings: List<LeagueStandings>,
    modifier: Modifier = Modifier
) {
    var selectedLeagueIndex by remember { mutableIntStateOf(0) }
    val selectedLeagueStanding by remember(selectedLeagueIndex, leagueStandings, leagues) {
        mutableStateOf(
            if (leagues.isNotEmpty() && selectedLeagueIndex in leagues.indices) {
                leagueStandings.find { it.leagueUid == leagues[selectedLeagueIndex].leagueUid }
            } else null
        )
    }

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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Chips to select the league
            LeagueSelector(
                leagues = leagues,
                selectedLeagueIndex = selectedLeagueIndex,
                onChipSelected = {
                    selectedLeagueIndex = it
                }
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            selectedLeagueStanding?.let { standing ->
                LeagueHeader(
                    league = leagues[selectedLeagueIndex],
                    standing = standing
                )

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )

                StandingsTable(
                    standing = standing,
                    maxRows = 6
                )
            }
        }
    }
}
