package flavours.core

import android.content.Context
import com.axmor.fsinphone.videomessages.common.Constants.TAG_PUSH_TOKEN
import com.axmor.fsinphone.videomessages.common.extensions.getDefaultSharedPreferences
import com.axmor.fsinphone.videomessages.core.use_cases.SendPushTokenUseCase

/**
 * Пуши Huawei не работают для неподписанных приложений, поэтому проверять надо на релизной версии.
 */
object PushTokenRetriever {

    suspend fun getToken(context: Context): Pair<SendPushTokenUseCase.ServiceType, String?> {
        val prefs = context.getDefaultSharedPreferences()
        val token = prefs.getString(TAG_PUSH_TOKEN, null)
        return Pair(SendPushTokenUseCase.ServiceType.HUAWEI, token)
    }

    suspend fun setToken(token: String, context: Context) {
        val prefs = context.getDefaultSharedPreferences()
        prefs.edit().putString(TAG_PUSH_TOKEN, token).apply()
    }
}