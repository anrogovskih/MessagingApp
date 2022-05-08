package com.axmor.fsinphone.videomessages.core.network.objects.get_settings

import com.axmor.fsinphone.videomessages.core.network.objects.BaseRequest

data class GetSettingsRequest(
    val phone_number: String,
    val device_token: String,
    override val action: String = "action_name"
): BaseRequest()