package com.axmor.fsinphone.videomessages.core.network.objects.chat

import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.isValidUrl
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.network.serialization.ChatContactLastMessageDeserializer
import com.google.gson.annotations.JsonAdapter
import timber.log.Timber

data class ChatContact(
    val id: Long,
    val name: String,
    /**
     * Название исправительного учреждения
     */
    val object_title: String?,
    /**
     * Дата в формате @[com.axmor.fsinphone.videomessages.core.network.ServerApi.DEFAULT_DATE_FORMAT]
     */
    val last_activity: String?,
    /**
     * timestamp в секундах
     */
    val last_message_date: Long,

    @JsonAdapter(ChatContactLastMessageDeserializer::class)
    val last_message: ChatContactLastMessage?,

    /**
     * Настройки чата для этого контакта
     */
    val settings: ChatSettings?,

    /**
     * Количество новых сообщений от этого контакта
     */
    val new_messages: Int,

    /**
     * Аватарка контакта, добавленная ему родственником (пользователем этого приложения)
     */
    val image: String?,

    /**
     * Информация по карте заключенного - номер и баланс
     */
    val card_data: CardData?
) {

    fun isSupport(): Boolean {
        return id == Constants.ID_SUPPORT
    }

    fun addAvatar(avatar: String?) = ChatContact(
        id,
        name,
        object_title,
        last_activity,
        last_message_date,
        last_message,
        settings,
        new_messages,
        avatar,
        card_data
    )

    fun toChatContactEntity(): ChatContactEntity = ChatContactEntity(
        id,
        name,
        object_title,
        last_activity,
        last_message_date,
        new_messages,
        image,
        last_message?.getId(),
        settings,
        card_data,
        image?.isValidUrl() ?: false
    )
}