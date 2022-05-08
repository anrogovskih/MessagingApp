package com.axmor.fsinphone.videomessages.ui.screens.contact_details

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.common.state_handling.SingleLiveEvent
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepo
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

@HiltViewModel
class ContactDetailsViewModel @Inject constructor(
    application: Application,
    private val chatContactsRepository: ChatContactsRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(application) {

    private val contactId = savedStateHandle.get<Long>(Constants.KEY_CONTACT_ID)!!
    val stateLiveData by lazy { MutableLiveData(State()) }
    val contactLiveData by lazy { setContact() }
    val goEditContact by lazy { SingleLiveEvent<Long>() }

    data class State(
        val buttonBackClickObservable: ObservableBoolean = ObservableBoolean(),
        val buttonEditClickObservable: ObservableBoolean = ObservableBoolean()
    )

    init {
        stateLiveData.value?.observe()
    }

    private fun setContact(): LiveData<ChatContactEntity> {
        return chatContactsRepository.getContactFlow(contactId).filterNotNull().asLiveData()
    }

    private fun State.observe() {
        buttonBackClickObservable
            .addOnPropertyChanged { backPressedEvent.value = true }
            .disposeWith(compositeDisposable)
        buttonEditClickObservable
            .addOnPropertyChanged { goEditContact.value = contactId }
            .disposeWith(compositeDisposable)
    }
}