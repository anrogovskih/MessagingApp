package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

data class AddPhotoMessageRequest(
    override val phone_number: String,
    override val device_token: String,
    val contact_id: Long,
    /**
     * Расширение файла без точки в начале. Возможные варианты: jpg, jpeg или png
     */
    val file_extension: String,
    override val action: String = "action_name"
): BaseAuthorizedRequest() {
}