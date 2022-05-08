package com.axmor.fsinphone.videomessages.ui.screens.splash

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.extensions.checkAuthError
import com.axmor.fsinphone.videomessages.common.extensions.isNetworkException
import com.axmor.fsinphone.videomessages.databinding.ActivitySplashBinding
import com.axmor.fsinphone.videomessages.ui.Navigator
import com.axmor.fsinphone.videomessages.ui.common.BaseActivity
import com.axmor.fsinphone.videomessages.ui.common.Event

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.goAuthorizeLiveData.observe(this, Observer<Event<Boolean>?> { event ->
            event?.getContentIfNotHandled()?.let {
                Navigator.goEnterPhone(this@SplashActivity)
            }
        })

        viewModel.goMessagesListLiveData.observe(this, Observer<Event<Boolean>?> { event ->
            event?.getContentIfNotHandled()?.let {
                Navigator.goMainActivity(this@SplashActivity)
            }
        })

        viewModel.errorLiveData.observe(this, Observer<Event<Exception>?> { event ->
            event?.getContentIfNotHandled()?.let {
                if (checkAuthError(it))
                    return@let

                if (it.isNetworkException())
                    showNoNetworkAlert(cancelable = false, hasExit = true) {
                        viewModel.launchApp()
                    }
                else{
                    showErrorAlert(it.localizedMessage, negativeAction = {
                        Navigator.goEnterPhone(this@SplashActivity)
                    })
                }
            }
        })
    }
}