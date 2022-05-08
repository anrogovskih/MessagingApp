package com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_messages

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

/**
 * Запрос на список сообщений чата с @param[contact_id].
 *
 * Если параметры @param[page] и @param[size] не заданы, выведутся все сообщения разом.
 * Стартовая страница page == 1, отсчет начинается не с нуля!
 */
data class GetMessagesRequest(
    override val phone_number: String,
    override val device_token: String,
    val contact_id: Long,
    val page: Int? = null,
    val size: Int? = null,
    override val action: String = "action_name"
) : BaseAuthorizedRequest() {

}