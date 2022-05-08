package com.axmor.fsinphone.videomessages.core.use_cases

import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.di.DeviceId
import com.axmor.fsinphone.videomessages.core.entities.NewAvatar
import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.edit_contact.ChatEditContactRequest
import javax.inject.Inject

class EditContactUseCase @Inject constructor(
    @DeviceId private val deviceId: String
) {

    suspend fun execute(contact: ChatContactEntity, newName: String, newAvatar: NewAvatar) {
        val name = if (newName != contact.name) newName else null
        val avatarBase64 = when {
            newAvatar.imageBase64 == contact.image -> null
            newAvatar.isDefault -> Constants.NO_AVATAR
            else -> newAvatar.imageBase64
        }

        if (name != null || avatarBase64 != null) {
            editOnServer(contact.id, name, avatarBase64).checkResponse()
            editInDatabase(name, contact, newAvatar)
        }
    }

    private suspend fun editOnServer(
        contactId: Long,
        name: String?,
        image: String?
    ): UnifiedResponse {
        val cachedProfile = DatabaseManager.getDb().userProfileDao().getProfile()!!
        val request =
            ChatEditContactRequest(
                cachedProfile.phoneNumber,
                deviceId,
                contactId,
                cachedProfile.id,
                name,
                image
            )
        return UnifiedResponse()
    }

    private suspend fun editInDatabase(
        name: String?,
        contact: ChatContactEntity,
        newAvatar: NewAvatar
    ) {
        val newContact = contact.copy(
            name = name ?: contact.name,
            image = newAvatar.imageBase64,
            isAvatarUploaded = newAvatar.isDefault.not()
        )
        DatabaseManager.getDb().chatContactsDao().insert(newContact)
    }
}