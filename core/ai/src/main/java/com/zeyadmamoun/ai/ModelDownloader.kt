package com.zeyadmamoun.ai

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class ModelDownloader @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val downloader = context.getSystemService<DownloadManager>()

    companion object {
        const val DEFAULT_MODEL_NAME = "qwen2.5-0.5b-instruct.tflite"
    }

    fun getModelFile(fileName: String = DEFAULT_MODEL_NAME): File {
        return File(context.getExternalFilesDir(null), "models/$fileName")
    }

    val filePath: File
        get() = getModelFile()

    fun downloadModel(
        url: String = context.getString(R.string.qwen_model_uri),
        fileName: String = DEFAULT_MODEL_NAME,
        title: String = "AI Tactical Model",
        description: String = "Downloading on-device intelligence model..."
    ): Long {
        if (isModelDownloaded(fileName)) return -1L

        val targetFile = getModelFile(fileName)
        targetFile.parentFile?.mkdirs()

        val request = DownloadManager.Request(url.toUri())
            .setTitle(title)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationUri(Uri.fromFile(targetFile))

        return downloader?.enqueue(request) ?: -1L
    }

    fun getDownloadProgress(downloadId: Long): Flow<Int> = flow {
        if (downloadId == -1L) return@flow

        var isDownloading = true
        while (isDownloading) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            downloader?.query(query)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val bytesDownloaded = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    )
                    val totalBytes = cursor.getLong(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    )

                    if (totalBytes > 0) {
                        val percentage = ((bytesDownloaded * 100L) / totalBytes).toInt()
                        emit(percentage.coerceIn(0, 100))
                    }

                    val status = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                    )
                    if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                        isDownloading = false
                    }
                } else {
                    isDownloading = false
                }
            }
            if (isDownloading) {
                delay(500.milliseconds)
            }
        }
    }

    fun cancelDownload(id: Long, fileName: String = DEFAULT_MODEL_NAME): Boolean {
        if (id == -1L) return false

        val removed = downloader?.remove(id) ?: 0
        val targetFile = getModelFile(fileName)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        return removed > 0
    }

    fun isModelDownloaded(fileName: String = DEFAULT_MODEL_NAME): Boolean {
        val targetFile = getModelFile(fileName)
        return targetFile.exists() && targetFile.length() > 0
    }
}
