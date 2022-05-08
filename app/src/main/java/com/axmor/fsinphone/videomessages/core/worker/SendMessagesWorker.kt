package com.axmor.fsinphone.videomessages.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.di.DeviceId
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageType
import com.axmor.fsinphone.videomessages.core.repos.notifications.NotificationsRepo
import com.axmor.fsinphone.videomessages.core.use_cases.SendPhotoMessageUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendSupportMessageUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendTextMessageUseCase
import com.axmor.fsinphone.videomessages.core.use_cases.SendVideoMessageUseCase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class SendMessagesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val notificationsRepository: NotificationsRepo,
    @DeviceId private val deviceId: String
) : CoroutineWorker(appContext, params) {

    /**
     * id контактов, для которых были показаны нотификации об ошибке отправки сообщений
     */
    private val errorsForContacts = mutableListOf<Long>()

    companion object {
        const val TAG = "SendMessagesWorker"
    }

    override suspend fun doWork(): Result {
        val sendSupportMessagesResult = sendSupportMessages()
        val sendChatMessagesResult = sendChatMessages()
        val isSuccess = sendChatMessagesResult == Result.success()
                && sendSupportMessagesResult == Result.success()
        return if (isSuccess) Result.success() else Result.retry()
    }

    private suspend fun sendChatMessages(): Result{
        var result = Result.success()
        val dao = DatabaseManager.getDb().chatMessagesDao()
        val allDrafts = dao.getAllDraftsToSend()

        allDrafts.forEach { chatMessageEntity ->
            try {
                chatMessageEntity.send(deviceId)
                //позволяет эмулировать проблему с сервером
//                throw Exception("This is an expected exception")
            }
            catch (e: Exception){
                Timber.w("sendChatMessages error: ${e.localizedMessage}")
                FirebaseCrashlytics.getInstance().recordException(e)

                val copy = chatMessageEntity.copyAfterFailedAttempt()
                dao.insert(copy)

                if (copy.isShowingWarningToUser())
                    possiblyShowErrorNotification(chatMessageEntity.contactId)
                else
                    result = Result.retry()
            }
        }
        return result
    }

    private suspend fun sendSupportMessages(): Result{
        var result = Result.success()
        val dao = DatabaseManager.getDb().supportChatMessageDao()
        val allDrafts = dao.getAllDraftsToSend()
        val profile = DatabaseManager.getDb().userProfileDao().getProfile()!!

        allDrafts.forEach { messageEntity ->
            try {
                SendSupportMessageUseCase.execute(messageEntity, deviceId, profile.phoneNumber,)
                //позволяет эмулировать проблему с сервером
//                throw Exception("This is an expected exception")
            }
            catch (e: Exception){
                Timber.w("sendSupportMessages error: ${e.localizedMessage}")
                FirebaseCrashlytics.getInstance().recordException(e)

                val copy = messageEntity.copyAfterFailedAttempt()
                dao.insert(copy)

                if (copy.isShowingWarningToUser())
                    possiblyShowErrorNotification(Constants.ID_SUPPORT)
                else
                    result = Result.retry()
            }
        }
        return result
    }

    private fun possiblyShowErrorNotification(contactId: Long){
        if (!errorsForContacts.contains(contactId)){
            errorsForContacts.add(contactId)
            notificationsRepository.showMessageSendFailure(contactId)
        }
    }

    private suspend fun ChatMessageEntity.send(deviceId: String){
        when(getMessageType()){
            ChatMessageType.TEXT -> SendTextMessageUseCase.execute(this, deviceId)
            ChatMessageType.IMAGE -> SendPhotoMessageUseCase.execute(this, deviceId, applicationContext)
            ChatMessageType.VIDEO -> SendVideoMessageUseCase.execute(this, deviceId, applicationContext)
            else -> throw Exception("Unknown message type $type")
        }
    }
}