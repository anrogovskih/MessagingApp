package com.axmor.fsinphone.videomessages.ui.common

import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItem

interface ChatItemObserver {
    fun observe(item: ChatItem)
}