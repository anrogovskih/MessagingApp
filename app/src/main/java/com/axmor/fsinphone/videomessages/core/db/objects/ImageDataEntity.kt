package com.axmor.fsinphone.videomessages.core.db.objects

data class ImageDataEntity(
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

)