package com.axmor.fsinphone.videomessages.core.repos.faq

import androidx.lifecycle.MutableLiveData
import com.axmor.fsinphone.videomessages.core.di.DeviceId
import com.axmor.fsinphone.videomessages.core.network.objects.faq.FaqQuestion
import com.axmor.fsinphone.videomessages.core.use_cases.GetFaqUseCase
import javax.inject.Inject

class FaqRepoImpl @Inject constructor(
    @DeviceId private val deviceId: String
) : FaqRepo {
    override val faqLiveData: MutableLiveData<List<FaqQuestion>> = MutableLiveData()

    override suspend fun loadIfNoData() {
        if (faqLiveData.value == null) {
            val response = GetFaqUseCase.execute(deviceId)
            response.checkResponse()

            faqLiveData.postValue(response.questions)
        }
    }
}