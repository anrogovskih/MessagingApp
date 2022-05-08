package com.axmor.fsinphone.videomessages.core.use_cases

import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddMessageResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddTextMessageRequest
import kotlin.random.Random

object SendTextMessageUseCase {

    suspend fun execute(draftMessage: ChatMessageEntity, deviceId: String) {
        val request = AddTextMessageRequest(
            draftMessage.sender,
            deviceId,
            draftMessage.contactId,
            draftMessage.textData!!.text
        ).also {
            it.id = draftMessage.createdAt * 1000
        }
        val response = AddMessageResponse(Random(System.currentTimeMillis()).nextLong())
        response.checkResponse()
        val copy = draftMessage.copyWithServerId(response.message_id)
        DatabaseManager.getDb().chatMessagesDao().replace(draftMessage, copy)
    }
}