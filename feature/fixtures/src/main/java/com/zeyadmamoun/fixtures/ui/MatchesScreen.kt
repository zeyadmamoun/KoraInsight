package com.zeyadmamoun.fixtures.ui

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zeyadmamoun.core.common.datetime.toRelativeChipString
import kotlinx.datetime.LocalDate

@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel = viewModel()
) {
    val daysList by viewModel.nearbyDays.collectAsStateWithLifecycle()
    val selectedDay by viewModel.selectedDay.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DateNavigationChips(
            daysList = daysList,
            selectedDay = selectedDay
        ) { day ->
            viewModel.updateSelectedDay(day)
        }
    }
}

@Composable
fun DateNavigationChips(
    daysList: List<LocalDate>,
    selectedDay: LocalDate,
    onChipClicked: (LocalDate) -> Unit
) {
    val initialIndex = remember(selectedDay, daysList) {
        (daysList.indexOf(selectedDay) - 1).coerceAtLeast(0)
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(selectedDay) {
        val targetIndex = daysList.indexOf(selectedDay)
        if (targetIndex != -1) {
            var itemInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
            if (itemInfo == null) {
                listState.animateScrollToItem(targetIndex)
                itemInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == targetIndex }
            }

            if (itemInfo != null) {
                val viewportWidth = listState.layoutInfo.viewportSize.width
                val itemCenter = itemInfo.offset + (itemInfo.size / 2)
                val viewportCenter = viewportWidth / 2
                val scrollDelta = itemCenter - viewportCenter
                listState.animateScrollBy(scrollDelta.toFloat())
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(
            items = daysList,
            key = { _, day -> day.toEpochDays() },
            contentType = { _, _ -> "date_chip" }
        ) { _, day ->
            DateChip(
                day = day,
                isSelectedProvider = { day == selectedDay },
                onClick = { onChipClicked(day) }
            )
        }
    }
}

@Composable
private fun DateChip(
    day: LocalDate,
    isSelectedProvider: () -> Boolean,
    onClick: () -> Unit
) {
    val isSelected = isSelectedProvider()
    ElevatedAssistChip(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        label = {
            Text(
                text = day.toRelativeChipString(),
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface,
            labelColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview
@Composable
fun MatchesScreenPreview() {
    MatchesScreen()
}