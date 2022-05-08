package com.axmor.fsinphone.videomessages.core.network.objects.chat

import com.axmor.fsinphone.videomessages.core.network.objects.support.SupportChatMessage

class ChatContactLastMessage(
    val chatMessage: ChatMessage? = null,
    val supportChatMessage: SupportChatMessage? = null
){
    fun getId(): Long? = chatMessage?.id ?: supportChatMessage?.id

    fun toChatMessageEntityOrNull(contactId: Long) = chatMessage?.toChatMessageEntity(contactId)
    fun toSupportChatMessageOrNull() = supportChatMessage?.toSupportChatMessageEntity()
}