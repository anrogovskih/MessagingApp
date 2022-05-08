package com.axmor.fsinphone.videomessages.core.enums

import androidx.annotation.StringRes
import com.axmor.fsinphone.videomessages.R

const val TYPE_TEXT = "text"
const val TYPE_IMAGE = "image"
const val TYPE_VIDEO = "video"

const val TYPE_UNKNOWN = "unknown"

enum class ChatMessageType(
    val typeString: String,
    @StringRes
    val typeStringRes: Int
) {

    VIDEO(TYPE_VIDEO, R.string.chat_message_type_video),
    IMAGE(TYPE_IMAGE, R.string.chat_message_type_image),
    TEXT(TYPE_TEXT, R.string.chat_message_type_text),

    UNKNOWN(TYPE_UNKNOWN, R.string.chat_message_type_unknown);

    companion object {
        fun fromString(typeString: String): ChatMessageType{
            return when(typeString){
                TYPE_VIDEO -> VIDEO
                TYPE_IMAGE -> IMAGE
                TYPE_TEXT -> TEXT
                else -> UNKNOWN
            }
        }
    }
}