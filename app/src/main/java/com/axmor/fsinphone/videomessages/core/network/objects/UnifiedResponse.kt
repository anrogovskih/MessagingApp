package com.axmor.fsinphone.videomessages.core.network.objects

import com.axmor.fsinphone.videomessages.core.exceptions.DeviceTokenError
import com.axmor.fsinphone.videomessages.core.exceptions.ServerError
import com.google.gson.annotations.SerializedName

open class UnifiedResponse {
    /**
     * Одна из констант ниже
     */
    val status: String? = /*null*/ "ok"
    val data: String? = null
    @SerializedName("is_development")
    val isDevelopment: Boolean = false

    fun checkResponse() {
        if (status != OK) {

            checkForDeviceTokenError()

            throw ServerError(data.orEmpty())
        }
    }

    private fun checkForDeviceTokenError(){
        if (status == DEVICE_TOKEN_ERROR) throw DeviceTokenError()
    }

    /**
     * Константы статуса ответа сервера
     */
    companion object {
        const val OK: String = "ok"
        const val ERROR: String = "error"
        const val DEVICE_TOKEN_ERROR = "device_token_error"
        const val SESSION_ERROR: String = "session_error"
        const val REGISTRATION_REQUIRED_ERROR: String = "registration_required_error"
    }
}

