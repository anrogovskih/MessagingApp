package com.axmor.fsinphone.videomessages.ui.screens.chat

import android.app.Application
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.ImagesUtils
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.collectOnce
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.common.extensions.observe
import com.axmor.fsinphone.videomessages.common.state_handling.SingleLiveEvent
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageItem
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType
import com.axmor.fsinphone.videomessages.core.mappers.Mapper
import com.axmor.fsinphone.videomessages.core.repos.chat.ChatRepo
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepo
import com.axmor.fsinphone.videomessages.core.repos.notifications.NotificationsRepo
import com.axmor.fsinphone.videomessages.ui.common.ChatItemObserver
import com.axmor.fsinphone.videomessages.ui.common.states.ChatState
import com.axmor.fsinphone.videomessages.ui.common.view_models.CameraOpenerViewModel
import com.axmor.fsinphone.videomessages.ui.common.view_models.PagingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class ChatViewModel @Inject constructor(
    application: Application,
    private val chatRepository: ChatRepo,
    private val chatContactsRepository: ChatContactsRepo,
    private val notificationsRepo: NotificationsRepo,
    private val chatMapper: Mapper<List<ChatMessageEntityWithFile>, List<ChatItem>, ChatItemObserver>,
    savedStateHandle: SavedStateHandle
) : CameraOpenerViewModel(application), PagingViewModel, ChatItemObserver {

    companion object {
        const val INITIAL_REPEAT_DELAY: Long = 500
    }

    private var loadItemsJob: Job? = null
    private var updateLastJob: Job? = null
    private var readMessagesJob: Job? = null
    private var newMessageListenerJob: Job? = null
    private var repeatDelay: Long = INITIAL_REPEAT_DELAY

    private val totalStateFlow: StateFlow<Int?> = chatRepository
        .createTotalFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val contactId = savedStateHandle.get<Long>(Constants.KEY_CONTACT_ID)!!

    val pageLoadingLiveData by lazy { MutableLiveData<Boolean>() }
    val stateLiveData by lazy { MutableLiveData(initState()) }
    val contactLiveData by lazy { initContactLiveData() }
    val chatLiveData by lazy { initChatLiveData() }
    override val pagingResetEventLiveData: MutableLiveData<Boolean> by lazy { SingleLiveEvent() }
    override val totalLiveData: LiveData<Int?> = totalStateFlow.asLiveData()
    val showAlertDraftItemOptionsLiveEvent by lazy { SingleLiveEvent<AlertDraftItemOptionsState>() }
    val showAlertDeleteMessage by lazy { SingleLiveEvent<ChatMessageItem>() }
    val showOptionsDialogLiveEvent by lazy { SingleLiveEvent<Boolean>() }
    val toContactDetailsLiveEvent by lazy { SingleLiveEvent<Long>() }
    val toViewPhotoMessage by lazy { SingleLiveEvent<Long>() }
    val toViewVideoMessage by lazy { SingleLiveEvent<Long>() }
    val takePhotoEvent by lazy { SingleLiveEvent<Long>() }
    val takeVideoEvent by lazy { SingleLiveEvent<Pair<Long, Int>>() }

    class State(
        isActionButtonEnabled: Boolean,
        isActionButtonSend: Boolean,
    ) : ChatState(isActionButtonEnabled, isActionButtonSend)

    inner class AlertDraftItemOptionsState(
        val item: ChatMessageEntity
    )

    init {
        stateLiveData.value?.observe()

        initNewMessageNotificationsListener()
        updateLastInternal(loadingLiveData)
    }

    override fun loadMore() {
        if (loadItemsJob?.isActive == true) return
        val loader = if (updateLastJob?.isActive == true) loadingLiveData else pageLoadingLiveData
        loadItemsJob = scope.launch {
            updateLastJob?.join()
            loader.postValue(true)
            loadMoreAsync(true)
            loader.postValue(false)
        }
    }

    override fun observe(item: ChatItem) {
        if (item is ChatMessageItem) {
            item.itemClick
                .addOnPropertyChanged { item.onClick() }
                .disposeWith(compositeDisposable)

            item.itemLongClick
                .addOnPropertyChanged { item.onLongClick() }
                .disposeWith(compositeDisposable)
        }
    }

    override fun onCleared() {
        super.onCleared()
        newMessageListenerJob?.cancel()
    }

    fun setLastMessageVisible(isVisible: Boolean) {
        stateLiveData.value?.isLastMessageVisible?.value = isVisible
    }

    fun resend(message: ChatMessageEntity) {
        async(null, {
            chatRepository.resend(message)
        })
    }

    fun delete(message: ChatMessageEntity) {
        async(null, {
            chatRepository.delete(message)
        })
    }

    fun sendPhotoMessage(file: File) {
        async(loadingLiveData, {
            chatRepository.sendPhotoMessage(
                contactId,
                ImagesUtils.getExifRotatedFile(file, ImagesUtils.createImageFile(getApplication()))
            )
        })
    }

    fun readAll() {
        if (readMessagesJob?.isActive == true) return
        readMessagesJob = async(null, {
            chatRepository.readAll(contactId)
        }, onError = {})
    }

    fun takePhoto() {
        safelyOpenCamera {
            takePhotoEvent.postValue(contactId)
        }
    }

    fun takeVideo() {
        safelyOpenCamera {
            contactLiveData.value?.let { entity ->
                entity.chatSettings?.let { settings ->
                    val params = Pair(entity.id, settings.video.max_length)
                    takeVideoEvent.postValue(params)
                }
            }
        }
    }

    private fun initNewMessageNotificationsListener() {
        if (newMessageListenerJob?.isActive == true) return

        newMessageListenerJob = async(null, {
            notificationsRepo.newMessage.filterNotNull().collect {
                Timber.d("NewMessageNotificationsListener: $it; contactId = $contactId")
                if (it.toLong() == contactId) {
                    notificationsRepo.cancelNotificationsById(Constants.NEW_MESSAGE_NOTIFICATION_ID)
                    updateLastInternal(loadingLiveData)
                }
            }
        })
    }

    private suspend fun loadMoreAsync(isIncrementingPage: Boolean) {
        try {
            chatRepository.loadMore(contactId, isIncrementingPage)
            repeatDelay = INITIAL_REPEAT_DELAY
        } catch (e: Exception) {
            //мы не показываем ошибку в случае загрузки новой сртраницы, просто оставляем лоадер
            //крутиться и шлём новые запросы с увеличивающимся промежутком
            if (e !is CancellationException) {
                delay(repeatDelay)
                repeatDelay *= 2
                loadMoreAsync(false)
            }
        }
    }

    private fun updateLastInternal(loadingLiveData: MutableLiveData<Boolean>? = null) {
        if (updateLastJob?.isActive == true) return
        updateLastJob = async(
            loadingLiveData,
            asyncJob = {
                val oldCount = totalStateFlow.value
                Timber.d("updateLastInternal: oldCount = $oldCount")
                chatRepository.updateLast(contactId)
                updateNewMessagesCount(oldCount)
            },
            syncJob = { stateLiveData.value?.isRefreshing?.set(false) },
            onError = ::handleException
        )
    }

    private suspend fun updateNewMessagesCount(oldCount: Int?) {
        oldCount ?: return
        totalStateFlow.collectOnce { newCount ->
            stateLiveData.value?.newMessagesCounter?.value =
                ((newCount ?: 0) - oldCount).coerceAtLeast(0)
        }
    }

    private fun initState(): State {
        return State(isActionButtonEnabled = true, isActionButtonSend = false)
    }

    private fun initContactLiveData(): LiveData<ChatContactEntity> {
        return chatContactsRepository.getContactFlow(contactId).filterNotNull().asLiveData()
    }

    private fun initChatLiveData(): LiveData<List<ChatItem>> {
        return chatRepository
            .getMessagesFlow(contactId)
            .mapNotNull { chatMapper.map(it, this) }
            .asLiveData()
    }

    private fun ChatMessageItem.onClick() {
        if (message.isShowingWarningToUser())
            showAlertDraftItemOptionsLiveEvent.value = AlertDraftItemOptionsState(message)
        else
            view()
    }

    private fun ChatMessageItem.onLongClick() {
        isSelected.set(true)
        showAlertDeleteMessage.value = this
    }

    private fun ChatMessageItem.view() {
        when (message.getMessageType()) {
            ChatMessageType.IMAGE -> toViewPhotoMessage.value = getId()
            ChatMessageType.VIDEO -> toViewVideoMessage.value = getId()
            else -> Timber.d("click ignored")
        }
    }

    private fun State.observe() {
        observe(this@ChatViewModel)

        actionButtonClickObservable
            .addOnPropertyChanged {
                if (isActionButtonSend.get()) {
                    sendTextMessage(inputFieldObservable.get() ?: "")
                    inputFieldObservable.set("")
                } else {
                    showOptionsDialog()
                }
            }
            .disposeWith(compositeDisposable)

        isRefreshing
            .addOnPropertyChanged {
                if (updateLastJob?.isActive == true || loadItemsJob?.isActive == true)
                    isRefreshing.set(false)
                else if (isRefreshing.get())
                    updateLastInternal()
            }
            .disposeWith(compositeDisposable)

        headerClick
            .addOnPropertyChanged {
                toContactDetailsLiveEvent.value = contactId
            }
            .disposeWith(compositeDisposable)
    }

    private fun sendTextMessage(message: String) {
        async(null, {
            chatRepository.sendTextMessage(contactId, message.trim())
        },
            onError = { it.printStackTrace() })
    }

    private fun showOptionsDialog() {
        showOptionsDialogLiveEvent.value = true
    }
}