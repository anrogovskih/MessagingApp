package com.axmor.fsinphone.videomessages.core.network.objects.chat

import com.axmor.fsinphone.videomessages.core.db.objects.ImageDataEntity
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType

data class ChatImageData(
    /**
     * Размер картинки, в мб.
     */
    val size: Double,

    /**
     * Прямая ссылка на файл для загрузки.
     */
    val file: String,

    /**
     * Превью изображения более низкого качества, ссылка на файл
     */
    val thumb: String,

){
    fun toImageDataEntity() = ImageDataEntity(size, file, thumb)
}