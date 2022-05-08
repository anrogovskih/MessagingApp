package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_messages

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatMessage

/**
 * @param[messages_list] - список сообщений согласно параметрам запроса.
 * @param[total] отдает общее количество сообщений в чате.
 */
data class GetMessagesResponse (
    val messages_list: List<ChatMessage>?,
    val total: Int,
): UnifiedResponse()