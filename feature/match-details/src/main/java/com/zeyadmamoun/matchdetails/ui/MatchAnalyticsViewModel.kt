package com.zeyadmamoun.matchdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.ai.ModelDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchAnalyticsUiState(
    val isModelDownloaded: Boolean = false,
    val isDownloadingModel: Boolean = false,
    val downloadProgress: Int = 0
)

@HiltViewModel
class MatchAnalyticsViewModel @Inject constructor(
    private val modelDownloader: ModelDownloader
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchAnalyticsUiState())
    val uiState: StateFlow<MatchAnalyticsUiState> = _uiState.asStateFlow()

    private var downloadId: Long = -1L

    init {
        if (modelDownloader.isModelDownloaded()) {
            _uiState.update { it.copy(isModelDownloaded = true) }
        }
    }

    fun downloadAnalysisModel() {
        downloadId = modelDownloader.downloadModel()
        updateDownloadProgress()
        if (downloadId != -1L) {
            _uiState.update { it.copy(isDownloadingModel = true) }
        }
    }

    fun cancelModelDownloading() {
        val result = modelDownloader.cancelDownload(downloadId)
        if (result) {
            _uiState.update {
                it.copy(
                    isDownloadingModel = false,
                    downloadProgress = 0
                )
            }
        }
    }

    fun onDownloadCompleted() {
        val isDownloaded = modelDownloader.isModelDownloaded()
        _uiState.update {
            it.copy(
                isModelDownloaded = isDownloaded,
                isDownloadingModel = false
            )
        }
    }

    private fun updateDownloadProgress() {
        viewModelScope.launch {
            modelDownloader.getDownloadProgress(downloadId).collect { progress ->
                _uiState.update {
                    it.copy(downloadProgress = progress)
                }
            }
        }
    }
}
