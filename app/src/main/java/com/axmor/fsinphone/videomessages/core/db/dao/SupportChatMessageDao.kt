package com.axmor.fsinphone.videomessages.core.db.dao

import androidx.room.*
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface SupportChatMessageDao {
    @Delete
    suspend fun delete(entity: SupportChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SupportChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<SupportChatMessageEntity>)

    @Query("DELETE FROM SupportChatMessageEntity")
    suspend fun clearTable()

    @Transaction
    suspend fun replace(old: SupportChatMessageEntity, newEntity: SupportChatMessageEntity) {
        delete(old)
        insert(newEntity)
    }

    @Query("SELECT * FROM SupportChatMessageEntity ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SupportChatMessageEntity>>

    fun getAllDistinctUntilChanged() = getAll().distinctUntilChanged()

    @Query("SELECT MIN(id) FROM SupportChatMessageEntity")
    suspend fun getMinId(): Long?

    @Query("SELECT * FROM SupportChatMessageEntity WHERE isDraft == 1 AND attemptsToSend < :maxAttempts ORDER BY createdAt ASC")
    suspend fun getAllDraftsToSend(maxAttempts: Int = Constants.MAX_ATTEMPTS_TO_SEND_TEXT): List<SupportChatMessageEntity>
}