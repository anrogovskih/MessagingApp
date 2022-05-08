package com.axmor.fsinphone.videomessages.core.repos.notifications

import kotlinx.coroutines.flow.MutableSharedFlow

interface NotificationsRepo {
    //message id
    val newMessage: MutableSharedFlow<String>
    //contact id
    val remoteHangup: MutableSharedFlow<String>
    //call id
    val answeredThroughNotification: MutableSharedFlow<String>

    fun cancelNotificationsById(id: Int)

    fun showMessageSendFailure(contactId: Long)

    suspend fun onDataMessageReceived(data: Map<String, String>)

    fun isCallInProgress(): Boolean
}