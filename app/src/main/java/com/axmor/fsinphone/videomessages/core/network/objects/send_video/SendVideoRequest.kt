package com.axmor.fsinphone.videomessages.core.network.objects.send_video

import com.axmor.fsinphone.videomessages.core.network.objects.BaseRequest

data class SendVideoRequest(
    val user_id: String,
    val contact_id: String,
    val duration: Int,
    val phone_number: String,
    val device_token: String,
    val file_extension: String = "mp4",
    override val action: String = "action_name"
) : BaseRequest()