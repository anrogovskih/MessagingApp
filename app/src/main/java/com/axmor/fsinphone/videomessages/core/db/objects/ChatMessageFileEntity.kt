package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatMessageFileEntity(
    @PrimaryKey
    val messageId: Long,
    val filePath: String,
    /**
     * Только для видеофайлов. Длительность в миллисекундах
     */
    val videoDuration: Long? = null
)