package com.axmor.fsinphone.videomessages.core.entities.chat.base

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageStatusIcon
import com.axmor.fsinphone.videomessages.core.enums.ChatWarningIcon

interface ChatItemWithStatus: ChatItem {
    fun getTime(): Long

    fun isOutgoing(): Boolean

    fun getStatus(): ChatMessageStatusIcon

    fun getIcon(): ChatWarningIcon

    fun getAlertTextAlpha(): Float

    @ColorRes
    fun getAlertTextColor(): Int

    @StringRes
    fun getAlertText(): Int

    @ColorRes
    fun getBackground(): Int
}