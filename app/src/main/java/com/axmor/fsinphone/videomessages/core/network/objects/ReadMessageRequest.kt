package com.axmor.fsinphone.videomessages.core.network.objects

data class ReadMessageRequest(
    val message_id: Long,
    override val phone_number: String,
    override val device_token: String,
    val include_older_messages: Boolean? = null,
    val isRead: Boolean = true,
    override val action: String = "action_name"
): BaseAuthorizedRequest() {

}