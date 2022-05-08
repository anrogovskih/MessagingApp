package com.axmor.fsinphone.videomessages.core.mappers

import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.chat.SupportMessageItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.ui.common.ChatTextBubbleItemObserver
import javax.inject.Inject

class SupportChatMessageEntityToChatTextBubbleItemMapper @Inject constructor():
    Mapper<SupportChatMessageEntity, ChatTextBubbleItem, ChatTextBubbleItemObserver> {

    override fun map(
        input: SupportChatMessageEntity,
        args: ChatTextBubbleItemObserver?
    ): ChatTextBubbleItem {
        val item = SupportMessageItem(input)
        args?.observe(item)
        return item
    }
}