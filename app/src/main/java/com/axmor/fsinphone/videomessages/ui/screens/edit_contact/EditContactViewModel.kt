package com.axmor.fsinphone.videomessages.ui.screens.edit_contact

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.axmor.fsinphone.videomessages.common.*
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.common.state_handling.SingleLiveEvent
import com.axmor.fsinphone.videomessages.core.entities.NewAvatar
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepo
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class EditContactViewModel @Inject constructor(
    application: Application,
    private val chatContactsRepo: ChatContactsRepo,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(application) {
    private val contactId = savedStateHandle.get<Long>(Constants.KEY_CONTACT_ID)!!
    private val contactFlow = chatContactsRepo.getContactFlow(contactId).asStateFlow(null)
    private val oldDefaultAvatar =
        contactFlow.map { it?.getDefaultAvatarOrNull() }.asStateFlow(null)

    val stateLiveData by lazy { MutableLiveData(State()) }
    val pickImage by lazy { SingleLiveEvent<Boolean>() }

    data class State(
        val editedName: ObservableField<String> = ObservableField(),
        val editedAvatar: ObservableField<String> = ObservableField(),
        val isDefaultAvatar: ObservableBoolean = ObservableBoolean(),
        val buttonBackClickObservable: ObservableBoolean = ObservableBoolean(),
        val buttonEditAvatarClickObservable: ObservableBoolean = ObservableBoolean(),
        val buttonClearAvatarClickObservable: ObservableBoolean = ObservableBoolean(),
        val buttonSaveClickObservable: ObservableBoolean = ObservableBoolean()
    )

    init {
        initFields()
        stateLiveData.value?.observe()
    }

    fun setAvatar(avatar: File) {
        async(loadingLiveData, {
            val bitmap = ImagesUtils.getExifRotatedBitmap(avatar.path)
            val resizedBitmap = ImagesUtils.resizeImageProportionally(bitmap, Constants.AVATAR_SIZE)
            val avatarBase64 = ImagesUtils.bitmapToBase64(resizedBitmap, 50)

            stateLiveData.value?.apply {
                editedAvatar.set(avatarBase64)
                isDefaultAvatar.set(false)
            }
        })
    }

    private fun initFields() {
        stateLiveData.value?.apply {
            async(loadingLiveData, {
                contactFlow.collect { contact ->

                    if (contact != null) {
                        editedName.set(contact.name)
                        editedAvatar.set(contact.image)
                        isDefaultAvatar.set(contact.isDefaultAvatar())
                        coroutineContext.cancel()
                    }
                }
            })
        }
    }

    private fun State.observe() {
        buttonBackClickObservable
            .addOnPropertyChanged { backPressedEvent.value = true }
            .disposeWith(compositeDisposable)

        buttonSaveClickObservable
            .addOnPropertyChanged { saveContact() }
            .disposeWith(compositeDisposable)

        buttonEditAvatarClickObservable
            .addOnPropertyChanged { pickImage.value = true }
            .disposeWith(compositeDisposable)

        buttonClearAvatarClickObservable
            .addOnPropertyChanged { clearAvatar() }
            .disposeWith(compositeDisposable)
    }

    private fun State.clearAvatar() {
        contactFlow.value?.apply {
            editedAvatar.set(oldDefaultAvatar.value ?: getDefaultAvatarBase64())
            isDefaultAvatar.set(true)
        }
    }

    private fun saveContact() {
        val state = stateLiveData.value ?: return
        val newName = state.editedName.get()?.trim()

        if (!newName.isNullOrBlank()) {
            val contact = contactFlow.value ?: return
            val editedAvatar = state.editedAvatar.get() ?: return
            val newAvatar = NewAvatar(editedAvatar, state.isDefaultAvatar.get())

            async(loadingLiveData,
                asyncJob = { chatContactsRepo.editContact(contact, newName, newAvatar) },
                syncJob = { backPressedEvent.postValue(true) }
            )
        }
    }
}
