package com.axmor.fsinphone.videomessages.core.network.serialization

import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatContactLastMessage
import com.axmor.fsinphone.videomessages.core.network.objects.chat.ChatMessage
import com.axmor.fsinphone.videomessages.core.network.objects.support.SupportChatMessage
import com.google.gson.*
import java.lang.reflect.Type

class ChatContactLastMessageDeserializer: JsonDeserializer<ChatContactLastMessage> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ChatContactLastMessage {
        if (json != null){
            if (json.asJsonObject.has(ChatMessage.TYPE_SERIALIZED_NAME)){
                val chatMessage = Gson().fromJson(json, ChatMessage::class.java)
                return ChatContactLastMessage(chatMessage = chatMessage)
            }
            else if (json.asJsonObject.has(SupportChatMessage.TEXT_SERIALIZED_NAME)){
                val supportMessage = Gson().fromJson(json, SupportChatMessage::class.java)
                return ChatContactLastMessage(supportChatMessage = supportMessage)
            }
        }
        return ChatContactLastMessage()
    }
}