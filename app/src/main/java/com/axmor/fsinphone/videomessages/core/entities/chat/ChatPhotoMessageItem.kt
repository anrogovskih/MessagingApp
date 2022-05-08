package com.axmor.fsinphone.videomessages.core.entities.chat

import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageWithImageItem

class ChatPhotoMessageItem(message: ChatMessageEntityWithFile) : ChatMessageWithImageItem(message) {

    override fun getPreview(): String {
        return if (file?.exists() == true)
            file.path
        else
            message.imageData?.thumb?:""
    }

    override fun getCentralIcon(): Icon = Icon.NONE
}