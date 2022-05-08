package com.axmor.fsinphone.videomessages.core.entities.chat.base

import androidx.databinding.ObservableBoolean
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageStatusIcon
import com.axmor.fsinphone.videomessages.core.enums.ChatWarningIcon
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatMessageStatus

abstract class ChatMessageItem(val message: ChatMessageEntity): ChatItemWithStatus {

    val itemClick: ObservableBoolean = ObservableBoolean()
    val itemLongClick: ObservableBoolean = ObservableBoolean()
    val isSelected: ObservableBoolean = ObservableBoolean()

    private var isDateVisibleObservable: ObservableBoolean = ObservableBoolean()

    override fun getTime(): Long = message.createdAt * 1000

    override fun isOutgoing(): Boolean = message.isOutgoing

    override fun getStatus(): ChatMessageStatusIcon {
        return when(getIcon()){
            ChatWarningIcon.NONE -> getStatusIcon()
            else -> ChatMessageStatusIcon.NONE
        }
    }

    override fun getIcon(): ChatWarningIcon {
        return when{
            isErrorState() -> ChatWarningIcon.ALERT
            isOnModeration() -> ChatWarningIcon.WATCH_LATER
            else -> ChatWarningIcon.NONE
        }
    }

    override fun getAlertTextAlpha(): Float = 1f

    override fun getAlertText(): Int {
        return when {
            message.isShowingWarningToUser() -> R.string.chat_message_not_sent
            isOnModeration() -> R.string.chat_message_on_moderation
            isDeclined() -> R.string.chat_message_declined
            else -> R.string.empty
        }
    }

    override fun getId(): Long = message.id

    override fun isDateVisible(): ObservableBoolean = isDateVisibleObservable

    override fun setDateVisible(isVisible: Boolean) {
        isDateVisibleObservable.set(isVisible)
    }

    override fun requireReading(): Boolean {
        return !isOutgoing() && !message.isRead
    }

    override fun getBackground(): Int {
        return when{
            isErrorState() -> R.color.colorRedVeryLight2
            isOutgoing() -> R.color.colorMintCream
            else -> android.R.color.white
        }
    }

    protected fun isOnModeration(): Boolean = message.status == ChatMessageStatus.MODERATION

    protected fun isDeclined(): Boolean = message.status == ChatMessageStatus.DECLINED

    protected fun isErrorState() = isDeclined() || message.isShowingWarningToUser()

    protected fun getStatusIcon(): ChatMessageStatusIcon{
        return when {
            !message.isOutgoing -> ChatMessageStatusIcon.NONE
            message.isRead -> ChatMessageStatusIcon.READ
            message.status == ChatMessageStatus.RECEIVED -> ChatMessageStatusIcon.DELIVERED
            message.status == ChatMessageStatus.SENT -> ChatMessageStatusIcon.SENT
            else -> ChatMessageStatusIcon.NONE
        }
    }
}