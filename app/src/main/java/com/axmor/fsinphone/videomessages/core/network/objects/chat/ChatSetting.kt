package com.axmor.fsinphone.videomessages.core.network.objects.chat

data class ChatSetting(
    val max_length: Int,
    val cost: Float,
    val is_allowed: Boolean,
    val is_moderated: Boolean
)