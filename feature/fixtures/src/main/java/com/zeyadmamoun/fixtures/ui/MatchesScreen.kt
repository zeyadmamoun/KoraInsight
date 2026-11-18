package com.zeyadmamoun.fixtures.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.data.model.scoreboard.Match
import com.zeyadmamoun.data.model.scoreboard.Scoreboard
import com.zeyadmamoun.data.model.scoreboard.Status
import com.zeyadmamoun.fixtures.ui.components.DateNavigationChips
import com.zeyadmamoun.fixtures.ui.components.LeagueDropDownScheduleCard
import com.zeyadmamoun.fixtures.ui.components.MatchRecord
import com.zeyadmamoun.fixtures.ui.components.matchesShimmerLoading
import com.zeyadmamoun.fixtures.ui.components.rememberShimmerBrush

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel = viewModel(),
    onMatchClicked: (String, String) -> Unit = {_, _ ->}
) {
    val state by viewModel.matchesUiState.collectAsStateWithLifecycle()
    val shimmerBrush = rememberShimmerBrush()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                DateNavigationChips(
                    daysList = state.nearbyDays,
                    selectedDay = state.selectedDay
                ) { day ->
                    viewModel.updateSelectedDay(day)
                }
            }
            item {
                Spacer(Modifier.height(8.dp))
            }

            when (val status = state.networkStatus) {
                is NetworkStatus.Loading -> {
                    matchesShimmerLoading(brush = shimmerBrush, count = 3)
                }
                is NetworkStatus.Success -> {
                    if (state.scoreboards.isEmpty()) {
                        item {
                            EmptyMatchesPlaceholder()
                        }
                    } else {
                        itemsIndexed(
                            items = state.scoreboards,
                            key = { index, scoreboard -> scoreboard.leagues.firstOrNull()?.slug ?: index },
                            contentType = { _, _ -> "league_card" }
                        ) { _, scoreboard ->
                            LeagueDropDownScheduleCard(scoreboard,onMatchClicked)
                        }
                    }
                }
                is NetworkStatus.Error -> {
                    item {
                        ErrorMatchesPlaceholder(
                            message = status.message,
                            onRetry = { viewModel.getScoreboards() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyMatchesPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No matches scheduled for this date",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorMatchesPlaceholder(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        ElevatedAssistChip(
            onClick = onRetry,
            label = { Text("Retry") }
        )
    }
}

@Preview
@Composable
fun MatchRecordPreview(){
    MatchRecord(
        Match(
            "2",
            name = "name",
            date = "20261101",
            status = Status(1359, "status", name = "status name", state = "ss", description = "desc")
        ),
        league = "esp.1"
    )
}

@Preview(showSystemUi = true)
@Composable
fun MatchesScreenPreview() {
    LeagueDropDownScheduleCard(Scoreboard())
}
