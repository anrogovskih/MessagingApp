package com.axmor.fsinphone.videomessages.core.network.objects.code

import android.os.Build
import com.axmor.fsinphone.videomessages.core.network.objects.BaseRequest

data class CodeRequest(
    val phone_number: String,
    val device_token: String,
    val platform: String = "android",
    val model: String = "${Build.MANUFACTURER} ${Build.MODEL}",
    override val action: String = "action_name"
) : BaseRequest()