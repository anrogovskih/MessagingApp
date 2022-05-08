package com.axmor.fsinphone.videomessages.core.network.objects

abstract class BaseRequest: BaseRequestNoAction() {
    abstract val action: String
}