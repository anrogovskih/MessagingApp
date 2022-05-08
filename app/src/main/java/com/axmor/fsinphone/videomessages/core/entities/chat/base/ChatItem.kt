package com.axmor.fsinphone.videomessages.core.entities.chat.base

import androidx.databinding.ObservableBoolean

interface ChatItem {
    fun getId(): Long
    fun isDateVisible(): ObservableBoolean
    fun setDateVisible(isVisible: Boolean)
    fun requireReading(): Boolean
}