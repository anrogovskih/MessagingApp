package com.axmor.fsinphone.videomessages.core.network.objects.support

import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import com.google.gson.annotations.SerializedName


/**
 * Показывает список сообщений в чате, с указанием параметров is_answer, маркирующим авторство.
 * Если is_answer == true, значит это ответ со стороны техподдержки, если false - это сообщение от
 * заключенного.
 */
data class SupportChatResponse(
    val messages: List<SupportChatMessage>,
    val action: String = "action_name"
): UnifiedResponse()

data class SupportChatMessage(
    val id: Long,
    @SerializedName(TEXT_SERIALIZED_NAME)
    val text: String,
    val is_answer: Boolean,
    val is_viewed: Boolean,
    val created: String
){
    fun isRead(): Boolean = is_viewed

    fun isOutgoing(): Boolean = !is_answer

    fun toSupportChatMessageEntity() = SupportChatMessageEntity(
        id,
        text,
        isRead(),
        isOutgoing(),
        /*ServerApi.getDefaultDateFormat().parse(created)?.time ?:*/ 0L
    )

    companion object {
        const val TEXT_SERIALIZED_NAME = "body"
    }
}