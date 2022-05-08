package com.axmor.fsinphone.videomessages.ui.screens.splash

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.UserProfile
import com.axmor.fsinphone.videomessages.core.use_cases.AuthUseCase
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import com.axmor.fsinphone.videomessages.ui.common.Event
import kotlinx.coroutines.launch

class SplashViewModel(application: Application) : BaseViewModel(application) {
    val goAuthorizeLiveData by lazy { MutableLiveData<Event<Boolean>>() }
    val goMessagesListLiveData by lazy { MutableLiveData<Event<Boolean>>() }

    init {
        launchApp()
    }

    fun launchApp() {
        scope.launch {
            loadingLiveData.postValue(true)

            try {
                val cachedProfile = UserProfile("some_id", "+79999999999")

                if (cachedProfile == null) {
                    goAuthorizeLiveData.postValue(Event(true))
                } else {
                    AuthUseCase.execute(getApplication(), cachedProfile.phoneNumber)

                    goMessagesListLiveData.postValue(Event(true))
                }
            } catch (ex: Exception) {
                handleException(ex)
            }

            loadingLiveData.postValue(false)
        }
    }
}