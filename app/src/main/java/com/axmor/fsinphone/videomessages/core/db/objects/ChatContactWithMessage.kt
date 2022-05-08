package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Embedded
import androidx.room.Relation

data class ChatContactWithMessage(
    @Embedded
    val chatContactEntity: ChatContactEntity,

    @Relation(
        parentColumn = "lastMessageId",
        entityColumn = "id"
    )
    val lastMessage: ChatMessageEntity?,

    @Relation(
        parentColumn = "lastMessageId",
        entityColumn = "id"
    )
    val lastSupportMessage: SupportChatMessageEntity?
) {
}