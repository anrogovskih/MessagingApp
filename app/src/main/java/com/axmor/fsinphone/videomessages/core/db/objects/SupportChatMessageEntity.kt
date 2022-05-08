package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axmor.fsinphone.videomessages.common.Constants

@Entity
data class SupportChatMessageEntity(
    @PrimaryKey
    val id: Long,
    //текст сообщения
    val text: String,
    val isRead: Boolean,
    //исходящее, если true
    val isOutgoing: Boolean,
    //время создания, в миллисекундах!
    val createdAt: Long,

    /**
     * true - если это сообщение было создано локально и ещё не отправлено на сервер
     * false - для всех сообщений, получаемых с бэкенда
     */
    val isDraft: Boolean = false,
    /**
     * Количество попыток отправки
     */
    val attemptsToSend: Int = 0,
){

    fun isShowingWarningToUser(): Boolean = isDraft && attemptsToSend >= Constants.MAX_ATTEMPTS_TO_SEND_TEXT

    fun copyWithServerId(id: Long) = SupportChatMessageEntity(
        id, text, isRead, isOutgoing, System.currentTimeMillis()
    )

    fun copyAfterFailedAttempt() = SupportChatMessageEntity(
        id, text, isRead, isOutgoing, System.currentTimeMillis(), isDraft, attemptsToSend + 1
    )

    fun copyToResend() = SupportChatMessageEntity(
        id, text, isRead, isOutgoing, System.currentTimeMillis(), isDraft, 0
    )
}