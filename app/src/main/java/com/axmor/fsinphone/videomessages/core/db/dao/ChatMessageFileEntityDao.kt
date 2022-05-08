package com.axmor.fsinphone.videomessages.core.db.dao

import androidx.room.*
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageFileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
interface ChatMessageFileEntityDao {

    @Delete
    suspend fun delete(entity: ChatMessageFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatMessageFileEntity)

    @Query("SELECT * FROM ChatMessageFileEntity WHERE messageId == :id LIMIT 1")
    suspend fun getById(id: Long): ChatMessageFileEntity?

    @Query("SELECT * FROM ChatMessageFileEntity WHERE messageId == :id LIMIT 1")
    fun getFlowById(id: Long): Flow<ChatMessageFileEntity?>

    @Transaction
    suspend fun replace(old: ChatMessageFileEntity, newEntity: ChatMessageFileEntity){
        delete(old)
        insert(newEntity)
    }
}