package com.axmor.fsinphone.videomessages.ui.common.states

import androidx.databinding.ObservableBoolean

open class StateSwipeRefreshWithErrorScreen {
    val isErrorScreenShown: ObservableBoolean = ObservableBoolean(false)
    val isNetworkError: ObservableBoolean = ObservableBoolean(false)
    val isRefreshing: ObservableBoolean = ObservableBoolean()
    val updateClickObservable: ObservableBoolean = ObservableBoolean()
}