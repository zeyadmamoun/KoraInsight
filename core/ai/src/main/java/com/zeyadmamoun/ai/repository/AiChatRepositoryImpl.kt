package com.zeyadmamoun.ai.repository

import android.util.Log
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Message
import com.zeyadmamoun.ai.config.ModelEngineConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatRepositoryImpl @Inject constructor(
    private val modelEngineConfig: ModelEngineConfig
) : AiChatRepository {

    private lateinit var convo: Conversation

    override val isConvoActive: Boolean
        get() = ::convo.isInitialized && convo.isAlive

    override fun initializeAnalysisModel() {
        modelEngineConfig.initializeEngine()
    }

    override fun startConvo(systemInstruction: String) {
        if (modelEngineConfig.isEngineInitialized())
            convo = modelEngineConfig.createConvo(systemInstruction)!!
    }

    override fun sendMessage(message: String): Flow<Message> {
        return if (::convo.isInitialized) {
            convo.sendMessageAsync(message)
                .catch { Log.d("ModelTracing", "sendMessage: $it") }
                .flowOn(Dispatchers.IO)
        } else {
            throw IllegalStateException("Conversation not initialized")
        }
    }

    override fun closeConvo() {
        if (::convo.isInitialized && convo.isAlive)
            convo.close()
    }
}