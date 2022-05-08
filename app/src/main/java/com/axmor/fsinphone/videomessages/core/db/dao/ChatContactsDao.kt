package com.axmor.fsinphone.videomessages.core.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatContactWithMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface ChatContactsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ChatContactEntity>)

    @Query("DELETE FROM ChatContactEntity")
    suspend fun clearTable()

    @Transaction
    @Query("SELECT * FROM ChatContactEntity")
    suspend fun getChatContactsWithMessage(): List<ChatContactWithMessage>

    @Transaction
    @Query("SELECT * FROM ChatContactEntity")
    fun getChatContactsWithMessageFlow(): Flow<List<ChatContactWithMessage>>

    @Transaction
    @Query("SELECT * FROM ChatContactEntity")
    fun getChatContactsWithMessageLiveData(): LiveData<List<ChatContactWithMessage>>

    @Query("SELECT * FROM ChatContactEntity WHERE id ==:id LIMIT 1")
    fun getChatContactFlow(id: Long): Flow<ChatContactEntity?>

    @Query("SELECT * FROM ChatContactEntity WHERE id ==:id LIMIT 1")
    suspend fun getChatContact(id: Long): ChatContactEntity?

    @Query("SELECT * FROM ChatContactEntity")
    suspend fun getChatContacts(): List<ChatContactEntity>

    @Query("SELECT * FROM ChatContactEntity WHERE id != ${Constants.ID_SUPPORT}")
    fun getPrisonersFlow(): Flow<List<ChatContactEntity>>

    fun getChatContactDistinctUntilChanged(id: Long) = getChatContactFlow(id).distinctUntilChanged()
}