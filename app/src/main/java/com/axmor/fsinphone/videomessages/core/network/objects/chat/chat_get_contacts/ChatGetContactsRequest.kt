package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_contacts

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

/**
 * Выдает список доступных чатов с последними сообщениями, количеством непрочитанных сообщений и в
 * той последовательности, в которой они были отправлены. Здесь же отдаётся техподдержка с последним
 * сообщением и счетчиком непрочитанных ответов от службы.
 */
class ChatGetContactsRequest(
    override val phone_number: String,
    override val device_token: String,
    override val action: String = "action_name"
): BaseAuthorizedRequest() {

}