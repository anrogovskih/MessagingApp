package com.axmor.fsinphone.videomessages.core.mappers

import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import com.axmor.fsinphone.videomessages.core.entities.chat.ChatPhotoMessageItem
import com.axmor.fsinphone.videomessages.core.entities.chat.ChatTextMessageItem
import com.axmor.fsinphone.videomessages.core.entities.chat.ChatVideoMessageItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItem
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType
import com.axmor.fsinphone.videomessages.ui.common.ChatItemObserver
import javax.inject.Inject

class ChatMessageEntityToChatItemMapper @Inject constructor(): Mapper<ChatMessageEntityWithFile, ChatItem?, ChatItemObserver> {
    override fun map(input: ChatMessageEntityWithFile, args: ChatItemObserver?): ChatItem? {
        val item = when(input.message.getMessageType()){
            ChatMessageType.TEXT -> ChatTextMessageItem(input.message)
            ChatMessageType.VIDEO -> ChatVideoMessageItem(input)
            ChatMessageType.IMAGE -> ChatPhotoMessageItem(input)
            ChatMessageType.UNKNOWN -> null
        }
        if (item != null) args?.observe(item)
        return item
    }
}