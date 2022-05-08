package flavours.services

import android.content.Context
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.repos.notifications.NotificationsRepo
import com.axmor.fsinphone.videomessages.core.use_cases.SendPushTokenUseCase
import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import flavours.core.PushTokenRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PushService: HmsMessageService() {

    @Inject
    lateinit var notificationsRepo: NotificationsRepo

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendToken(applicationContext, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.w("onMessageReceived data: ${message.data}")

        GlobalScope.launch(Dispatchers.IO) {
            notificationsRepo.onDataMessageReceived(message.dataOfMap)
        }
    }

    private fun sendToken(context: Context, token: String){
        Timber.w("HuaweiMessagingService: sendToken $token")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                PushTokenRetriever.setToken(token, context)
                SendPushTokenUseCase.execute(context, token, SendPushTokenUseCase.ServiceType.HUAWEI)
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}