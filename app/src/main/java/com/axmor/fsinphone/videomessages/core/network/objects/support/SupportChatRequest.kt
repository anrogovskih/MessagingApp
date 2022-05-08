package com.axmor.fsinphone.videomessages.core.network.objects.support


import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

class SupportChatRequest(
    override val phone_number: String,
    override val device_token: String,
    override val action: String = "action_name"
): BaseAuthorizedRequest()