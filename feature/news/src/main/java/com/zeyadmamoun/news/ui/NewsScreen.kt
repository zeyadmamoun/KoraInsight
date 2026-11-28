package com.zeyadmamoun.news.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.data.model.news.NewsArticle
import com.zeyadmamoun.news.ui.components.CompactNewsCard
import com.zeyadmamoun.news.ui.components.EmptyNewsPlaceholder
import com.zeyadmamoun.news.ui.components.ErrorNewsPlaceholder
import com.zeyadmamoun.news.ui.components.FeaturedNewsCard
import com.zeyadmamoun.news.ui.components.NewsLeagueFilterRow
import com.zeyadmamoun.news.ui.components.NewsPaginationBar
import com.zeyadmamoun.news.ui.components.newsShimmerLoading
import com.zeyadmamoun.news.ui.components.rememberNewsShimmerBrush
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

/**
 * Stateful news feed screen wired to [NewsViewModel].
 *
 * @param onArticleClick Reserved callback for navigating to an article details screen.
 */
@Composable
fun NewsScreen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: NewsViewModel = hiltViewModel(),
    onArticleClick: (articleId: String, leagueName: String) -> Unit = { _, _ -> },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NewsScreenContent(
        state = state,
        paddingValues = paddingValues,
        onRefresh = viewModel::refresh,
        onRetry = { viewModel.loadNews() },
        onLeagueSelected = viewModel::selectLeague,
        onPageSelected = viewModel::selectPage,
        onArticleClick = { article ->
            val leagueName = state.leagueNamesBySlug[article.leagueSlug] ?: article.leagueSlug
            onArticleClick(article.id, leagueName)
        }
    )
}

/**
 * Stateless news feed content matching the Light and Dark Figma layouts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsScreenContent(
    state: NewsUiState,
    paddingValues: PaddingValues = PaddingValues(),
    onRefresh: () -> Unit = {},
    onRetry: () -> Unit = {},
    onLeagueSelected: (String?) -> Unit = {},
    onPageSelected: (Int) -> Unit = {},
    onArticleClick: (NewsArticle) -> Unit = {},
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val shimmerBrush = rememberNewsShimmerBrush()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(12.dp))

            NewsLeagueFilterRow(
                chips = state.chips,
                selectedLeagueSlug = state.selectedLeagueSlug,
                onChipSelected = { slug ->
                    onLeagueSelected(slug)
                    coroutineScope.launch { listState.animateScrollToItem(0) }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 116.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (val status = state.networkStatus) {
                    is NetworkStatus.Loading -> {
                        newsShimmerLoading(brush = shimmerBrush)
                    }

                    is NetworkStatus.Error -> {
                        item(key = "error") {
                            ErrorNewsPlaceholder(
                                message = status.message,
                                onRetry = onRetry
                            )
                        }
                    }

                    is NetworkStatus.Success -> {
                        if (state.pagedArticles.isEmpty()) {
                            item(key = "empty") {
                                EmptyNewsPlaceholder()
                            }
                        } else {
                            itemsIndexed(
                                items = state.pagedArticles,
                                key = { _, article -> article.id },
                                contentType = { index, _ ->
                                    if (index == 0) "featured_article" else "compact_article"
                                }
                            ) { index, article ->
                                val leagueLabel = state.leagueNamesBySlug[article.leagueSlug]
                                    ?: article.leagueSlug
                                if (index == 0) {
                                    FeaturedNewsCard(
                                        article = article,
                                        leagueName = leagueLabel,
                                        onClick = { onArticleClick(article) }
                                    )
                                } else {
                                    CompactNewsCard(
                                        article = article,
                                        leagueName = leagueLabel,
                                        onClick = { onArticleClick(article) }
                                    )
                                }
                            }

                            item(key = "pagination", contentType = "pagination") {
                                Spacer(modifier = Modifier.height(4.dp))
                                NewsPaginationBar(
                                    currentPage = state.currentPage,
                                    totalPages = state.totalPages,
                                    onPageSelected = { page ->
                                        onPageSelected(page)
                                        coroutineScope.launch { listState.animateScrollToItem(0) }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private val PreviewArticles = listOf(
    NewsArticle(
        id = "1",
        headline = "Levante vs. Racing Santander – Game Highlights",
        summary = "Watch the Game Highlights from Levante vs. Racing Santander, 11/30/2026",
        publishedAt = Clock.System.now() - 2.hours,
        leagueSlug = "esp.1",
        type = "Media"
    ),
    NewsArticle(
        id = "2",
        headline = "The title race: three things to watch this weekend",
        summary = "The key fixtures and talking points.",
        publishedAt = Clock.System.now() - 3.hours,
        leagueSlug = "eng.1",
        type = "Story"
    ),
    NewsArticle(
        id = "3",
        headline = "Champions League: the games that could decide it",
        summary = "Everything to know before kick-off.",
        publishedAt = Clock.System.now() - 4.hours,
        leagueSlug = "uefa.champions",
        type = "Story"
    ),
    NewsArticle(
        id = "4",
        headline = "Inside the tactics: how a high press changes the game",
        summary = "A closer look at the tactical battle.",
        publishedAt = Clock.System.now() - 5.hours,
        leagueSlug = "esp.1",
        type = "Story"
    )
)

private val PreviewUiState = NewsUiState(
    chips = listOf(
        NewsLeagueChip(null, "All"),
        NewsLeagueChip("esp.1", "LaLiga"),
        NewsLeagueChip("eng.1", "Premier League"),
        NewsLeagueChip("uefa.champions", "UCL")
    ),
    selectedLeagueSlug = null,
    pagedArticles = PreviewArticles,
    leagueNamesBySlug = mapOf(
        "esp.1" to "LaLiga",
        "eng.1" to "Premier League",
        "uefa.champions" to "UCL"
    ),
    currentPage = 1,
    totalPages = 5,
    networkStatus = NetworkStatus.Success
)

@Preview(uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun NewsScreenLightPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF3463CC),
            onPrimary = Color.White,
            background = Color(0xFFF6F8FE),
            surface = Color.White,
            onSurface = Color(0xFF1E242B),
            surfaceVariant = Color(0xFFE2E5EB),
            onSurfaceVariant = Color(0xFF8D98A5),
            outline = Color(0xFFE2E8F0)
        )
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NewsScreenContent(state = PreviewUiState)
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun NewsScreenDarkPreview() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF3463CC),
            onPrimary = Color.White,
            background = Color(0xFF0F141C),
            surface = Color(0xFF181F2A),
            onSurface = Color(0xFFE6EDF5),
            surfaceVariant = Color(0xFF303B4D),
            onSurfaceVariant = Color(0xFFA1ACBB),
            outline = Color(0xFF3A4658)
        )
    ) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NewsScreenContent(state = PreviewUiState)
        }
    }
}
