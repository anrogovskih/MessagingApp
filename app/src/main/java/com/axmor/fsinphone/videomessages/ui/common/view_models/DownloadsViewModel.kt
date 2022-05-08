package com.axmor.fsinphone.videomessages.ui.common.view_models

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.entities.DownloadStateWithProgress
import com.axmor.fsinphone.videomessages.core.enums.DownloadState
import com.axmor.fsinphone.videomessages.core.repos.chat.ChatRepo
import com.axmor.fsinphone.videomessages.core.repos.download.DownloadsRepo
import com.axmor.fsinphone.videomessages.core.use_cases.GetDownloadMessageMediaWorkerUseCase
import com.axmor.fsinphone.videomessages.core.worker.DownloadMessageMediaWorker
import com.axmor.fsinphone.videomessages.ui.common.Event
import com.axmor.fsinphone.videomessages.ui.common.states.DownloadsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class DownloadsViewModel(
    application: Application,
    downloadsRepository: DownloadsRepo,
    private val chatRepository: ChatRepo,
    protected val savedStateHandle: SavedStateHandle
) : BaseViewModel(application) {

    protected val workManager = WorkManager.getInstance(application)
    protected val messageId = savedStateHandle.get<Long>(Constants.KEY_MESSAGE_ID)!!
    private val downloadStateFlow = downloadsRepository.getOrCreateDownloadFlow(messageId)

    val fileLiveData = chatRepository.getMediaFileFlow(messageId).asLiveData()

    protected abstract fun getState(): DownloadsState?

    protected fun observeDownloadState(){
        scope.launch(Dispatchers.Main) {
            downloadStateFlow.collect (::handleState)
        }
    }

    protected fun checkIfFileExists(){
        loadingLiveData.value = true
        scope.launch {
            try {
                val file = chatRepository.getMediaFileIfExists(messageId)
                if (file != null){
                    loadingLiveData.postValue(false)
                    return@launch
                }
            }
            catch (e: Exception){
                handleException(e)
            }
            enqueueDownloadWorker()
        }
    }

    private fun handleState(state: DownloadStateWithProgress){
        getState()?.progress?.set(state.progress)
        when (state.state){
            DownloadState.DOWNLOADING -> loadingLiveData.value = true
            DownloadState.ERROR -> {
                loadingLiveData.value = false
                val errorString = getApplication<Application>().getString(R.string.error_download_video)
                errorLiveData.postValue(Event(Exception(errorString)))
            }
            DownloadState.DONE -> loadingLiveData.value = false
            DownloadState.IDLE -> {
                //мы не знаем, нужно ли что-то загружать, прогресс баром управляет метод loadVideo
            }
        }
    }

    protected fun enqueueDownloadWorker(){
        Timber.d("enqueueDownloadWorker")
        val request = GetDownloadMessageMediaWorkerUseCase.execute(messageId)
        workManager.enqueueUniqueWork(DownloadMessageMediaWorker.TAG, ExistingWorkPolicy.KEEP, request)
    }
}