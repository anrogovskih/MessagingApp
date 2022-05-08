package com.axmor.fsinphone.videomessages.core.network.objects.payment

data class PaymentRequest(
    val amount: Int,
    val card_number: String,
    val email: String
)