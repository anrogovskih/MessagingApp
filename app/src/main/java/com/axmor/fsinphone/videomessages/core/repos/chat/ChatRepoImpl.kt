package com.axmor.fsinphone.videomessages.core.repos.chat

import android.content.Context
import androidx.work.WorkManager
import com.axmor.fsinphone.videomessages.common.Constants.FIRST_ID_FOR_DRAFT
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.*
import com.axmor.fsinphone.videomessages.core.di.DeviceId
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType
import com.axmor.fsinphone.videomessages.core.network.objects.DeleteMessageRequest
import com.axmor.fsinphone.videomessages.core.network.objects.ReadMessageRequest
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatMessageStatus
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_messages.GetMessagesRequest
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_messages.GetMessagesResponse
import com.axmor.fsinphone.videomessages.core.use_cases.SendMessagesWorkerUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendPhotoMessageUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendTextMessageUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendVideoMessageUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ChatRepoImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @DeviceId private val deviceId: String,
    private val workManager: WorkManager
) : ChatRepo {

    private val totalFlow: MutableStateFlow<Int?> = MutableStateFlow(null)

    companion object {
        private const val PAGE_SIZE = 30
        private const val FIRST_PAGE = 1
    }

    override fun getMessagesFlow(contactId: Long): Flow<List<ChatMessageEntityWithFile>> {
        return DatabaseManager.getDb().chatMessagesDao().getAllDistinctUntilChanged(contactId)
    }

    override fun createTotalFlow(): Flow<Int?> {
        totalFlow.value = null
        return totalFlow
    }

    override fun getMediaFileFlow(messageId: Long): Flow<File?> {
        return DatabaseManager.getDb()
            .chatMessageFileEntityDao()
            .getFlowById(messageId)
            .distinctUntilChanged()
            .map { it?.filePath?.let { path -> File(path) } }
            .map { if (it?.exists() == true) it else null }
    }

    override suspend fun getMediaFileIfExists(messageId: Long): File? {
        val fileEntity = DatabaseManager.getDb().chatMessageFileEntityDao().getById(messageId)
        val filePath = fileEntity?.filePath
        if (filePath != null) {
            val file = File(filePath)
            if (file.exists()) return file
        }
        return null
    }

    override suspend fun loadMore(contactId: Long, isIncrementingPage: Boolean) {
        val currentTotal = DatabaseManager.getDb().chatMessagesDao().getCount(contactId)
        val currentPage = (currentTotal / PAGE_SIZE).coerceAtLeast(FIRST_PAGE)
        val newPage = if (isIncrementingPage) currentPage + 1 else currentPage
        val response = load(contactId, newPage)
        insertMessagesToDB(response, contactId)
    }

    override suspend fun updateLast(contactId: Long) {
        val response = load(contactId, FIRST_PAGE)
        insertMessagesToDB(response, contactId)
    }

    override suspend fun sendTextMessage(contactId: Long, text: String) {
        val draftMessage = createDraftMessage(
            contactId,
            ChatMessageType.TEXT,
            textDataEntity = TextDataEntity(text)
        )
        try {
            draftMessage.send()
            //тест неотправленных сообщений
//            throw Exception("this is an expected exception")
        } catch (e: Exception) {
            Timber.w("sendTextMessage inside repository have thrown exception: e.localizedMessage")
            Timber.e(e.localizedMessage)
            enqueueSendMessagesWorker()
        }
    }

    override suspend fun sendVideoMessage(contactId: Long, file: File, duration: Long) {
        sendMediaMessage(contactId, file, ChatMessageType.VIDEO, duration)
    }

    override suspend fun sendPhotoMessage(contactId: Long, file: File) {
        sendMediaMessage(contactId, file, ChatMessageType.IMAGE)
    }

    override suspend fun resend(message: ChatMessageEntity) {
        val copy = message.copyToResend()
        DatabaseManager.getDb().chatMessagesDao().insert(copy)
        enqueueSendMessagesWorker()
    }

    override suspend fun delete(message: ChatMessageEntity) {
        if (message.isDraftMessage.not()) {
            val request = DeleteMessageRequest(message.id, getProfile().phoneNumber, deviceId)
//            ServerApi.api.deleteMessage(request).checkResponse()
        }
        DatabaseManager.getDb().chatMessagesDao().delete(message)
    }

    override suspend fun readAll(contactId: Long) {
        //ищем последнее входящее непрочитанное сообщение
        val messagesDao = DatabaseManager.getDb().chatMessagesDao()
        val lastIngoingMessage = messagesDao.getLatestIngoing(contactId)
        if (lastIngoingMessage != null) {
            //отправляем запрос на прочтение всех сообщений старше последнего, а также его самого
            val profile = getProfile()
            val request =
                ReadMessageRequest(lastIngoingMessage.id, profile.phoneNumber, deviceId, true)
//            val response = ServerApi.api.readMessage(request)
//            response.checkResponse()
            //устанавливаем статус прочтения на стороне локальной БД
            val unreadMessagesBeforeLast = messagesDao.getUnreadBefore(lastIngoingMessage.createdAt)
            val readMessages = unreadMessagesBeforeLast.map { it.copyToRead() }
            messagesDao.insert(readMessages)
            //обновляем количество непрочитанных у контакта
            val contactsDao = DatabaseManager.getDb().chatContactsDao()
            val contact = contactsDao.getChatContact(contactId)
            if (contact != null) {
                val newUnreadCount = messagesDao.getUnreadCount(contactId)
                val newLastMessage = messagesDao.getLatest(contactId)
                val updatedContact = contact.copyWith(newUnreadCount, newLastMessage?.id)
                contactsDao.insert(updatedContact)
            }
        }
    }

    private fun enqueueSendMessagesWorker() = SendMessagesWorkerUseCase.execute(workManager)

    private suspend fun sendMediaMessage(
        contactId: Long,
        file: File,
        type: ChatMessageType,
        videoDuration: Long? = null
    ) {
        val draftMessage = createDraftMessage(contactId, type)
        val mediaFileEntity = ChatMessageFileEntity(draftMessage.id, file.path, videoDuration)
        DatabaseManager.getDb().chatMessageFileEntityDao().insert(mediaFileEntity)
        enqueueSendMessagesWorker()
    }

    private suspend fun ChatMessageEntity.send(file: File? = null, videoDuration: Long? = null) {
        when (getMessageType()) {
            ChatMessageType.TEXT -> SendTextMessageUseCase.execute(this, deviceId)
            ChatMessageType.IMAGE -> SendPhotoMessageUseCase.execute(
                this,
                deviceId,
                appContext,
                file
            )
            ChatMessageType.VIDEO -> SendVideoMessageUseCase.execute(
                this,
                deviceId,
                appContext,
                file,
                videoDuration
            )
            else -> throw Exception("Unknown message type ${getMessageType()}")
        }
    }

    private suspend fun createDraftMessage(
        contactId: Long,
        type: ChatMessageType,
        imageDataEntity: ImageDataEntity? = null,
        textDataEntity: TextDataEntity? = null,
        videoDataEntity: VideoDataEntity? = null
    ): ChatMessageEntity {
        val profile = getProfile()
        val minId = DatabaseManager.getDb().chatMessagesDao().getMinId()
        val newPrimaryKey = if (minId != null && minId < 0) minId - 1 else FIRST_ID_FOR_DRAFT
        val draftMessage = ChatMessageEntity(
            newPrimaryKey,
            isOutgoing = true,
            isRead = false,
            isPaid = false,
            createdAt = System.currentTimeMillis() / 1000,
            status = ChatMessageStatus.SENT,
            sender_id = profile.id,
            sender = profile.phoneNumber,
            receiver = "",
            prisonName = "",
            type = type.typeString,
            imageData = imageDataEntity,
            textData = textDataEntity,
            videoData = videoDataEntity,
            contactId,
            isDraftMessage = true
        )
        DatabaseManager.getDb().chatMessagesDao().insert(draftMessage)
        return draftMessage
    }

    private suspend fun insertMessagesToDB(response: GetMessagesResponse, contactId: Long) {
        val entities = response.messages_list?.map { it.toChatMessageEntity(contactId) }
        if (entities != null) {
            DatabaseManager.getDb().chatMessagesDao().insert(entities)
        }
    }

    private suspend fun load(contactId: Long, page: Int): GetMessagesResponse {
        val profile = getProfile()
        val request = GetMessagesRequest(profile.phoneNumber, deviceId, contactId, page, PAGE_SIZE)
        val response = GetMessagesResponse(null, 0)
        response.checkResponse()
        totalFlow.value = response.total
        return response
    }

    private suspend fun getProfile() = DatabaseManager.getDb().userProfileDao().getProfile()!!
}