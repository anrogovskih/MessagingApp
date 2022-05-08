package com.axmor.fsinphone.videomessages.core.network.objects.chat


import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageStatusIcon
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType
import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val id: Long,
    //исходящее - true, входящее - false
    val isOutgoing: Boolean,
    //прочитано или нет
    val isRead: Boolean,
    //оплачено или нет
    @SerializedName("isPayed")
    val isPaid: Boolean,
    //время изначальной загрузки файла на сервер
    val createdAt: Long,
    val status: ChatMessageStatus?,
    val sender_id: String,
    val sender: String,
    val receiver: String,
    val prisonName: String,
    @SerializedName(TYPE_SERIALIZED_NAME)
    val type: String,

    /**
     * Только один из этих типов будет не null, т.к. у нас не может быть в одном сообщении контент
     * разных типов.
     */
    val image_data: ChatImageData?,
    val text_data: ChatTextData?,
    val video_data: ChatVideoData?
){

    fun toChatMessageEntity(contactId: Long): ChatMessageEntity {
        val textData = text_data?.toTextDataEntity()
        val imageData = image_data?.toImageDataEntity()
        val videoData = video_data?.toVideoDataEntity()

        return ChatMessageEntity(
            id,
            isOutgoing,
            isRead,
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
            contactId
        )
    }

    companion object {
       const val TYPE_SERIALIZED_NAME = "type"
    }
}