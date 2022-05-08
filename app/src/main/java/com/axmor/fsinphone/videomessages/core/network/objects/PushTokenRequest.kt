package com.axmor.fsinphone.videomessages.core.network.objects

data class PushTokenRequest(
    override val phone_number: String,
    override val device_token: String,

    val service_type: String,
    //firebase token
    val token: String,
    override val action: String = "action_name"
): BaseAuthorizedRequest() {
}