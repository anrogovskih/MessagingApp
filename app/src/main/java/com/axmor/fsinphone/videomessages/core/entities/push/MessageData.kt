package com.axmor.fsinphone.videomessages.core.entities.push

import androidx.core.text.isDigitsOnly
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

data class MessageData(
    val id: String?,
    val type: String?,
    val title: String?,
    val body: String?,
    val sound: String?
): Serializable {

    companion object {
        private const val TYPE_NEW_MESSAGE = "NEW_MESSAGE"
        private const val TYPE_END_CALL = "prisoner_end_call"
    }

    val callId: String?
    val contactId: Long?

    val isEndCallNotification = type == TYPE_END_CALL
    val isNewMessageNotification = id?.isDigitsOnly() == true && type == TYPE_NEW_MESSAGE
    val isVideoCallNotification: Boolean

    init {
        val bodyJson: JSONObject? = body?.let {
            try {
                JSONObject(it)
            }
            catch (e: JSONException){
//                e.printStackTrace()
                null
            }
        }

        callId = bodyJson?.optString("call_id")
        contactId = bodyJson?.getLong("contactId")
        isVideoCallNotification = callId.isNullOrEmpty().not()
    }
}