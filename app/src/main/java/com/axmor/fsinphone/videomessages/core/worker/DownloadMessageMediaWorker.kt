package com.axmor.fsinphone.videomessages.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.Utils
import com.axmor.fsinphone.videomessages.common.extensions.getExtension
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageFileEntity
import com.axmor.fsinphone.videomessages.core.enums.DownloadState
import com.axmor.fsinphone.videomessages.core.repos.download.DownloadsRepo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

@HiltWorker
class DownloadMessageMediaWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val downloadsRepository: DownloadsRepo
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TAG = "DownloadMessageMediaWorker"
    }

    override suspend fun doWork(): Result {

        val messageId = inputData.getLong(Constants.KEY_MESSAGE_ID, 0)
        Timber.d("doWork with messageId $messageId")

        try {
            if (messageId != 0L) return download(messageId)
        }
        catch (e: Exception){
            Timber.e(e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        downloadsRepository.postProgress(0, DownloadState.ERROR, messageId)
        return Result.failure()
    }

    private suspend fun download(messageId: Long): Result{
        val entity = DatabaseManager.getDb().chatMessagesDao().getById(messageId)!!
        Timber.d("entity received")
        val mediaFile = entity.download()
        Timber.d("mediaFile downloaded")

        val mediaFileEntity = ChatMessageFileEntity(entity.id, mediaFile.path)
        DatabaseManager.getDb().chatMessageFileEntityDao().insert(mediaFileEntity)
        return Result.success()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun ChatMessageEntity.download(): File = withContext(Dispatchers.IO){
        val downloadUrl = getDownloadUrl()
//        val fileResponse = ServerApi.downloadService(this@DownloadMessageMediaWorker).getFile(downloadUrl!!)
//        val fileData = fileResponse.bytes()
//        val outputDirectory = Utils.getOutputDirectory(applicationContext)
//        val file = Utils.createFile(outputDirectory, extension = downloadUrl.getExtension())
//        file.writeBytes(fileData)

        return@withContext File("")
    }

//    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
//        val progress = (bytesRead * 100 / contentLength).toInt()
//        val messageId = inputData.getLong(Constants.KEY_MESSAGE_ID, 0)
//        if (messageId != 0L){
//            val state = if (done) DownloadState.DONE else DownloadState.DOWNLOADING
//            Timber.d("update messageId with progress $progress and state $state")
//            downloadsRepository.postProgress(progress, state, messageId)
//        }
//    }
}