package com.axmor.fsinphone.videomessages.ui.listeners

import android.text.Editable
import android.text.TextWatcher

abstract class EmptyTextWatcher: TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(text: Editable?) {
    }
}