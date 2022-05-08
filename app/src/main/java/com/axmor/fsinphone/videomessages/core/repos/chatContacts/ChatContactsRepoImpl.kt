package com.axmor.fsinphone.videomessages.core.repos.chatContacts

import com.axmor.fsinphone.videomessages.common.ImagesUtils
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactWithMessage
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.di.DeviceId
import com.axmor.fsinphone.videomessages.core.entities.NewAvatar
import com.axmor.fsinphone.videomessages.core.network.objects.chat.*
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_contacts.ChatGetContactsRequest
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_contacts.ChatGetContactsResponse
import com.axmor.fsinphone.videomessages.core.use_cases.EditContactUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatContactsRepoImpl @Inject constructor(
    @DeviceId private val deviceId: String,
    private val editUseCase: EditContactUseCase
) : ChatContactsRepo {

    private suspend fun getChatContactsMocked(): ChatGetContactsResponse {
        delay(1000)
        return ChatGetContactsResponse(listOf(contact(), support()))
    }

    private fun contact() = ChatContact(
        1,
        "John Doe",
        "Alkatras",
        "2022-05-06 13:39:52",
        1651833121,
        ChatContactLastMessage(
            ChatMessage(
                172176,
                false,
                false,
                true,
                1651833121,
                ChatMessageStatus.SENT,
                "16",
                "79999999999",
                "John Doe",
                "Alkatras",
                "image",
                image_data = ChatImageData(
                    0.14,
                    "https://images.unsplash.com/photo-1414438992182-69e404046f80?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1900&q=80",
                    "https://images.unsplash.com/photo-1414438992182-69e404046f80?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1900&q=80",
                ),
                text_data = null,
                video_data = null
            )
        ),
        getSettingsMocked(),
        0,
        null,
        null
    )

    private fun getSettingsMocked() = ChatSettings(
        video = ChatSetting(10, 1.0f, true, false),
        photo = ChatSetting(5242880, 1.0f, true, false),
        text = ChatSetting(900, 1.0f, true, false),
    )

    private fun support() = ChatContact(
        0,
        "Support",
        null,
        "2022-04-13 09:31:00",
        0,
        null,
        getSettingsMocked(),
        0,
        null,
        null
    )

    override suspend fun load() {
        val profile = DatabaseManager.getDb().userProfileDao().getProfile()!!
        val request = ChatGetContactsRequest(profile.phoneNumber, deviceId)
        val response = getChatContactsMocked()
        response.checkResponse()

        val localEntities = getEntities()
        val loadedEntities = response.contacts.map { newEntity ->
            // Аватар контакта
            val avatar = newEntity.image
                ?: localEntities.find { it.id == newEntity.id }?.image
                ?: if (newEntity.id == 0L) ImagesUtils.getDefaultAvatarBase64("")
                else ImagesUtils.getDefaultAvatarBase64(newEntity.name)

            newEntity.addAvatar(avatar).toChatContactEntity()
        }

        val messages = arrayListOf<ChatMessageEntity>()
        val supportMessages = arrayListOf<SupportChatMessageEntity>()
        response.contacts.forEach { contact ->
            val message = contact.last_message?.toChatMessageEntityOrNull(contact.id)
            val supportMessage = contact.last_message?.toSupportChatMessageOrNull()
            if (message != null) messages.add(message)
            if (supportMessage != null) supportMessages.add(supportMessage)
        }

        getDao().insert(loadedEntities)
        DatabaseManager.getDb().chatMessagesDao().insert(messages)
        DatabaseManager.getDb().supportChatMessageDao().insert(supportMessages)
    }

    override suspend fun editContact(contact: ChatContactEntity, name: String, avatar: NewAvatar) {
        editUseCase.execute(contact, name, avatar)
    }

    override suspend fun getContact(contactId: Long): ChatContactEntity? {
        return getDao().getChatContact(contactId)
    }

    override fun getContactFlow(contactId: Long): Flow<ChatContactEntity?> {
        return getDao().getChatContactDistinctUntilChanged(contactId)
    }

    override fun getContactsWithMessagesFlow(): Flow<List<ChatContactWithMessage>> {
        return getDao().getChatContactsWithMessageFlow()
    }

    override fun getPrisonersFlow() = getDao().getPrisonersFlow()

    private fun getDao() = DatabaseManager.getDb().chatContactsDao()

    private suspend fun getEntities() = getDao().getChatContacts()
}