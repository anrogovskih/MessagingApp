package com.axmor.fsinphone.videomessages.ui.broadcastReceivers

import android.content.BroadcastReceiver
import android.content.IntentFilter

abstract class ReceiverWithIntentFilter: BroadcastReceiver() {
    abstract fun getFilter() : IntentFilter
}
