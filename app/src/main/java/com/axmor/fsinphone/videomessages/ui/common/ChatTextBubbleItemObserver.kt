package com.axmor.fsinphone.videomessages.ui.common

import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem

interface ChatTextBubbleItemObserver {
    fun observe(item: ChatTextBubbleItem)
}