package flavours.broadcastReceivers

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.axmor.fsinphone.videomessages.ui.broadcastReceivers.ReceiverWithIntentFilter

/**
 * Класс нужен только для Huawei AppGalery (huaweiAppGalery sourceSet), но используется он в классе
 * общем классе App. Пока не нашёл способа реализовать так, чтобы не создавать дублирующего класса.
 *
 * arogovskikh 26.03.2021
 */
class PushTokenReceiver: ReceiverWithIntentFilter() {

    companion object {
        private const val ACTION_NEW_TOKEN = "com.huawei.codelabpush.ON_NEW_TOKEN"
        private const val KEY_TOKEN = "token"
    }

    override fun onReceive(context: Context, intent: Intent) {

    }

    override fun getFilter(): IntentFilter = IntentFilter().apply {
        addAction(ACTION_NEW_TOKEN)
    }
}