package com.zeyadmamoun.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.news.NewsArticle
import com.zeyadmamoun.data.model.scoreboard.LeagueSlug
import com.zeyadmamoun.data.repository.LeaguesRepository
import com.zeyadmamoun.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.ceil

/**
 * Filter chip representation for the news feed header.
 *
 * @property slug League slug (e.g. `"eng.1"`), or `null` for the `"All"` chip.
 * @property label Human-readable league title shown inside the chip.
 */
data class NewsLeagueChip(
    val slug: String?,
    val label: String,
)

/**
 * Immutable UI state for [NewsScreen].
 */
data class NewsUiState(
    val chips: List<NewsLeagueChip> = listOf(NewsLeagueChip(slug = null, label = "All")),
    val selectedLeagueSlug: String? = null,
    val pagedArticles: List<NewsArticle> = emptyList(),
    val leagueNamesBySlug: Map<String, String> = emptyMap(),
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val networkStatus: NetworkStatus = NetworkStatus.Loading,
    val isRefreshing: Boolean = false,
)

/**
 * Manages the football news feed, league chip filtering, and in-memory pagination.
 *
 * All fetched articles (~200+ flat [NewsArticle] items cached by [NewsRepository]) are kept
 * in memory so switching league chips or page numbers updates immediately without network calls.
 */
@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val leaguesRepository: LeaguesRepository,
) : ViewModel() {

    private var fetchJob: Job? = null
    private var allArticles: List<NewsArticle> = emptyList()
    private var leagues: List<LeagueSlug> = emptyList()

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    /** Triggers a pull-to-refresh reload bypassing the repository's short-lived cache. */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadNews(isRefresh = true)
    }

    /** Loads featured leagues and then fetches the merged news feed across them. */
    fun loadNews(isRefresh: Boolean = false) {
        fetchJob?.cancel()
        if (!isRefresh) {
            _uiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
        }

        fetchJob = viewModelScope.launch {
            leaguesRepository.getLeagueSlugs().collect { leaguesResult ->
                when (leaguesResult) {
                    is Result.Loading -> {
                        if (!isRefresh) {
                            _uiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
                        }
                    }

                    is Result.Success -> {
                        leagues = leaguesResult.data
                        collectNewsArticles(isRefresh = isRefresh)
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Error(
                                    leaguesResult.message ?: "Failed to load leagues"
                                ),
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun collectNewsArticles(isRefresh: Boolean) {
        newsRepository.getNews(
            leagueSlugs = leagues,
            isForceRefresh = isRefresh
        ).collect { newsResult ->
            when (newsResult) {
                is Result.Loading -> {
                    if (!isRefresh) {
                        _uiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
                    }
                }

                is Result.Success -> {
                    allArticles = newsResult.data
                    val leagueNames = leagues.associate { it.slug to it.name }
                    val chips = buildChips(allArticles, leagues, leagueNames)

                    _uiState.update { current ->
                        val activeSlug = current.selectedLeagueSlug?.takeIf { slug ->
                            chips.any { it.slug == slug }
                        }
                        val targetPage = if (isRefresh) current.currentPage else 1
                        applyFilterAndPage(
                            base = current.copy(
                                chips = chips,
                                leagueNamesBySlug = leagueNames,
                                networkStatus = NetworkStatus.Success,
                                isRefreshing = false
                            ),
                            selectedSlug = activeSlug,
                            requestedPage = targetPage
                        )
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            networkStatus = NetworkStatus.Error(
                                newsResult.message ?: "Failed to fetch news"
                            ),
                            isRefreshing = false
                        )
                    }
                }
            }
        }
    }

    /** Filters the feed to [slug] (`null` for `"All"`) and resets to page 1. */
    fun selectLeague(slug: String?) {
        _uiState.update { current ->
            if (current.selectedLeagueSlug == slug) return@update current
            applyFilterAndPage(
                base = current,
                selectedSlug = slug,
                requestedPage = 1
            )
        }
    }

    /** Navigates to [page] within the currently filtered article list. */
    fun selectPage(page: Int) {
        _uiState.update { current ->
            val clamped = page.coerceIn(1, current.totalPages)
            if (current.currentPage == clamped) return@update current
            applyFilterAndPage(
                base = current,
                selectedSlug = current.selectedLeagueSlug,
                requestedPage = clamped
            )
        }
    }

    private fun applyFilterAndPage(
        base: NewsUiState,
        selectedSlug: String?,
        requestedPage: Int
    ): NewsUiState {
        val filtered = if (selectedSlug == null) {
            allArticles
        } else {
            allArticles.filter { it.leagueSlug == selectedSlug }
        }
        val totalPages = if (filtered.isEmpty()) 1 else ceil(filtered.size / PAGE_SIZE.toDouble()).toInt()
        val safePage = requestedPage.coerceIn(1, totalPages)
        val startIndex = (safePage - 1) * PAGE_SIZE
        val endIndex = (startIndex + PAGE_SIZE).coerceAtMost(filtered.size)
        val pageItems = if (startIndex < filtered.size) {
            filtered.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return base.copy(
            selectedLeagueSlug = selectedSlug,
            pagedArticles = pageItems,
            currentPage = safePage,
            totalPages = totalPages
        )
    }

    private fun buildChips(
        articles: List<NewsArticle>,
        orderedLeagues: List<LeagueSlug>,
        leagueNames: Map<String, String>
    ): List<NewsLeagueChip> {
        val presentSlugs = articles.map { it.leagueSlug }.toSet()
        val orderedPresent = orderedLeagues
            .map { it.slug }
            .filter { it in presentSlugs }
        val extraSlugs = presentSlugs.filter { it !in orderedPresent }

        val leagueChips = (orderedPresent + extraSlugs).map { slug ->
            NewsLeagueChip(
                slug = slug,
                label = leagueNames[slug] ?: slug
            )
        }
        return listOf(NewsLeagueChip(slug = null, label = "All")) + leagueChips
    }

    private companion object {
        const val PAGE_SIZE = 15
    }
}
