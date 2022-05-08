package com.axmor.fsinphone.videomessages.core.db.objects

import android.webkit.URLUtil
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.ImagesUtils
import com.axmor.fsinphone.videomessages.common.extensions.isValidUrl
import com.axmor.fsinphone.videomessages.core.network.objects.chat.CardData
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatSettings

@Entity
data class ChatContactEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val object_title: String?,
    val last_activity: String?,
    val last_message_date: Long,
    val new_messages: Int,
    val image: String?,
    val lastMessageId: Long?,
    val chatSettings: ChatSettings?,
    @Embedded
    val cardData: CardData?,
    @ColumnInfo(defaultValue = "0")
    val isAvatarUploaded: Boolean
) {

    fun isSupport(): Boolean {
        return id == Constants.ID_SUPPORT
    }

    fun copyWith(newMessages: Int, lastMessageId: Long?) =
        copy(new_messages = newMessages, lastMessageId = lastMessageId)

    fun isDefaultAvatar() = image.isNullOrEmpty() || (!image.isValidUrl() && !isAvatarUploaded)

    fun getFormattedCard(): String? = cardData?.number?.let { cardNumber ->
        val firstPart = cardNumber.take(cardNumber.length / 2)
        val lastPart = cardNumber.takeLast(cardNumber.length / 2)
        return@let "$firstPart-$lastPart"
    }

    fun getDefaultAvatarBase64() = ImagesUtils.getDefaultAvatarBase64(name)

    fun getDefaultAvatarOrNull() = if (isDefaultAvatar()) image else null
}