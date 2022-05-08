package com.axmor.fsinphone.videomessages.core.entities

import android.content.Context
import androidx.databinding.ObservableBoolean

interface IChatContactItem {
    val clickObserver: ObservableBoolean

    fun getAvatar(): String?

    fun isDialogWithSupport(): Boolean

    fun getPrimaryText(): String

    fun getSecondaryText(context: Context): String

    fun getNewMessagesCount(): Int

    fun getId(): Long
}