package com.zeyadmamoun.leagues.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug

@Composable
fun SelectableLeagueChip(
    league: LeagueSlug,
    isSelected: Boolean,
    onChipSelected: () -> Unit
) {
    AssistChip(
        onClick = { onChipSelected() },
        label = { Text(league.name, style = MaterialTheme.typography.labelSmall) },
        shape = RoundedCornerShape(16.dp),
        leadingIcon = {
            AsyncImage(
                model = if (isSystemInDarkTheme()) league.darkLogoUrl else league.logoUrl,
                contentDescription = league.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    )
}
