package com.axmor.fsinphone.videomessages.core.mappers

import com.axmor.fsinphone.videomessages.common.extensions.isSameDayAs
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItemWithStatus
import com.axmor.fsinphone.videomessages.ui.common.ChatItemObserver
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ChatMessageEntityToChatItemListMapper @Inject constructor(
    private val itemMapper: Mapper<ChatMessageEntityWithFile, ChatItem?, ChatItemObserver>
) : Mapper<@JvmSuppressWildcards List<ChatMessageEntityWithFile>, @JvmSuppressWildcards List<ChatItem>, ChatItemObserver> {

    override fun map(input: List<ChatMessageEntityWithFile>, args: ChatItemObserver?): List<ChatItem> {
        Timber.w("ChatMessageEntityToChatItemListMapper: map input of size ${input.size}")
        val lastItemCalendar: Calendar by lazy { Calendar.getInstance() }
        val thisItemCalendar: Calendar by lazy { Calendar.getInstance() }
        var lastItem: ChatItem? = null

        val result = input.mapNotNull {
            val item = itemMapper.map(it, args)
            if (item is ChatItemWithStatus) {
                thisItemCalendar.timeInMillis = item.getTime()

                if (lastItem != null && !thisItemCalendar.isSameDayAs(lastItemCalendar))
                    lastItem?.setDateVisible(true)

                lastItemCalendar.timeInMillis = item.getTime()
                lastItem = item
            }
            return@mapNotNull item
        }

        lastItem?.setDateVisible(true)
        Timber.w("ChatMessageEntityToChatItemListMapper: return result of size ${result.size}")
        return result
    }
}