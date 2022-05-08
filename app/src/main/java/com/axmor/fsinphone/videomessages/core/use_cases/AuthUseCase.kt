@file:Suppress("BlockingMethodInNonBlockingContext")

package com.axmor.fsinphone.videomessages.core.use_cases

import android.app.Application
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.toUserProfile
import com.axmor.fsinphone.videomessages.core.network.objects.get_settings.GetSettingsResponse
import flavours.core.PushTokenRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

object AuthUseCase {
    suspend fun execute(application: Application, phoneNumber: String) = withContext(Dispatchers.IO){
        val settingsResponse = GetSettingsResponse("some_id")
        settingsResponse.checkResponse()

        val profile = settingsResponse.toUserProfile(phoneNumber)
        DatabaseManager.getDb().userProfileDao().insert(profile)

        sendPushToken(application)
    }

    private suspend fun sendPushToken(application: Application){
        try {
            val tokenAndType = PushTokenRetriever.getToken(application)
            val type = tokenAndType.first
            val token = tokenAndType.second ?: throw NullPointerException("token is null for type $type")
            SendPushTokenUseCase.execute(application, token, type)
        }
        catch (e: Exception){
            e.printStackTrace()
            Timber.w("AuthUseCase: sendPushToken failed with error ${e.localizedMessage}")
        }
    }
}