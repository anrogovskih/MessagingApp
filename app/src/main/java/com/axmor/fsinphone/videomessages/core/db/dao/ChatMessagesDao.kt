package com.axmor.fsinphone.videomessages.core.db.dao

import androidx.room.*
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface ChatMessagesDao {

    @Delete
    suspend fun delete(entity: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ChatMessageEntity>)

    @Query("DELETE FROM ChatMessageEntity")
    suspend fun clearTable()

    @Query("SELECT * FROM ChatMessageEntity WHERE id == :id LIMIT 1")
    fun getById(id: Long): ChatMessageEntity?

    @Query("SELECT * FROM ChatMessageEntity WHERE createdAt == (SELECT MAX(createdAt) FROM ChatMessageEntity WHERE contactId == :contactId AND isOutgoing == 0) LIMIT 1")
    fun getLatestIngoing(contactId: Long): ChatMessageEntity?

    @Query("SELECT * FROM ChatMessageEntity WHERE createdAt == (SELECT MAX(createdAt) FROM ChatMessageEntity WHERE contactId == :contactId) LIMIT 1")
    fun getLatest(contactId: Long): ChatMessageEntity?

    @Query("SELECT * FROM ChatMessageEntity WHERE createdAt <= :createdAt AND isOutgoing == 0 AND isRead == 0")
    fun getUnreadBefore(createdAt: Long): List<ChatMessageEntity>

    @Transaction
    @Query("SELECT * FROM ChatMessageEntity WHERE contactId == :contactId ORDER BY createdAt DESC")
    fun getAll(contactId: Long): Flow<List<ChatMessageEntityWithFile>>

    fun getAllDistinctUntilChanged(contactId: Long) = getAll(contactId).distinctUntilChanged()

    @Query("SELECT COUNT(:contactId) FROM ChatMessageEntity")
    suspend fun getCount(contactId: Long): Int

    @Query("SELECT COUNT() FROM ChatMessageEntity WHERE id < 0")
    suspend fun getDraftsCount(): Int

    @Query("SELECT MIN(id) FROM ChatMessageEntity")
    suspend fun getMinId(): Long?

    @Query("SELECT COUNT(:contactId) FROM ChatMessageEntity WHERE isOutgoing == 0 AND isRead == 0")
    suspend fun getUnreadCount(contactId: Long): Int

    @Transaction
    suspend fun replace(old: ChatMessageEntity, newEntity: ChatMessageEntity) {
        delete(old)
        insert(newEntity)
    }

    @Query("SELECT * FROM ChatMessageEntity WHERE isDraftMessage == 1 AND attemptsToSend < :maxAttempts ORDER BY createdAt ASC")
    suspend fun getAllDraftsToSend(maxAttempts: Int = Constants.MAX_ATTEMPTS_TO_SEND_TEXT): List<ChatMessageEntity>
}