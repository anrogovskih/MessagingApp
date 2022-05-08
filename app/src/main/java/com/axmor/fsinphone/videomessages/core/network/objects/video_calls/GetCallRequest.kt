package com.axmor.fsinphone.videomessages.core.network.objects.video_calls

import com.axmor.fsinphone.videomessages.core.network.objects.BaseRequestNoAction

data class GetCallRequest(
    // хэш полученный методом requestOneTimeLoginKey
    val key: String,
    // идентификатор звонка в пуш уведомлении
    val call_id: String
): BaseRequestNoAction()
