package com.axmor.fsinphone.videomessages.ui.common.states

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.common.extensions.observe
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

open class ChatState(
    isActionButtonEnabled: Boolean,
    isActionButtonSend: Boolean,
): StateSwipeRefreshWithErrorScreen() {
    val actionButtonClickObservable: ObservableBoolean = ObservableBoolean()
    val isActionButtonEnabled: ObservableBoolean = ObservableBoolean(isActionButtonEnabled)
    val isActionButtonSend: ObservableBoolean = ObservableBoolean(isActionButtonSend)
    val inputFieldObservable: ObservableField<String> = ObservableField()

    val isLastMessageVisible by lazy { MutableStateFlow(true) }
    val newMessagesCounter by lazy { MutableStateFlow(0) }

    val backButtonClick: ObservableBoolean = ObservableBoolean()
    val headerClick: ObservableBoolean = ObservableBoolean()

    fun observe(viewModel: BaseViewModel) {
        with(viewModel) {
            backButtonClick
                .addOnPropertyChanged { backPressedEvent.value = true }
                .disposeWith(compositeDisposable)

            inputFieldObservable
                .addOnPropertyChanged {
                    val textNoBlank = inputFieldObservable.get()?.isNotBlank() == true
                    isActionButtonEnabled.set(textNoBlank)
                }
                .disposeWith(compositeDisposable)

            isLastMessageVisible.observe(viewModelScope) { isVisible ->
                if (isVisible) newMessagesCounter.value = 0
            }
        }
    }
}