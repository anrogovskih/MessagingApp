package com.axmor.fsinphone.videomessages.common.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import com.axmor.fsinphone.videomessages.core.exceptions.DeviceTokenError
import com.axmor.fsinphone.videomessages.ui.Navigator
import com.axmor.fsinphone.videomessages.ui.broadcastReceivers.ReceiverWithIntentFilter

fun FragmentActivity?.checkAuthError(e: java.lang.Exception): Boolean {
    if (this is Activity && e is DeviceTokenError) {
        Navigator.goEnterPhone(this@checkAuthError)
        return true
    }
    return false
}

fun Context.getDefaultSharedPreferences(): SharedPreferences =
    getSharedPreferences("default", Context.MODE_PRIVATE)

fun Context.registerReceiver(receiver: ReceiverWithIntentFilter): Intent? {
    return registerReceiver(receiver, receiver.getFilter())
}

fun Context.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        startForegroundService(intent)
    else
        startService(intent)
}

fun Context.goToPermissionsSettings() {
    Intent()
        .apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            addCategory(Intent.CATEGORY_DEFAULT)
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        .startActivity(this)
}

fun Context.getRingToneUri(): Uri {
    return Settings.System.DEFAULT_RINGTONE_URI
}

fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork?.isConnected == true
}
