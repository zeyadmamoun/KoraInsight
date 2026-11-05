package com.zeyadmamoun.leagues.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.leagues.ui.components.LeagueStandingsCard
import com.zeyadmamoun.leagues.ui.components.LeaguesErrorPlaceholder
import com.zeyadmamoun.leagues.ui.components.SelectableLeagueChip
import com.zeyadmamoun.leagues.ui.utils.LeaguesNetworkStatus

@Composable
fun LeagueScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: LeaguesViewModel = viewModel()
) {
    val state by viewModel.leaguesUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when (val status = state.networkStatus) {
            is LeaguesNetworkStatus.Loading -> {
                CircularProgressIndicator()
            }

            is LeaguesNetworkStatus.Error -> {
                LeaguesErrorPlaceholder(
                    message = status.message,
                    onRetry = { viewModel.getLeagues() }
                )
            }

            is LeaguesNetworkStatus.Success -> {
                if (state.standingsCardData == null || state.leagues.isEmpty()) {
                    LeaguesErrorPlaceholder(
                        message = "No standings available",
                        onRetry = { viewModel.getLeagues() }
                    )
                } else {
                    LeagueStandingsCard(
                        leagues = state.standingsCardData?.cardLeagues.orEmpty(),
                        leagueStandings = state.standingsCardData?.standings.orEmpty()
                    )
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Composable
fun LeagueScreenPreview() {
    var isSelected by remember { mutableStateOf(false) }
    SelectableLeagueChip(
        league = LeagueSlug(
            id = 1,
            slug = "league-slug",
            name = "Premier League",
            logoUrl = "https://a.espncdn.com/i/leaguelogos/soccer/500/23.png",
            darkLogoUrl = "https://a.espncdn.com/i/leaguelogos/soccer/500-dark/23.png"
        ),
        isSelected = isSelected,
        onChipSelected = {
            isSelected = !isSelected
        }
    )
}
