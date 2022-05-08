package com.axmor.fsinphone.videomessages.core.network.objects.chat.edit_contact

import com.axmor.fsinphone.videomessages.core.network.objects.BaseAuthorizedRequest

data class ChatEditContactRequest(
    override val phone_number: String,
    override val device_token: String,
    val contact_id: Long,
    val user_id: String,
    val name: String?,
    val image: String?,
    override val action: String = "action_name"
) : BaseAuthorizedRequest()