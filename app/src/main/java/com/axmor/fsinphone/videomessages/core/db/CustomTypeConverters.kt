package com.axmor.fsinphone.videomessages.core.db

import androidx.room.TypeConverter
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatMessageStatus
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatSettings
import com.google.gson.Gson

class CustomTypeConverters {
    @TypeConverter
    fun fromChatMessageStatus(value: ChatMessageStatus?): Int?{
        return value?.ordinal
    }

    @TypeConverter
    fun toChatMessageStatus(value: Int?): ChatMessageStatus?{
        value?: return null
        return enumValues<ChatMessageStatus>()[value]
    }

    @TypeConverter
    fun fromChatSettings(value: ChatSettings?): String? {
        return Gson().toJson(value ?: return null)
    }

    @TypeConverter
    fun toChatSettings(value: String?): ChatSettings? {
        return Gson().fromJson(value ?: return null, ChatSettings::class.java)
    }
}