package com.axmor.fsinphone.videomessages.ui.screens.main.dataClasses

import android.content.Context
import androidx.databinding.ObservableBoolean
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactWithMessage
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.IChatContactItem
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType

class ChatContactItem(private val contact: ChatContactWithMessage): IChatContactItem {

    override val clickObserver: ObservableBoolean = ObservableBoolean()

    /**
     * Название диалога
     */
    override fun getPrimaryText(): String = contact.chatContactEntity.name

    /**
     * Аватар контакта
     */
    override fun getAvatar(): String? = contact.chatContactEntity.image

    /**
     * Превью последнего сообщения или его тип
     */
    override fun getSecondaryText(context: Context): String {
        val lastChatMessage = contact.lastMessage
        if (lastChatMessage != null) {
            return when (lastChatMessage.getMessageType()) {
                ChatMessageType.IMAGE, ChatMessageType.VIDEO, ChatMessageType.UNKNOWN -> context.getString(
                    lastChatMessage.getMessageType().typeStringRes
                )
                ChatMessageType.TEXT -> {
                    lastChatMessage.textData?.text ?: ""
                }
            }
        }

        val lastSupportMessage = contact.lastSupportMessage
        if (lastSupportMessage != null)
            return lastSupportMessage.text

        return ""
    }

    /**
     * Время создания последнего сообщения (в миллисекундах)
     */
    fun getLastMessageCreationTime(): Long {
        return contact.chatContactEntity.last_message_date
    }

    /**
     * Количество непрочитанных сообщений в диалоге
     */
    override fun getNewMessagesCount(): Int = contact.chatContactEntity.new_messages

    override fun isDialogWithSupport(): Boolean = contact.chatContactEntity.isSupport()

    override fun getId(): Long = contact.chatContactEntity.id
}