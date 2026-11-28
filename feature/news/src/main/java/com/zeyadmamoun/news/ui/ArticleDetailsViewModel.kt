package com.zeyadmamoun.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.core.common.result.NetworkStatus
import com.zeyadmamoun.core.common.result.Result
import com.zeyadmamoun.data.model.news.ArticleDetails
import com.zeyadmamoun.data.repository.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Immutable UI state for [ArticleDetailsScreen].
 */
data class ArticleDetailsUiState(
    val networkStatus: NetworkStatus = NetworkStatus.Loading,
    val article: ArticleDetails? = null,
    val isRefreshing: Boolean = false,
)

/**
 * Loads full details for a single article directly from [NewsRepository] without caching.
 */
@HiltViewModel(assistedFactory = ArticleDetailsViewModel.Factory::class)
class ArticleDetailsViewModel @AssistedInject constructor(
    @Assisted("articleId") private val articleId: String,
    @Assisted("leagueName") private val leagueName: String,
    private val newsRepository: NewsRepository,
) : ViewModel() {

    private var fetchJob: Job? = null

    private val _uiState = MutableStateFlow(ArticleDetailsUiState())
    val uiState: StateFlow<ArticleDetailsUiState> = _uiState.asStateFlow()

    init {
        loadArticleDetails()
    }

    /** Triggers a pull-to-refresh reload of the article details. */
    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadArticleDetails(isRefresh = true)
    }

    /** Fetches fresh article details from [NewsRepository]. */
    fun loadArticleDetails(isRefresh: Boolean = false) {
        fetchJob?.cancel()
        if (!isRefresh) {
            _uiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
        }

        fetchJob = viewModelScope.launch {
            newsRepository.getArticleDetails(
                articleId = articleId,
                leagueName = leagueName
            ).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        if (!isRefresh) {
                            _uiState.update { it.copy(networkStatus = NetworkStatus.Loading) }
                        }
                    }

                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                article = result.data,
                                networkStatus = NetworkStatus.Success,
                                isRefreshing = false
                            )
                        }
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                networkStatus = NetworkStatus.Error(
                                    result.message ?: "Failed to load article"
                                ),
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("articleId") articleId: String,
            @Assisted("leagueName") leagueName: String,
        ): ArticleDetailsViewModel
    }
}
