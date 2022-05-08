package com.axmor.fsinphone.videomessages.core.network.objects.get_settings

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse

data class GetSettingsResponse(
    val user_id: String,
) : UnifiedResponse()