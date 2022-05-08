package com.axmor.fsinphone.videomessages.core.repos.support

import androidx.work.WorkManager
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.di.DeviceId
import com.axmor.fsinphone.videomessages.core.network.objects.support.SupportChatRequest
import com.axmor.fsinphone.videomessages.core.network.objects.support.SupportChatResponse
import com.axmor.fsinphone.videomessages.core.use_cases.SendMessagesWorkerUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendSupportMessageUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class SupportRepoImpl @Inject constructor(
    @DeviceId private val deviceId: String,
    private val workManager: WorkManager
): SupportRepo {

    override fun getSupportMessagesFlow(): Flow<List<SupportChatMessageEntity>> {
        return DatabaseManager.getDb().supportChatMessageDao().getAllDistinctUntilChanged()
    }

    override suspend fun loadMessages() {
        val profile = DatabaseManager.getDb().userProfileDao().getProfile()!!
        val request = SupportChatRequest(profile.phoneNumber, deviceId)
        val response = SupportChatResponse(emptyList())
        response.checkResponse()
        val entities = response.messages.map { it.toSupportChatMessageEntity() }
        DatabaseManager.getDb().supportChatMessageDao().insert(entities)
    }

    override suspend fun send(text: String) {
        val minId = DatabaseManager.getDb().chatMessagesDao().getMinId()
        val newPrimaryKey = if (minId != null && minId < 0) minId - 1 else Constants.FIRST_ID_FOR_DRAFT
        val draftMessage = SupportChatMessageEntity(
            newPrimaryKey,
            text,
            isRead = false,
            isOutgoing = true,
            createdAt = System.currentTimeMillis(),
            isDraft = true
        )
        DatabaseManager.getDb().supportChatMessageDao().insert(draftMessage)

        val profile = DatabaseManager.getDb().userProfileDao().getProfile()!!

        try {
            SendSupportMessageUseCase.execute(draftMessage, deviceId, profile.phoneNumber)
            //тест неотправленных сообщений
//            throw Exception("this is an expected exception")
        }
        catch (e: Exception){
            Timber.w("SupportRepoImpl.send error: ${e.localizedMessage}")
            SendMessagesWorkerUseCase.execute(workManager)
        }
    }

    override suspend fun resend(message: SupportChatMessageEntity) {
        val copy = message.copyToResend()
        DatabaseManager.getDb().supportChatMessageDao().insert(copy)
        SendMessagesWorkerUseCase.execute(workManager)
    }

    override suspend fun delete(message: SupportChatMessageEntity) {
        DatabaseManager.getDb().supportChatMessageDao().delete(message)
    }
}