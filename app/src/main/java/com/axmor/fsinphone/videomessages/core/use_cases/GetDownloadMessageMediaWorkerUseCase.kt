package com.axmor.fsinphone.videomessages.core.use_cases

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.workDataOf
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.worker.DownloadMessageMediaWorker

object GetDownloadMessageMediaWorkerUseCase {

    fun execute(messageId: Long): OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data = workDataOf(Pair(Constants.KEY_MESSAGE_ID, messageId))

        return OneTimeWorkRequest.Builder(DownloadMessageMediaWorker::class.java)
            .addTag(DownloadMessageMediaWorker.TAG)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
    }
}