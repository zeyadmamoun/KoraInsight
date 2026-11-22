package com.zeyadmamoun.matchdetails.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Message
import com.zeyadmamoun.ai.ModelDownloader
import com.zeyadmamoun.ai.repository.AiChatRepository
import com.zeyadmamoun.data.model.summary.MatchDetails
import com.zeyadmamoun.matchdetails.components.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val modelDownloader: ModelDownloader,
    private val chatRepository: AiChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchAnalyticsUiState())
    val uiState: StateFlow<MatchAnalyticsUiState> = _uiState.asStateFlow()
    private var downloadId: Long = -1L

    init {
        if (modelDownloader.isModelDownloaded()) {
            _uiState.update { it.copy(isModelDownloaded = true) }
        }
        viewModelScope.launch(Dispatchers.Default) {
            chatRepository.initializeAnalysisModel()
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

    private fun startConvo(matchDetails: MatchDetails) {
        chatRepository.startConvo(matchDetails.toTacticalSystemPrompt())
    }

    /**
     * Appends a new [message] to the conversation history, initializes the tactical
     * conversation if needed off the main thread, and streams the AI model response.
     *
     * @param matchDetails The current match details used for tactical prompt context.
     * @param message The user chat message to append.
     */
    fun sendMessage(
        matchDetails: MatchDetails,
        message: ChatMessage
    ) {
        val botPlaceholder = ChatMessage(
            text = "",
            isUser = false,
            isThinking = true
        )

        _uiState.update { state ->
            state.copy(
                conversations = state.conversations + message + botPlaceholder,
                messageBoxText = ""
            )
        }

        viewModelScope.launch {
            if (!chatRepository.isConvoActive) {
                withContext(Dispatchers.Default) {
                    startConvo(matchDetails)
                }
            }

            try {
                chatRepository.sendMessage(message.text).collect { reply ->
                    val chunk = reply.modelReply
                    if (chunk.isNotEmpty()) {
                        updateMessage(botPlaceholder.id) { current ->
                            current.copy(
                                text = current.text + chunk,
                                isThinking = false
                            )
                        }
                    }
                }
                updateMessage(botPlaceholder.id) { current ->
                    if (current.text.isBlank()) {
                        current.copy(text = "No response generated. Please try again.", isThinking = false)
                    } else {
                        current.copy(isThinking = false)
                    }
                }
            } catch (e: Throwable) {
                Log.e("MatchAnalyticsViewModel", "Failed to stream chat reply", e)
                updateMessage(botPlaceholder.id) { current ->
                    current.copy(
                        text = "Unable to analyze match right now. Please try again.",
                        isThinking = false
                    )
                }
            }
        }
    }

    private fun updateMessage(messageId: String, transform: (ChatMessage) -> ChatMessage) {
        _uiState.update { state ->
            state.copy(
                conversations = state.conversations.map { msg ->
                    if (msg.id == messageId) transform(msg) else msg
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (chatRepository.isConvoActive)
            chatRepository.closeConvo()
    }
}

val Message.modelReply: String
    get() {
        val text = contents.contents.filterIsInstance<Content.Text>()
            .joinToString("") { it.text }
        if (text.isNotEmpty()) return text
        // Take streamed channel text, but ignore the thinking channel
        return channels.filterKeys { key ->
            key != "thinking" && key != "thought"
        }.values.joinToString("")
    }
