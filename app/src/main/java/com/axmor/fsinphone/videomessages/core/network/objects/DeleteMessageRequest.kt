package com.axmor.fsinphone.videomessages.core.network.objects

data class DeleteMessageRequest(
    val message_id: Long,
    override val phone_number: String,
    override val device_token: String,
    override val action: String = "action_name"
): BaseAuthorizedRequest()