package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_contacts

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatContact

/**
 * Выдает список доступных чатов с последними сообщениями, количеством непрочитанных сообщений и в
 * той последовательности, в которой они были отправлены. Здесь же отдаётся техподдержка с последним
 * сообщением и счетчиком непрочитанных ответов от службы.
 */
data class ChatGetContactsResponse(
    val contacts: List<ChatContact>
): UnifiedResponse()