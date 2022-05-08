package com.axmor.fsinphone.videomessages.core.network.objects.faq

import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse

data class GetFaqResponse(
    val questions: List<FaqQuestion>
) : UnifiedResponse()