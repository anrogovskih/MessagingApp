package com.axmor.fsinphone.videomessages.core.repos.user

import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepoImpl @Inject constructor() : UserRepo {
    override suspend fun logout() {
        clearDb()
    }

    override suspend fun getDraftMessagesCount() = DatabaseManager.getDb().chatMessagesDao().getDraftsCount()

    override suspend fun isLoggedIn(): Boolean = DatabaseManager.getDb().userProfileDao().getProfile() != null

    private suspend fun clearDb() {
        DatabaseManager.getDb().userProfileDao().clearTable()
        DatabaseManager.getDb().chatMessagesDao().clearTable()
        DatabaseManager.getDb().chatContactsDao().clearTable()
        DatabaseManager.getDb().supportChatMessageDao().clearTable()
        DatabaseManager.getDb().timestampsDao().clearTable()
    }
}