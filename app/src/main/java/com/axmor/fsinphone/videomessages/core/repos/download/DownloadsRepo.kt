package com.axmor.fsinphone.videomessages.core.repos.download

import com.axmor.fsinphone.videomessages.core.entities.DownloadStateWithProgress
import com.axmor.fsinphone.videomessages.core.enums.DownloadState
import kotlinx.coroutines.flow.StateFlow

/**
 * Репозиторий для работы с загружаемым контентом
 */
interface DownloadsRepo {

    fun getOrCreateDownloadFlow(messageId: Long): StateFlow<DownloadStateWithProgress>

    fun postProgress(progress: Int, state: DownloadState, messageId: Long)
}