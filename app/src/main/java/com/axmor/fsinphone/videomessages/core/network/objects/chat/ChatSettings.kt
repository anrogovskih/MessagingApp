package com.axmor.fsinphone.videomessages.core.network.objects.chat

data class ChatSettings(
    val video: ChatSetting,
    val photo: ChatSetting,
    val text: ChatSetting
) {
    fun attachmentsAllowed() = video.is_allowed || photo.is_allowed
    fun isChatAllowed() = video.is_allowed || photo.is_allowed || text.is_allowed
}