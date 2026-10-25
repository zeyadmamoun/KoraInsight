package com.zeyadmamoun.korainsight.ui.navigation.bottomBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.fixtures.navigation.MatchesRoute
import com.zeyadmamoun.leagues.navigation.LeaguesRoute
import com.zeyadmamoun.profile.navigation.ProfileRoute

/**
 * Model representing a bottom navigation item.
 *
 * @param route The destination key for Navigation 3.
 * @param title The display title for the item.
 * @param selectedIcon Icon displayed when this item is selected.
 * @param unselectedIcon Icon displayed when this item is not selected.
 */
data class BottomNavItem(
    val route: NavKey,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

/**
 * Default items for Matches, Leagues, and Profile tabs.
 */
val defaultBottomNavItems = listOf(
    BottomNavItem(
        route = MatchesRoute,
        title = "Matches",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange,
    ),
    BottomNavItem(
        route = LeaguesRoute,
        title = "Leagues",
        selectedIcon = Icons.Filled.Star,
        unselectedIcon = Icons.Outlined.Star,
    ),
    BottomNavItem(
        route = ProfileRoute,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
)
