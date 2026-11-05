package com.zeyadmamoun.leagues.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug

@Composable
fun LeagueSelector(
    leagues: List<LeagueSlug>,
    selectedLeagueIndex: Int,
    onChipSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(leagues.size) { index ->
            SelectableLeagueChip(
                league = leagues[index],
                isSelected = selectedLeagueIndex == index,
                onChipSelected = { onChipSelected(index) }
            )
        }
    }
}
