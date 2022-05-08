package com.axmor.fsinphone.videomessages.ui

interface BackPressHandler {
    fun onBackPressed()
    fun setBackPressLocked(isLocked: Boolean)
    fun backTo(tag: String)
}