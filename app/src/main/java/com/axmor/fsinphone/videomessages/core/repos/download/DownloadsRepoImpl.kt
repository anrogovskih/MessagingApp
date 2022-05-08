package com.axmor.fsinphone.videomessages.core.repos.download

import com.axmor.fsinphone.videomessages.core.entities.DownloadStateWithProgress
import com.axmor.fsinphone.videomessages.core.enums.DownloadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

class DownloadsRepoImpl @Inject constructor(): DownloadsRepo {

    private val flowsMap: HashMap<Long, MutableStateFlow<DownloadStateWithProgress>> = hashMapOf()

    override fun getOrCreateDownloadFlow(messageId: Long): StateFlow<DownloadStateWithProgress> {
        return getOrCreateMutableDownloadFlow(messageId)
    }

    override fun postProgress(progress: Int, state: DownloadState, messageId: Long) {
        val flow = getOrCreateMutableDownloadFlow(messageId)
        Timber.d("postProgress $progress, $state")
        flow.value = DownloadStateWithProgress(progress, state)
    }

    private fun getOrCreateMutableDownloadFlow(messageId: Long): MutableStateFlow<DownloadStateWithProgress>{
        val existingFlow = flowsMap[messageId]
        if (existingFlow != null) return existingFlow

        val newFlow = MutableStateFlow(DownloadStateWithProgress(0, DownloadState.IDLE))
        flowsMap[messageId] = newFlow
        return newFlow
    }
}