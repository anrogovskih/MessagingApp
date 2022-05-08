package com.axmor.fsinphone.videomessages.core.network.objects.chat

import com.axmor.fsinphone.videomessages.core.db.objects.VideoDataEntity

data class ChatVideoData(
    /**
     * Размер видео, в мб.
     */
    val size: Double,

    /**
     * Прямая ссылка на файл для загрузки.
     */
    val file: String,

    /**
     * Длительность видео, в формате "00:03"
     */
    val duration: String,

    /**
     * Превью видео, ссылка на файл картинки
     */
    val thumb: String,
){
    fun toVideoDataEntity() = VideoDataEntity(size, file, duration, thumb)
}