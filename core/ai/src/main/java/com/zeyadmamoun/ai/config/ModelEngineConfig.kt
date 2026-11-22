package com.zeyadmamoun.ai.config

import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.SamplerConfig
import com.zeyadmamoun.ai.ModelDownloader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelEngineConfig @Inject constructor(
    private val modelDownloader: ModelDownloader,
) {
    private var engine: Engine? = null

    fun initializeEngine(maxInputToken: Int = 2048) {
        if (isEngineInitialized()) return

        try {
            val config = EngineConfig(
                modelPath = modelDownloader.filePath.path,
                backend = Backend.GPU(),
                maxNumTokens = maxInputToken
            )
            engine = Engine(config)
            engine?.initialize()
        } catch (e: Throwable) {
            val config = EngineConfig(
                modelPath = modelDownloader.filePath.path,
                backend = Backend.CPU(),
                maxNumTokens = maxInputToken
            )
            engine = Engine(config)
            engine?.initialize()
        }
    }

    fun isEngineInitialized(): Boolean {
        return engine?.isInitialized() ?: false
    }

    fun createConvo(systemInstruction: String): Conversation? {
        val convoConfig = ConversationConfig(
            systemInstruction = Contents.of(systemInstruction),
            maxOutputToken = 1024,
            samplerConfig = SamplerConfig(
                topK = 40,
                topP = 0.9,
                temperature = 0.2
            )
        )
        return engine?.createConversation(convoConfig)
    }
}