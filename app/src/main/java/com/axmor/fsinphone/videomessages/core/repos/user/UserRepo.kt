package com.axmor.fsinphone.videomessages.core.repos.user

import kotlinx.coroutines.flow.Flow

interface UserRepo {
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
    suspend fun getDraftMessagesCount(): Int
}