package com.axmor.fsinphone.videomessages.core.db

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.axmor.fsinphone.videomessages.core.db.dao.*
import com.axmor.fsinphone.videomessages.core.db.objects.*

@Database(
    entities = [
        UserProfile::class,
        Timestamps::class,
        ChatContactEntity::class,
        ChatMessageEntity::class,
        ChatMessageFileEntity::class,
        SupportChatMessageEntity::class
    ],
    version = 14,
    autoMigrations = [
        AutoMigration(from = 12, to = 13, spec = AppDatabase.Migration12To13::class),
        AutoMigration(from = 13, to = 14)
    ]
)

@TypeConverters(CustomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun timestampsDao(): TimestampsDao
    abstract fun chatContactsDao(): ChatContactsDao
    abstract fun chatMessagesDao(): ChatMessagesDao
    abstract fun chatMessageFileEntityDao(): ChatMessageFileEntityDao
    abstract fun supportChatMessageDao(): SupportChatMessageDao

    @DeleteColumn(tableName = "ChatContactEntity", columnName = "isAvatarDefault")
    @DeleteTable(tableName = "VideoMessageEntity")
    class Migration12To13 : AutoMigrationSpec
}