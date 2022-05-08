package com.axmor.fsinphone.videomessages.core.entities.chat.base

import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import java.io.File

abstract class ChatMessageWithImageItem(message: ChatMessageEntityWithFile) : ChatMessageItem(message.message) {

    protected val file: File? = message.localFileEntity?.let { File(it.filePath) }

    abstract fun getPreview(): String

    abstract fun getCentralIcon(): Icon

    override fun getAlertTextColor(): Int {
        return when {
            isOnModeration() -> android.R.color.white
            else -> R.color.colorRedLight
        }
    }

    enum class Icon{
        PLAY, NONE
    }
}