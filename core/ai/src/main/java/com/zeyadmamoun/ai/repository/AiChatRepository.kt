package com.zeyadmamoun.ai.repository

import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Message
import kotlinx.coroutines.flow.Flow

interface AiChatRepository {
    val isConvoActive: Boolean

    fun initializeAnalysisModel()
    fun startConvo(systemInstruction: String)
    fun sendMessage(message: String): Flow<Message>
    fun closeConvo()
}