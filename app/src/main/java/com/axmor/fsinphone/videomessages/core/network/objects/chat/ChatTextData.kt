package com.axmor.fsinphone.videomessages.core.network.objects.chat

import com.axmor.fsinphone.videomessages.core.db.objects.TextDataEntity

data class ChatTextData(
    val text: String,
){
    fun toTextDataEntity() = TextDataEntity(text)
}