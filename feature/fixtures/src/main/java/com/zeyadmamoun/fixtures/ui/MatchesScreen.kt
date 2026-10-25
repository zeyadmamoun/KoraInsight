package com.zeyadmamoun.fixtures.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun MatchesScreen(
    paddingValues: PaddingValues = PaddingValues()
){
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun DateNavigationChips(){
//    val now = Clock.System.now()
//    val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
}

@Preview
@Composable
fun MatchesScreenPreview(){
    MatchesScreen()
}