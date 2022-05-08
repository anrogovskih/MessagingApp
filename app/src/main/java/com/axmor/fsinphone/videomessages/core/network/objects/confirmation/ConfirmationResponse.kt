package com.axmor.fsinphone.videomessages.core.network.objects.confirmation

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse

class ConfirmationResponse(
    val access_token: String,
    //настройки видеозвонков
    val video_call_data: VideoCallData?
): UnifiedResponse()