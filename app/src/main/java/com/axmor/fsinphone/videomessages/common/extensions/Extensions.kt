package com.axmor.fsinphone.videomessages.common.extensions

import java.net.ConnectException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import android.accounts.NetworkErrorException
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun Exception?.isNetworkException(): Boolean {
    return this is ConnectException || this is NetworkErrorException || this is UnknownHostException || this is SSLException
}
