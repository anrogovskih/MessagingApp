package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

data class AddVideoMessageRequest(
    override val phone_number: String,
    override val device_token: String,
    val contact_id: Long,
    val duration: Long,
    /**
     * Расширение файла (mp4, mov и т.д.) без точки в начале.
     */
    val file_extension: String,
    override val action: String = "action_name"
): BaseAuthorizedRequest() {
}