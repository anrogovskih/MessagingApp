package com.axmor.fsinphone.videomessages.core.repos.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.axmor.fsinphone.videomessages.App
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.entities.push.MessageData
import com.axmor.fsinphone.videomessages.ui.screens.main.MainActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber
import javax.inject.Inject


class NotificationsRepoImpl @Inject constructor(
    @ApplicationContext private val appContext: Context,
) : NotificationsRepo {

    override val newMessage: MutableSharedFlow<String> = MutableSharedFlow(0)
    override val remoteHangup: MutableSharedFlow<String> = MutableSharedFlow(0)
    override val answeredThroughNotification: MutableSharedFlow<String> = MutableSharedFlow(0)

    init {
        Timber.d("init")
    }
    private var currentCallId: String? = null

    override fun cancelNotificationsById(id: Int) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    override suspend fun onDataMessageReceived(data: Map<String, String>) {
        val messageData = data.toMessageData()

        when {
            messageData.isEndCallNotification -> endCall(messageData)
            messageData.isNewMessageNotification -> showNewMessageNotification(messageData)
            messageData.isVideoCallNotification -> showIncomingVideoCall(messageData)
            else -> showCommonMessageNotification(messageData)
        }
    }

    override fun showMessageSendFailure(contactId: Long) {
        val message = appContext.getString(R.string.notification_send_video_failure_title)
        safeCall(message) {
            val intent = MainActivity.getIntentShowChat(appContext, contactId)
            val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

            val notificationBuilder =
                NotificationCompat.Builder(appContext, App.UPLOAD_MESSAGES_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_launcher_notifications)
                    .setContentTitle(message)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVibrate(longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L))
                    .setContentIntent(pendingIntent)

            NotificationManagerCompat
                .from(appContext)
                .notify(Constants.FAILURE_NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    override fun isCallInProgress(): Boolean = currentCallId != null

    private suspend fun showNewMessageNotification(messageData: MessageData) {
        if (messageData.id != null && messageData.title != null) {
            safeCall(null) {
                val intent =
                    MainActivity.getIntentShowChat(appContext, messageData.id.toLong())
                val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

                val notificationBuilder =
                    NotificationCompat.Builder(appContext, App.NEW_MESSAGES_CHANNEL_ID)
                        .setOngoing(false)
                        .setSmallIcon(R.drawable.ic_launcher_notifications)
                        .setContentTitle(messageData.title)
                        .setContentText(messageData.body)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setVibrate(longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L))
                        .setContentIntent(pendingIntent)

                NotificationManagerCompat
                    .from(appContext)
                    .notify(Constants.NEW_MESSAGE_NOTIFICATION_ID, notificationBuilder.build())
            }
            newMessage.emit(messageData.id)
        }
    }

    private fun showCommonMessageNotification(messageData: MessageData) {
        if (messageData.title != null) {
            safeCall(null) {
                val notificationBuilder =
                    NotificationCompat.Builder(appContext, App.UPLOAD_MESSAGES_CHANNEL_ID)
                        .setOngoing(false)
                        .setSmallIcon(R.drawable.ic_launcher_notifications)
                        .setContentTitle(messageData.title)
                        .setContentText(messageData.body)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setVibrate(longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L))

                NotificationManagerCompat
                    .from(appContext)
                    .notify(Constants.COMMON_MESSAGE_NOTIFICATION_ID, notificationBuilder.build())
            }
        }
    }

    private suspend fun endCall(messageData: MessageData) {
        currentCallId = null
        Timber.w("endCall; messageData.id  = ${messageData.id}")
        if (messageData.id != null) remoteHangup.emit(messageData.id)

    }

    //https://developer.android.com/guide/components/activities/background-starts
    private suspend fun showIncomingVideoCall(messageData: MessageData) {
        currentCallId = messageData.callId
        val contactName = messageData.getContactName()

    }

    private suspend fun MessageData.getContactName(): String? {
        return contactId?.let { id ->
            DatabaseManager.getDb().chatContactsDao().getChatContact(id)?.name
        }
    }

    /**
     * Следует оборачивать показ нотификаций в этот метод.
     * @param message - сообщение, которое будет выведено тостом при ошибке показа пуша. Если null,
     * toast не будет показан.
     */
    private fun safeCall(message: String?, call: () -> Unit) {
        try {
            call.invoke()
        } catch (e: Exception) {
            Timber.e(e)
            FirebaseCrashlytics.getInstance().recordException(e)
            if (message != null)
                Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun Map<String, String>.toMessageData() = MessageData(
        this[KEY_ID],
        this[KEY_TYPE],
        this[KEY_TITLE],
        this[KEY_BODY],
        this[KEY_SOUND]
    )

    companion object {
        const val KEY_ID = "id"
        const val KEY_TYPE = "type"
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
        const val KEY_SOUND = "sound"
    }
}