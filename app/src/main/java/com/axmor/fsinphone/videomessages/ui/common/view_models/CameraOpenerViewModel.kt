package com.axmor.fsinphone.videomessages.ui.common.view_models

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.getDefaultSharedPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.math.abs

abstract class CameraOpenerViewModel(application: Application): BaseViewModel(application) {
    private var openCameraJob: Job? = null
    protected val sharedPreferences by lazy { application.getDefaultSharedPreferences() }

    protected fun safelyOpenCamera(loader: MutableLiveData<Boolean>? = loadingLiveData, action: ()-> Unit){
        val closingTime = sharedPreferences.getLong(Constants.KEY_CAMERA_RELEASE_TIMESTAMP, 0L)
        val gap = abs(System.currentTimeMillis() - closingTime)
        Timber.d("safelyOpenCamera: gap is $gap")
        if (gap > Constants.MIN_GAP_TO_REOPEN_CAMERA) {
            action()
        } else {
            if (openCameraJob?.isActive == true) return
            openCameraJob = async(loader, {
                delay(Constants.MIN_GAP_TO_REOPEN_CAMERA - gap)
                action()
            })
        }
    }
}