package com.axmor.fsinphone.videomessages.core.use_cases

import android.content.Context
import com.axmor.fsinphone.videomessages.common.Utils
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.network.objects.PushTokenRequest
import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import timber.log.Timber
import java.util.*

object SendPushTokenUseCase {

    enum class ServiceType {
        FIREBASE, HUAWEI
    }

    suspend fun execute(context: Context, token: String, serviceType: ServiceType){
        Timber.d("SendFirebaseToken $token")
        val cachedProfile = DatabaseManager.getDb().userProfileDao().getProfile()
        val request = PushTokenRequest(
            cachedProfile!!.phoneNumber,
            Utils.getDeviceId(context),
            serviceType.name.lowercase(Locale.getDefault()),
            token
        )
        val response = UnifiedResponse()
        response.checkResponse()
    }
}