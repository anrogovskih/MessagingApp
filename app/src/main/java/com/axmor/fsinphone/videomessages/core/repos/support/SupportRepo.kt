package com.axmor.fsinphone.videomessages.core.repos.support

import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import kotlinx.coroutines.flow.Flow

interface SupportRepo {

    fun getSupportMessagesFlow(): Flow<List<SupportChatMessageEntity>>

    suspend fun loadMessages()

    suspend fun send(text: String)

    suspend fun resend(message: SupportChatMessageEntity)

    suspend fun delete(message: SupportChatMessageEntity)

}