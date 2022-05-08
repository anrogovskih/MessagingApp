package com.axmor.fsinphone.videomessages.core.db.objects

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Timestamps(
    @PrimaryKey
    val id: Int = 1,
    var codeRequestTimestamp: Long = 0
)