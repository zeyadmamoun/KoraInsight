package com.zeyadmamoun.matchdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeyadmamoun.ai.ModelDownloader
import com.zeyadmamoun.matchdetails.components.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state representing the on-device AI model download lifecycle
 * and the active chat conversation history.
 *
 * @property isModelDownloaded Whether the AI model is downloaded and ready for inference.
 * @property isDownloadingModel Whether an active download is in progress.
 * @property downloadProgress Current download completion percentage (0-100).
 * @property messageBoxText Current draft text in the message input field.
 * @property conversations Ordered list of chat messages exchanged between user and AI.
 */
data class MatchAnalyticsUiState(
    val isModelDownloaded: Boolean = false,
    val isDownloadingModel: Boolean = false,
    val downloadProgress: Int = 0,
    val messageBoxText: String = "",
    val conversations: List<ChatMessage> = emptyList()
)

/**
 * ViewModel responsible for managing on-device AI model downloads
 * and handling tactical chat conversation state for match analytics.
 *
 * @param modelDownloader Helper for initiating, monitoring, and canceling model downloads.
 */
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

    /**
     * Starts downloading the on-device analysis model and monitors progress.
     */
    fun downloadAnalysisModel() {
        downloadId = modelDownloader.downloadModel()
        updateDownloadProgress()
        if (downloadId != -1L) {
            _uiState.update { it.copy(isDownloadingModel = true) }
        }
    }

    /**
     * Cancels any ongoing model download and resets download progress.
     */
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

    /**
     * Updates the state when the model download has completed successfully.
     */
    fun onDownloadCompleted() {
        val isDownloaded = modelDownloader.isModelDownloaded()
        _uiState.update {
            it.copy(
                isModelDownloaded = isDownloaded,
                isDownloadingModel = false
            )
        }
    }

    /**
     * Collects real-time download progress updates from [modelDownloader] into [uiState].
     */
    private fun updateDownloadProgress() {
        viewModelScope.launch {
            modelDownloader.getDownloadProgress(downloadId).collect { progress ->
                _uiState.update {
                    it.copy(downloadProgress = progress)
                }
            }
        }
    }

    /**
     * Updates the message draft text in the input box.
     *
     * @param text The new text entered by the user.
     */
    fun updateMessageBoxText(text: String) {
        _uiState.update {
            it.copy(messageBoxText = text)
        }
    }

    /**
     * Appends a new [message] to the conversation history and clears the message draft.
     *
     * @param message The chat message to append.
     */
    fun sendMessage(message: ChatMessage) {
        _uiState.update {
            it.copy(
                conversations = it.conversations + message,
                messageBoxText = ""
            )
        }
    }
}
