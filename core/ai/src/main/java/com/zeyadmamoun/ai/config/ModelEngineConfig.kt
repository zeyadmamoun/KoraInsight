package com.zeyadmamoun.ai.config

import android.util.Log
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.SamplerConfig
import com.zeyadmamoun.ai.ModelDownloader
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelEngineConfig @Inject constructor(
    private val modelDownloader: ModelDownloader,
) {
    private var engine: Engine? = null

    companion object {
        private const val TAG = "ModelEngineConfig"
    }

    fun initializeEngine(maxInputToken: Int = 4096) {
        if (isEngineInitialized()) return
        if (!modelDownloader.isModelDownloaded()) {
            return
        }

        val modelPath = modelDownloader.filePath.path
        val litertCacheDir = File(modelDownloader.filePath.parentFile?.parentFile, "cache/litert")
            .apply { mkdirs() }.path

        try {
            val config = EngineConfig(
                modelPath = modelPath,
                backend = Backend.GPU(),
                maxNumTokens = maxInputToken,
                cacheDir = litertCacheDir
            )
            engine = Engine(config)
            engine?.initialize()
        } catch (gpuError: Throwable) {
            try {
                val config = EngineConfig(
                    modelPath = modelPath,
                    backend = Backend.CPU(),
                    maxNumTokens = maxInputToken,
                    cacheDir = litertCacheDir
                )
                engine = Engine(config)
                engine?.initialize()
                Log.d(TAG, "LiteRT engine initialized successfully with CPU backend.")
            } catch (cpuError: Throwable) {
                engine = null
                Log.e(TAG, "Failed to initialize LiteRT engine on both GPU and CPU.", cpuError)
            }
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
                temperature = 0.5
            ),
            prefillPrefaceOnInit = true
        )
        return engine?.createConversation(convoConfig)
    }
}