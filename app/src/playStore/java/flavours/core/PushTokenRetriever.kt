@file:Suppress("BlockingMethodInNonBlockingContext")

package flavours.core

import android.content.Context
import com.axmor.fsinphone.videomessages.core.use_cases.SendPushTokenUseCase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PushTokenRetriever {

    suspend fun getToken(context: Context): Pair<SendPushTokenUseCase.ServiceType, String?> = withContext(Dispatchers.IO){
        val token = Tasks.await(FirebaseMessaging.getInstance().token)
        return@withContext Pair(SendPushTokenUseCase.ServiceType.FIREBASE, token)
    }
}