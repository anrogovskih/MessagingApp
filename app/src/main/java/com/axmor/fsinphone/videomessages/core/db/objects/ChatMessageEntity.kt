package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageStatusIcon
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatMessageStatus
import com.google.gson.annotations.SerializedName

@Entity
data class ChatMessageEntity(
    @PrimaryKey
    val id: Long,
    //исходящее - true, входящее - false
    val isOutgoing: Boolean,
    //прочитано или нет
    val isRead: Boolean,
    //оплачено или нет
    @SerializedName("isPayed")
    val isPaid: Boolean,
    //время изначальной загрузки файла на сервер, в секундах
    val createdAt: Long,
    val status: ChatMessageStatus?,
    val sender_id: String,
    val sender: String,
    val receiver: String,
    val prisonName: String,
    val type: String,

    @Embedded(prefix = "image_")
    val imageData: ImageDataEntity?,

    @Embedded(prefix = "text_")
    val textData: TextDataEntity?,

    @Embedded(prefix = "video_")
    val videoData: VideoDataEntity?,

    val contactId: Long,

    /**
     * true - если это сообщение было создано локально и ещё не отправлено на сервер
     * false - для всех сообщений, получаемых с бэкенда
     */
    val isDraftMessage: Boolean = false,

    /**
     * Количество попыток отправки
     */
    val attemptsToSend: Int = 0,
) {

    fun getDownloadUrl(): String?{
        return when(getMessageType()){
            ChatMessageType.VIDEO -> videoData?.file
            ChatMessageType.IMAGE -> imageData?.file
            else -> null
        }
    }

    fun getMessageType() = ChatMessageType.fromString(type)

    fun isDraft() = isDraftMessage

    fun isShowingWarningToUser(): Boolean = isDraft() && attemptsToSend >= Constants.MAX_ATTEMPTS_TO_SEND_TEXT

    fun copyWithServerId(id: Long) = ChatMessageEntity(
        id,
        isOutgoing,
        isRead,
        isPaid,
        System.currentTimeMillis() / 1000,
        status,
        sender_id,
        sender,
        receiver,
        prisonName,
        type,
        imageData,
        textData,
        videoData,
        contactId,
        isDraftMessage = false
    )

    fun copyAfterFailedAttempt() = ChatMessageEntity(
        id,
        isOutgoing,
        isRead,
        isPaid,
        System.currentTimeMillis() / 1000,
        status,
        sender_id,
        sender,
        receiver,
        prisonName,
        type,
        imageData,
        textData,
        videoData,
        contactId,
        attemptsToSend = attemptsToSend + 1,
        isDraftMessage = true
    )

    fun copyToResend() = ChatMessageEntity(
        id,
        isOutgoing,
        isRead,
        isPaid,
        System.currentTimeMillis() / 1000,
        status,
        sender_id,
        sender,
        receiver,
        prisonName,
        type,
        imageData,
        textData,
        videoData,
        contactId,
        attemptsToSend = 0,
        isDraftMessage = true
    )

    fun copyToRead() = ChatMessageEntity(
        id,
        isOutgoing,
        true,
        isPaid,
        createdAt,
        status,
        sender_id,
        sender,
        receiver,
        prisonName,
        type,
        imageData,
        textData,
        videoData,
        contactId,
        attemptsToSend = attemptsToSend,
        isDraftMessage = isDraftMessage
    )
}