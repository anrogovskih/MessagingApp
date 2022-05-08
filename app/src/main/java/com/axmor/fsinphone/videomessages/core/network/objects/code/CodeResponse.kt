package com.axmor.fsinphone.videomessages.core.network.objects.code

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse

data class CodeResponse(
    val user_id: Int,
    val sms_text: String?
) : UnifiedResponse()