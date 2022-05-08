package com.axmor.fsinphone.videomessages.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axmor.fsinphone.videomessages.core.db.objects.Timestamps

@Dao
interface TimestampsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timestamps: Timestamps)

    @Query("DELETE FROM Timestamps")
    suspend fun clearTable()

    @Query("SELECT * FROM Timestamps LIMIT 1")
    suspend fun getTimestamps(): Timestamps?
}