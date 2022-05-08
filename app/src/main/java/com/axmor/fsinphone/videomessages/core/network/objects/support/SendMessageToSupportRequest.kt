package com.axmor.fsinphone.videomessages.core.network.objects.support

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

/**
 * Добавление нового сообщения, параметр text определяет текст сообщения. Пустое сообщение вернет ошибку.
 */
class SendMessageToSupportRequest(
    /**
     * Текст сообщения
     */
    val text: String,

    override val phone_number: String,
    override val device_token: String,

    override val action: String = "action_name"
): BaseAuthorizedRequest() {
}