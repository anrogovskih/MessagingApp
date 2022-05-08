package com.axmor.fsinphone.videomessages.core.mappers

import com.axmor.fsinphone.videomessages.common.extensions.isSameDayAs
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.ui.common.ChatTextBubbleItemObserver
import java.util.*
import javax.inject.Inject

class SupportChatMessageEntityToChatTextBubbleItemListMapper @Inject constructor(
    private val itemMapper: Mapper<SupportChatMessageEntity, ChatTextBubbleItem, ChatTextBubbleItemObserver>
) :
    Mapper<@JvmSuppressWildcards List<SupportChatMessageEntity>, @JvmSuppressWildcards List<ChatTextBubbleItem>, ChatTextBubbleItemObserver> {

    override fun map(
        input: List<SupportChatMessageEntity>,
        args: ChatTextBubbleItemObserver?
    ): List<ChatTextBubbleItem> {
        val lastItemCalendar: Calendar by lazy { Calendar.getInstance() }
        val thisItemCalendar: Calendar by lazy { Calendar.getInstance() }
        var lastItem: ChatTextBubbleItem? = null

        val result = input.map {
            val item = itemMapper.map(it, args)
            thisItemCalendar.timeInMillis = item.getTime()

            if (lastItem != null && !thisItemCalendar.isSameDayAs(lastItemCalendar))
                lastItem?.setDateVisible(true)

            lastItemCalendar.timeInMillis = item.getTime()
            lastItem = item

            item
        }

        lastItem?.setDateVisible(true)

        return result
    }
}