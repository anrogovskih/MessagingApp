package com.axmor.fsinphone.videomessages.core.network.objects.confirmation

import com.axmor.fsinphone.videomessages.core.network.objects.BaseRequest

data class ConfirmationRequest(
    val user_id: Int,
    val confirmation_code: String,
    override val action: String = "action_name"
) : BaseRequest()