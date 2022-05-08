package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Embedded
import androidx.room.Relation

data class ChatMessageEntityWithFile (
    @Embedded
    val message: ChatMessageEntity,

    /**
     * Ссылка на файл на устройстве
     */
    @Relation(
        parentColumn = "id",
        entityColumn = "messageId"
    )
    val localFileEntity: ChatMessageFileEntity? = null
)