package com.axmor.fsinphone.videomessages.core.exceptions

import android.content.Context
import com.axmor.fsinphone.videomessages.R

class InvalidInputError(val type: InputType): Exception() {
    fun getText(context: Context): String {
        return when(type) {
            InputType.EMAIL -> context.getString(R.string.error_wrong_email)
        }
    }
}

enum class InputType {
    EMAIL
}