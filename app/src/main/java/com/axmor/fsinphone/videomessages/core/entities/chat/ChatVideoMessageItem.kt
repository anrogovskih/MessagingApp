package com.axmor.fsinphone.videomessages.core.entities.chat

import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageWithImageItem
import timber.log.Timber

class ChatVideoMessageItem(message: ChatMessageEntityWithFile) : ChatMessageWithImageItem(message) {

    override fun getPreview(): String {
        return if (file?.exists() == true)
            file.path
        else
            message.videoData?.thumb?:""
    }

    override fun getCentralIcon(): Icon = Icon.PLAY
}