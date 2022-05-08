package flavours.services

import com.axmor.fsinphone.videomessages.core.repos.notifications.NotificationsRepo
import com.axmor.fsinphone.videomessages.core.use_cases.SendPushTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PushService: FirebaseMessagingService() {

    @Inject
    lateinit var notificationsRepo: NotificationsRepo

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("onNewToken $token")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                SendPushTokenUseCase.execute(application, token, SendPushTokenUseCase.ServiceType.FIREBASE)
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.w("onMessageReceived data: ${message.data}")
        GlobalScope.launch(Dispatchers.IO) {
            notificationsRepo.onDataMessageReceived(message.data)
        }
    }
}