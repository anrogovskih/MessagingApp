package com.axmor.fsinphone.videomessages.core.network.objects

abstract class BaseAuthorizedRequest: BaseRequest() {
    abstract val phone_number: String
    abstract val device_token: String
}