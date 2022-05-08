package com.axmor.fsinphone.videomessages.core.entities.chat

import androidx.databinding.ObservableBoolean
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageStatusIcon
import com.axmor.fsinphone.videomessages.core.enums.ChatWarningIcon

class SupportMessageItem(val message: SupportChatMessageEntity): ChatTextBubbleItem {

    private val clickObservable = ObservableBoolean()
    private val isDateVisibleObservable = ObservableBoolean(false)

    override val itemLongClick: ObservableBoolean? = null
    override val isSelected: ObservableBoolean = ObservableBoolean()

    override fun getText(): String = message.text

    override fun getBackground(): Int {
        return when{
            isErrorState() -> R.color.colorRedVeryLight2
            isOutgoing() -> R.color.colorMintCream
            else -> android.R.color.white
        }
    }

    override fun bubbleClickObservable(): ObservableBoolean = clickObservable

    override fun getTime(): Long = message.createdAt

    override fun isOutgoing(): Boolean = message.isOutgoing

    override fun getStatus(): ChatMessageStatusIcon {
        return when {
            isErrorState() || !isOutgoing() -> ChatMessageStatusIcon.NONE
            message.isRead -> ChatMessageStatusIcon.READ
            else -> ChatMessageStatusIcon.SENT
        }
    }

    override fun getIcon(): ChatWarningIcon {
        return when {
            isErrorState() -> ChatWarningIcon.ALERT
            else -> ChatWarningIcon.NONE
        }
    }

    override fun getAlertTextAlpha(): Float = 1f

    override fun getAlertTextColor(): Int = R.color.colorRedLight

    override fun getAlertText(): Int {
        return when {
            message.isShowingWarningToUser() -> R.string.chat_message_not_sent
            else -> R.string.empty
        }
    }

    override fun getId(): Long = message.id

    override fun isDateVisible(): ObservableBoolean = isDateVisibleObservable

    override fun setDateVisible(isVisible: Boolean) {
        isDateVisibleObservable.set(isVisible)
    }

    override fun requireReading(): Boolean = false

    private fun isErrorState() = message.isShowingWarningToUser()

}