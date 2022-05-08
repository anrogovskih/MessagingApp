package com.axmor.fsinphone.videomessages.core.db

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseManager {
    private var database: AppDatabase? = null

    fun init(context: Context) {
        if (database == null) {
            val applicationContext = context.applicationContext

            database = Room
                .databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "fsin_videomessages_database"
                )
                .addMigrations(MIGRATION_3_12)
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    fun getDb(): AppDatabase {
        if (database == null) {
            throw RuntimeException("Call init() first")
        } else {
            return database!!
        }
    }

    val MIGRATION_3_12 = object : Migration(3, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TEMPORARY TABLE UserProfileBackup(`id` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("INSERT INTO UserProfileBackup (id, phoneNumber) SELECT id, phoneNumber FROM UserProfile")
            database.execSQL("DROP TABLE UserProfile")
            // Sql из схемы 12
            database.execSQL("CREATE TABLE IF NOT EXISTS `UserProfile` (`id` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `ChatContactEntity` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `object_title` TEXT, `last_activity` TEXT, `last_message_date` INTEGER NOT NULL, `new_messages` INTEGER NOT NULL, `image` TEXT, `isAvatarDefault` INTEGER NOT NULL, `lastMessageId` INTEGER, `chatSettings` TEXT, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `ChatMessageEntity` (`id` INTEGER NOT NULL, `isOutgoing` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `isPaid` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `status` INTEGER, `sender_id` TEXT NOT NULL, `sender` TEXT NOT NULL, `receiver` TEXT NOT NULL, `prisonName` TEXT NOT NULL, `type` TEXT NOT NULL, `contactId` INTEGER NOT NULL, `isDraftMessage` INTEGER NOT NULL, `attemptsToSend` INTEGER NOT NULL, `image_size` REAL, `image_file` TEXT, `image_thumb` TEXT, `text_text` TEXT, `video_size` REAL, `video_file` TEXT, `video_duration` TEXT, `video_thumb` TEXT, PRIMARY KEY(`id`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `ChatMessageFileEntity` (`messageId` INTEGER NOT NULL, `filePath` TEXT NOT NULL, `videoDuration` INTEGER, PRIMARY KEY(`messageId`))")
            database.execSQL("CREATE TABLE IF NOT EXISTS `SupportChatMessageEntity` (`id` INTEGER NOT NULL, `text` TEXT NOT NULL, `isRead` INTEGER NOT NULL, `isOutgoing` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `isDraft` INTEGER NOT NULL, `attemptsToSend` INTEGER NOT NULL, PRIMARY KEY(`id`))")
            // Перенос данных
            database.execSQL("INSERT INTO UserProfile (id, phoneNumber) SELECT id, phoneNumber FROM UserProfileBackup")
            database.execSQL("DROP TABLE UserProfileBackup")
            database.execSQL("INSERT INTO ChatMessageFileEntity (messageId, filePath) SELECT id, filePath FROM VideoMessageEntity")
            database.execSQL("DELETE FROM VideoMessageEntity")
        }
    }
}