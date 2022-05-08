package com.axmor.fsinphone.videomessages.core.repos.chat

import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

interface ChatRepo {

    fun getMessagesFlow(contactId: Long): Flow<List<ChatMessageEntityWithFile>>

    fun createTotalFlow(): Flow<Int?>

    fun getMediaFileFlow(messageId: Long): Flow<File?>

    suspend fun getMediaFileIfExists(messageId: Long): File?

    suspend fun loadMore(contactId: Long, isIncrementingPage: Boolean)

    suspend fun updateLast(contactId: Long)

    suspend fun sendTextMessage(contactId: Long, text: String)

    suspend fun sendVideoMessage(contactId: Long, file: File, duration: Long)

    suspend fun sendPhotoMessage(contactId: Long, file: File)

    suspend fun resend(message: ChatMessageEntity)

    suspend fun delete(message: ChatMessageEntity)

    suspend fun readAll(contactId: Long)
}