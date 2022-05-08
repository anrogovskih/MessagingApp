package com.axmor.fsinphone.videomessages.core.entities.chat.base

import androidx.databinding.ObservableBoolean

interface ChatTextBubbleItem: ChatItemWithStatus {

    val itemLongClick: ObservableBoolean?

    val isSelected: ObservableBoolean

    fun getText(): String

    fun bubbleClickObservable(): ObservableBoolean?
}