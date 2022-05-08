package com.axmor.fsinphone.videomessages.core.use_cases

import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddMessageResponse
import com.axmor.fsinphone.videomessages.core.network.objects.support.SendMessageToSupportRequest
import kotlin.random.Random

object SendSupportMessageUseCase {

    suspend fun execute(draftMessage: SupportChatMessageEntity, deviceId: String, phoneNumber: String) {
        val request = SendMessageToSupportRequest(draftMessage.text, phoneNumber, deviceId)
        val response = AddMessageResponse(Random(System.currentTimeMillis()).nextLong())
        response.checkResponse()

        val copy = draftMessage.copyWithServerId(response.message_id)
        DatabaseManager.getDb().supportChatMessageDao().replace(draftMessage, copy)
    }
}