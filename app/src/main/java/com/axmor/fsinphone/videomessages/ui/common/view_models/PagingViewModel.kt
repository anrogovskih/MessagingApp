package com.axmor.fsinphone.videomessages.ui.common.view_models

import androidx.lifecycle.LiveData

interface PagingViewModel {
    val pagingResetEventLiveData: LiveData<Boolean>
    val totalLiveData: LiveData<Int?>

    fun loadMore()
}