package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

data class AddTextMessageRequest(
    override val phone_number: String,
    override val device_token: String,
    val contact_id: Long,
    val text: String,
    override val action: String = "chat_add_message"
): BaseAuthorizedRequest() {
}