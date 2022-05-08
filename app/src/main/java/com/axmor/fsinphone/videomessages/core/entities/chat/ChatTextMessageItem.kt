package com.axmor.fsinphone.videomessages.core.entities.chat

import androidx.databinding.ObservableBoolean
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem

class ChatTextMessageItem(message: ChatMessageEntity) : ChatMessageItem(message), ChatTextBubbleItem {

    override fun getText(): String = message.textData?.text?:""

    override fun getAlertTextColor(): Int {
        return when {
            isOnModeration() -> R.color.colorGray
            else -> R.color.colorRedLight
        }
    }

    override fun bubbleClickObservable(): ObservableBoolean = itemClick
}