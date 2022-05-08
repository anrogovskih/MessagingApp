package com.axmor.fsinphone.videomessages.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.axmor.fsinphone.videomessages.core.db.objects.UserProfile

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: UserProfile)

    @Query("DELETE FROM UserProfile")
    suspend fun clearTable()

    @Query("SELECT * FROM UserProfile LIMIT 1")
    suspend fun getProfile(): UserProfile?
}