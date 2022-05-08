package com.axmor.fsinphone.videomessages.core.network.objects.chat

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.axmor.fsinphone.videomessages.R

enum class ChatMessageStatus {
    //сообщение находится на проверке
    MODERATION,

    //сообщение отклонено модератором
    DECLINED,

    //сообщение прошло модерацию и было отправлено
    SENT,

    //сообщение загружено на сервер и ожидает оплаты
    UPLOADED,

    //сообщение прошло модерацию и было получено
    RECEIVED;

    @StringRes
    fun getText(): Int {
        return when (this) {
            MODERATION -> R.string.video_messages_status_moderation
            DECLINED -> R.string.video_messages_status_declined
            SENT -> R.string.video_messages_status_sent
            RECEIVED -> R.string.video_messages_status_received
            else -> R.string.empty
        }
    }

    @ColorRes
    fun getBackground(): Int {
        return when (this) {
            MODERATION -> R.color.colorDoubleColonialWhite
            DECLINED -> R.color.colorRedVeryLight
            SENT -> R.color.colorBlueLight
            RECEIVED -> R.color.colorGreen
            UPLOADED -> R.color.colorGray6
        }
    }

    @ColorRes
    fun getTextColor(): Int {
        return when (this) {
            MODERATION -> R.color.colorBrown
            DECLINED -> R.color.colorRoman
            SENT -> R.color.colorBlue
            RECEIVED -> R.color.colorGreen
            UPLOADED -> R.color.colorBlack
        }
    }
}