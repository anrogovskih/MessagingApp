package com.axmor.fsinphone.videomessages.core.repos.chatContacts

import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactWithMessage
import com.axmor.fsinphone.videomessages.core.entities.NewAvatar
import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import kotlinx.coroutines.flow.Flow

interface ChatContactsRepo {

    suspend fun load()

    suspend fun editContact(contact: ChatContactEntity, name: String, avatar: NewAvatar)

    suspend fun getContact(contactId: Long): ChatContactEntity?

    fun getContactsWithMessagesFlow(): Flow<List<ChatContactWithMessage>>

    fun getPrisonersFlow(): Flow<List<ChatContactEntity>>

    fun getContactFlow(contactId: Long): Flow<ChatContactEntity?>
}