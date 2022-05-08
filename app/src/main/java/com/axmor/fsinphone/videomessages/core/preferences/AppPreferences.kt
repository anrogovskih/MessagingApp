package com.axmor.fsinphone.videomessages.core.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("CommitPrefEdits")
@Singleton
class AppPreferences @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private const val PREF_NAME = "MainPrefs"

        private const val KEY_API_TOKEN = "API_TOKEN"
        private const val KEY_VIDEO_ACCOUNT = "VIDEO_ACCOUNT"
        private const val KEY_EMAIL = "EMAIL"
    }

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var accessToken: String
        get() = sharedPrefs.getString(KEY_API_TOKEN, "")!!
        set(value) = saveString(KEY_API_TOKEN, value)

    var voxAccount: String?
        get() = sharedPrefs.getString(KEY_VIDEO_ACCOUNT, null)
        set(value) = saveString(KEY_VIDEO_ACCOUNT, value)

    var email: String?
        get() = sharedPrefs.getString(KEY_EMAIL, null)
        set(value) = saveString(KEY_EMAIL, value)


    private fun saveString(key: String, value: String?) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    private fun saveInt(key: String?, value: Int) {
        sharedPrefs.edit().putInt(key, value).apply()
    }

    private fun saveLong(key: String, value: Long) {
        sharedPrefs.edit().putLong(key, value).apply()
    }

    private fun saveBoolean(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    private fun saveStringSet(key: String, values: Set<String>?) {
        sharedPrefs.edit().putStringSet(key, values).apply()
    }
}