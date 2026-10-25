package com.zeyadmamoun.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.profile.ui.ProfileScreen

fun EntryProviderScope<NavKey>.profileEntryProvider(){
    entry<ProfileRoute> {
        ProfileScreen()
    }
}