package com.axmor.fsinphone.videomessages.core.use_cases

import androidx.work.*
import com.axmor.fsinphone.videomessages.core.worker.SendMessagesWorker

object SendMessagesWorkerUseCase {

    fun execute(workManager: WorkManager){
        val request = execute()
        workManager.enqueueUniqueWork(SendMessagesWorker.TAG, ExistingWorkPolicy.REPLACE, request)
    }

    private fun execute(): OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        return OneTimeWorkRequest.Builder(SendMessagesWorker::class.java)
            .addTag(SendMessagesWorker.TAG)
            .setConstraints(constraints)
            .build()
    }
}