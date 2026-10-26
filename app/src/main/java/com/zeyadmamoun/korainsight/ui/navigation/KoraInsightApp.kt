package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zeyadmamoun.korainsight.ui.navigation.bottomBar.FloatingBottomNavBar
import com.zeyadmamoun.korainsight.ui.navigation.bottomBar.defaultBottomNavItems
import com.zeyadmamoun.korainsight.ui.navigation.topBar.KoraInsightsAppBar

@Composable
fun KoraInsightApp(
    appState: KoraAppState = rememberKoraAppState()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { KoraInsightsAppBar() },
        bottomBar = {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                FloatingBottomNavBar(
                    selectedIndex = appState.selectedBottomBarIndex,
                    items = defaultBottomNavItems,
                    onItemSelected = { index ->
                        appState.navigateToTopLevelTab(defaultBottomNavItems[index].route)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        KoraInsightNavDisplay(
            backStack = appState.backStack,
            paddingValues = innerPadding
        )
    }
}