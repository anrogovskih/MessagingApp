package com.axmor.fsinphone.videomessages.core.network.objects.video_calls

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse

data class GetCallResponse(
    // токен для логина
    val call_token: String,
    //максимальная длительность этого звонка, с учётом баланса заключенного
    val call_length: Int,
): UnifiedResponse()
