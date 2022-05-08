package flavours.broadcastReceivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.axmor.fsinphone.videomessages.core.use_cases.SendPushTokenUseCase
import com.axmor.fsinphone.videomessages.ui.broadcastReceivers.ReceiverWithIntentFilter
import flavours.core.PushTokenRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class PushTokenReceiver: ReceiverWithIntentFilter() {

    companion object {
        private const val ACTION_NEW_TOKEN = "com.huawei.codelabpush.ON_NEW_TOKEN"
        private const val KEY_TOKEN = "token"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_NEW_TOKEN){
            val token = intent.getStringExtra(KEY_TOKEN) ?: return
            sendToken(context, token)
        }
    }

    private fun sendToken(context: Context, token: String){
        Timber.w("HuaweiPushTokenReceiver: sendToken $token")
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

    override fun getFilter(): IntentFilter = IntentFilter().apply {
        addAction(ACTION_NEW_TOKEN)
    }
}