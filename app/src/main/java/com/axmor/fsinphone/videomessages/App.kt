package com.axmor.fsinphone.videomessages

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.axmor.fsinphone.videomessages.common.extensions.getRingToneUri
import flavours.broadcastReceivers.PushTokenReceiver
import com.axmor.fsinphone.videomessages.common.extensions.registerReceiver
import com.axmor.fsinphone.videomessages.common.logging.FileLogTree
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.di.LogFile
import com.axmor.fsinphone.videomessages.core.network.ServerApi
import com.axmor.fsinphone.videomessages.core.preferences.AppPreferences
import com.axmor.fsinphone.videomessages.core.worker.LogCleaningWorker
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    companion object {
        const val UPLOAD_MESSAGES_CHANNEL_ID = "UPLOAD_MESSAGES_CHANNEL_ID"
        const val NEW_MESSAGES_CHANNEL_ID = "NEW_MESSAGES_CHANNEL_ID"
        const val VOIP_CHANNEL_ID = "VoipChannel"
    }

    @LogFile
    @Inject
    lateinit var logFile: File

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var preferences: AppPreferences

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        Timber.plant(FileLogTree(logFile))

        ServerApi.init(preferences)
        DatabaseManager.init(this)
        createNotificationChannelIfNeeded()
        initWorkers()

        BigImageViewer.initialize(GlideImageLoader.with(this))
        registerReceiver(PushTokenReceiver())
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun initWorkers() {
        val manager = WorkManager.getInstance(this)
        val clearLogsRequest = OneTimeWorkRequest.Builder(LogCleaningWorker::class.java).build()
        manager.enqueue(clearLogsRequest)
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importanceDefault = NotificationManager.IMPORTANCE_DEFAULT
            val importanceHigh = NotificationManager.IMPORTANCE_HIGH

            val uploadChannelName = getString(R.string.notification_send_video_channel_name)
            val uploadChannel = NotificationChannel(
                UPLOAD_MESSAGES_CHANNEL_ID,
                uploadChannelName,
                importanceDefault
            )
            uploadChannel.description =
                getString(R.string.notification_send_video_channel_description)
            notificationManager.createNotificationChannel(uploadChannel)

            val newMessageChannelName = getString(R.string.notification_new_message_channel_name)
            val newMessageChannel =
                NotificationChannel(NEW_MESSAGES_CHANNEL_ID, newMessageChannelName, importanceHigh)
            newMessageChannel.description =
                getString(R.string.notification_new_message_channel_description)
            notificationManager.createNotificationChannel(newMessageChannel)

            val channel = NotificationChannel(
                VOIP_CHANNEL_ID,
                getString(R.string.notification_video_call_channel_name),
                importanceHigh
            ).apply {
                description = getString(R.string.notification_video_call_channel_description)
                val attrs = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION_SIGNALLING)
                    .build()
                setSound(applicationContext.getRingToneUri(), attrs)
                enableVibration(true)
//                vibrationPattern = VideoCallNotificationService.vibrationPattern
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}