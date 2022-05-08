package com.axmor.fsinphone.videomessages.ui.common.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.axmor.fsinphone.videomessages.common.state_handling.SingleLiveEvent
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.exceptions.DeviceTokenError
import com.axmor.fsinphone.videomessages.ui.common.Event
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val scope = CoroutineScope(Dispatchers.IO)

    val backPressedEvent by lazy { SingleLiveEvent<Boolean>() }
    val loadingLiveData by lazy { MutableLiveData<Boolean>() }
    val errorLiveData by lazy { MutableLiveData<Event<Exception>>() }
    val logoutEvent by lazy { SingleLiveEvent<Boolean>() }

    val compositeDisposable = CompositeDisposable()

    protected open suspend fun handleException(ex: Exception) {
        ex.printStackTrace()
        Timber.w(ex.stackTraceToString())

        if (ex is DeviceTokenError)
            DatabaseManager.getDb().userProfileDao().clearTable()

        if (ex !is CancellationException)
            errorLiveData.postValue(Event(ex))
    }

    /**
     * Just a helper function for asynchronous operations, used to reduce boilerplate code.
     *
     * @param loadingLiveData - live data, that is responsible for handling progress bar visibility.
     *  Can be null in case where we don't want to show user that something is loading.
     * @param asyncJob - action to put inside try-catch block
     * @param syncJob - action, that will happen anyway, even if error had occurred
     * @param onError - action to put inside catch block
     */
    protected fun async(
        loadingLiveData: MutableLiveData<Boolean>?,
        asyncJob: suspend () -> Unit,
        syncJob: () -> Unit = {},
        onError: suspend (e: Exception) -> Unit = ::handleException
    ): Job {

        return scope.launch {
            loadingLiveData?.postValue(true)
            try {
                asyncJob.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                onError.invoke(e)
            }
            loadingLiveData?.postValue(false)
            syncJob.invoke()
        }
    }

    protected fun <T> Flow<T>.asStateFlow(initialValue: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.Eagerly, initialValue)

    override fun onCleared() {
        scope.coroutineContext.cancel()
        scope.cancel()
        compositeDisposable.dispose()
        super.onCleared()
        Timber.d("onCleared ${javaClass.simpleName}")
    }
}