package com.zeyadmamoun.korainsight.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        topBar = { KoraInsightsAppBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            KoraInsightNavDisplay(
                backStack = appState.backStack,
                modifier = Modifier.fillMaxSize()
            )

            // Subtle gradient scrim that gently fades content behind the floating bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            FloatingBottomNavBar(
                selectedIndex = appState.selectedBottomBarIndex,
                items = defaultBottomNavItems,
                onItemSelected = { index ->
                    appState.navigateToTopLevelTab(defaultBottomNavItems[index].route)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}
