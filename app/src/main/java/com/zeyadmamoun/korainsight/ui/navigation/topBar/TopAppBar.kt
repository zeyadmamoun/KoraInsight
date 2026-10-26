package com.zeyadmamoun.korainsight.ui.navigation.topBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KoraInsightsAppBar(
    searchIconClicked: () -> Unit = {},
    notificationIconClicked: () -> Unit = {}
){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(),
        title = {
            KoraInsightsLogo()
        },
        modifier = Modifier.fillMaxWidth(),
        actions = {
            IconButton(
                onClick = searchIconClicked
            ) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
            IconButton(
                onClick = notificationIconClicked
            ) {
                Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
            }
        }
    )
}

@Composable
fun KoraInsightsLogo(){
    Row {
        Text(
            text = "Kora Insights",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .padding(bottom = 5.dp)
                .size(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )
    }
}

@Preview
@Composable
fun KoraInsightsAppBarPreview(){
    KoraInsightsAppBar()
}