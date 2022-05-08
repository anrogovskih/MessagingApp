package com.axmor.fsinphone.videomessages.ui.screens.main

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.common.extensions.isNetworkException
import com.axmor.fsinphone.videomessages.common.state_handling.SingleLiveEvent
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactWithMessage
import com.axmor.fsinphone.videomessages.core.exceptions.DeviceTokenError
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepo
import com.axmor.fsinphone.videomessages.ui.common.Event
import com.axmor.fsinphone.videomessages.ui.common.states.StateSwipeRefreshWithErrorScreen
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import com.axmor.fsinphone.videomessages.ui.screens.main.dataClasses.ChatContactItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val chatContactsRepository: ChatContactsRepo
) : BaseViewModel(application) {

    val stateLiveData by lazy { MutableLiveData(State()) }
    val itemsLiveData by lazy { initItemsLiveData() }

    //список содержит только заключенных
    val prisoners by lazy { initPrisonersLiveData() }
    val toChatLiveEvent by lazy { SingleLiveEvent<Long>() }
    val toSupportChatLiveEvent by lazy { SingleLiveEvent<Boolean>() }
    val toReceiverSelection by lazy { SingleLiveEvent<Void>() }
    val toBalanceRefill by lazy { SingleLiveEvent<Long>() }

    private var loadItemsJob: Job? = null

    data class State(
        val settingsClick: ObservableBoolean = ObservableBoolean(),
        val isLockedBackPressClicked: ObservableBoolean = ObservableBoolean()
    ) : StateSwipeRefreshWithErrorScreen()

    init {
        stateLiveData.value?.observe()
    }

    fun loadData() {
        val loader = if (itemsLiveData.value.isNullOrEmpty())
            loadingLiveData
        else
            null
        loadData(loader)
    }

    fun setLockedBackPressClicked() {
        stateLiveData.value?.let { it.isLockedBackPressClicked.set(!it.isLockedBackPressClicked.get()) }
    }

    fun onBalanceRefillClicked() {
        val prisoners = prisoners.value ?: return
        when (prisoners.size) {
            0 -> return
            1 -> toBalanceRefill.postValue(prisoners.single().id)
            else -> toReceiverSelection.postCall()
        }
    }

    private fun initPrisonersLiveData(): LiveData<List<ChatContactEntity>> {
        return chatContactsRepository
            .getPrisonersFlow()
            .asLiveData()
    }

    private fun initItemsLiveData(): LiveData<List<ChatContactItem>> {
        return chatContactsRepository
            .getContactsWithMessagesFlow()
            .map { chatContacts ->
                chatContacts
                    .map { it.toChatContactItem() }
                    .sortedByDescending { it.getLastMessageCreationTime() }
            }
            .asLiveData()
    }

    /**
     * @param loadingLiveData - pass null, if you do not want to show progress bar
     */
    private fun loadData(loadingLiveData: MutableLiveData<Boolean>?) {
        loadItemsJob = async(loadingLiveData, {
            chatContactsRepository.load()
        },
            onError = {
                onError(it, loadingLiveData != null)
            },
            syncJob = {
                stateLiveData.value?.isRefreshing?.set(false)
            })
    }

    private suspend fun onError(ex: Exception, isShowingErrorScreen: Boolean) {
        ex.printStackTrace()

        if (ex is DeviceTokenError) {
            DatabaseManager.getDb().userProfileDao().clearTable()
            errorLiveData.postValue(Event(ex))
            return
        }

        if (isShowingErrorScreen) {
            stateLiveData.value?.apply {
                isErrorScreenShown.set(true)
                isNetworkError.set(ex.isNetworkException())
            }
        } else
            errorLiveData.postValue(Event(ex))
    }

    private fun State.observe() {
        isRefreshing
            .addOnPropertyChanged {
                if (loadItemsJob?.isActive == true)
                    isRefreshing.set(false)
                else if (isRefreshing.get())
                    loadData(null)
            }
            .disposeWith(compositeDisposable)

        updateClickObservable
            .addOnPropertyChanged {
                isErrorScreenShown.set(false)
                loadData(loadingLiveData)
            }
            .disposeWith(compositeDisposable)
    }

    private fun ChatContactWithMessage.toChatContactItem(): ChatContactItem {
        val item = ChatContactItem(this)
        item.clickObserver
            .addOnPropertyChanged {
                if (item.getId() != Constants.ID_SUPPORT)
                    toChatLiveEvent.value = item.getId()
                else
                    toSupportChatLiveEvent.value = true
            }
            .disposeWith(compositeDisposable)
        return item
    }
}