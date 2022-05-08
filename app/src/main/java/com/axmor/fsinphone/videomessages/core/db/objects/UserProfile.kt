package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.axmor.fsinphone.videomessages.core.network.objects.get_settings.GetSettingsResponse

@Entity
data class UserProfile(
    @PrimaryKey
    val id: String,
    val phoneNumber: String,
)

fun GetSettingsResponse.toUserProfile(phoneNumber: String): UserProfile {
    return UserProfile(user_id, phoneNumber)
}