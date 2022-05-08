package com.axmor.fsinphone.videomessages.core.use_cases

import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.network.objects.faq.GetFaqRequest
import com.axmor.fsinphone.videomessages.core.network.objects.faq.GetFaqResponse

object GetFaqUseCase {
    suspend fun execute(deviceId: String): GetFaqResponse {
        val cachedProfile = DatabaseManager.getDb().userProfileDao().getProfile()
        val request = GetFaqRequest(cachedProfile!!.phoneNumber, deviceId)

        return GetFaqResponse(emptyList())
    }
}