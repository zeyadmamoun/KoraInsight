package com.zeyadmamoun.news.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zeyadmamoun.news.ui.NewsScreen

fun EntryProviderScope<NavKey>.newsEntryProvider() {
    entry<NewsRoute> {
        NewsScreen()
    }
}