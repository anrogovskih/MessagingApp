package com.axmor.fsinphone.videomessages.core.network.objects.faq

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

data class GetFaqRequest(
    override val phone_number: String,
    override val device_token: String,
    override val action: String = "action_name"
) : BaseAuthorizedRequest()