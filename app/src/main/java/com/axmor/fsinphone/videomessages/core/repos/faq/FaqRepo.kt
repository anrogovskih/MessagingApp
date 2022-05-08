package com.axmor.fsinphone.videomessages.core.repos.faq

import androidx.lifecycle.LiveData
import com.axmor.fsinphone.videomessages.core.network.objects.faq.FaqQuestion

interface FaqRepo {
    val faqLiveData: LiveData<List<FaqQuestion>>

    suspend fun loadIfNoData()
}