package com.axmor.fsinphone.videomessages.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

fun <T> Flow<T>.observe(scope: CoroutineScope, observer: suspend (T) -> Unit) {
    scope.launch {
        collect(observer)
    }
}

suspend inline fun <T> Flow<T>.collectOnce(crossinline action: suspend (value: T) -> Unit) {
    collect {
        action(it)
        coroutineContext.cancel()
    }
}