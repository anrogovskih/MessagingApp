package com.axmor.fsinphone.videomessages.ui.screens.support

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.common.state_handling.SingleLiveEvent
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.chat.SupportMessageItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.core.mappers.Mapper
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepo
import com.axmor.fsinphone.videomessages.core.repos.support.SupportRepo
import com.axmor.fsinphone.videomessages.ui.common.ChatTextBubbleItemObserver
import com.axmor.fsinphone.videomessages.ui.common.states.ChatState
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

//TODO: после включения техподдержки реализовать нотификации о новых сообщениях и бэдж возле кнопки скролла вниз (см. ChatViewModel.updateNewMessagesCount)
@HiltViewModel
class SupportViewModel @Inject constructor(
    application: Application,
    private val chatContactsRepository: ChatContactsRepo,
    private val supportRepository: SupportRepo,
    private val chatMapper: Mapper<List<SupportChatMessageEntity>, List<ChatTextBubbleItem>, ChatTextBubbleItemObserver>
) : BaseViewModel(application), ChatTextBubbleItemObserver {

    val stateLiveData by lazy { MutableLiveData(initState()) }
    val chatLiveData by lazy { initChatLiveData() }
    val contactLiveData by lazy { initContactLiveData() }
    val showAlertDraftItemOptionsLiveEvent by lazy { SingleLiveEvent<AlertDraftItemOptionsState>() }

    private var loadItemsJob: Job? = null

    class State(isActionButtonEnabled: Boolean, isActionButtonSend: Boolean) :
        ChatState(isActionButtonEnabled, isActionButtonSend)

    inner class AlertDraftItemOptionsState(
        val item: SupportMessageItem
    )

    init {
        stateLiveData.value?.observe()
        loadItems(loadingLiveData)
    }

    fun setLastMessageVisible(isVisible: Boolean) {
        stateLiveData.value?.isLastMessageVisible?.value = isVisible
    }

    fun resend(message: SupportMessageItem) {
        async(null, {
            supportRepository.resend(message.message)
        })
    }

    fun delete(message: SupportMessageItem) {
        async(null, {
            supportRepository.delete(message.message)
        })
    }

    private fun initContactLiveData(): LiveData<ChatContactEntity> {
        return chatContactsRepository.getContactFlow(Constants.ID_SUPPORT).filterNotNull().asLiveData()
    }

    private fun initState(): State {
        return State(isActionButtonEnabled = false, isActionButtonSend = true)
    }

    private fun initChatLiveData(): LiveData<List<ChatTextBubbleItem>> {
        return supportRepository
            .getSupportMessagesFlow()
            .mapNotNull { chatMapper.map(it, this) }
            .asLiveData()
    }

    private fun State.observe() {
        observe(this@SupportViewModel)

        isRefreshing
            .addOnPropertyChanged {
                if (loadItemsJob?.isActive == true)
                    isRefreshing.set(false)
                else if (isRefreshing.get())
                    loadItems(null)
            }
            .disposeWith(compositeDisposable)

        actionButtonClickObservable
            .addOnPropertyChanged {
                sendTextMessage(inputFieldObservable.get() ?: "")
                inputFieldObservable.set("")
            }
            .disposeWith(compositeDisposable)
    }

    override fun observe(item: ChatTextBubbleItem) {
        item.bubbleClickObservable()
            ?.addOnPropertyChanged { item.onClick() }
            ?.disposeWith(compositeDisposable)
    }

    private fun sendTextMessage(message: String) {
        async(null, { supportRepository.send(message) })
    }

    private fun ChatTextBubbleItem.onClick() {
        if (this is SupportMessageItem) {
            if (message.isShowingWarningToUser())
                showAlertDraftItemOptionsLiveEvent.value = AlertDraftItemOptionsState(this)
        }
    }

    private fun loadItems(loadingLiveData: MutableLiveData<Boolean>?) {
        loadItemsJob = async(
            loadingLiveData, {
                supportRepository.loadMessages()
            },
            syncJob = { stateLiveData.value?.isRefreshing?.set(false) }
        )
    }
}