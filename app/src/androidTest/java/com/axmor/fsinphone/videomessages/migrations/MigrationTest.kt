package com.axmor.fsinphone.videomessages.migrations

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.axmor.fsinphone.videomessages.core.db.AppDatabase
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager.MIGRATION_3_12
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageFileEntity
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import timber.log.Timber
import java.io.IOException

class MigrationTest {
    private val TEST_DB = "migration-test"
    private val ENTITY_1 = ChatMessageFileEntity(1, "testPath1")
    private val ENTITY_2 = ChatMessageFileEntity(2, "testPath2")
    private lateinit var testDatabase: SupportSQLiteDatabase

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Before
    fun createDatabase() {
        testDatabase = helper.createDatabase(TEST_DB, 3)
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To12() {
        Timber.d("Proceeding Migrations Test...")

        testDatabase.apply {
            execSQL("INSERT INTO VideoMessageEntity (messageId, filePath) VALUES (${ENTITY_1.messageId}, ${ENTITY_1.filePath})")
            execSQL("INSERT INTO VideoMessageEntity (messageId, filePath) VALUES (${ENTITY_2.messageId}, ${ENTITY_2.filePath})")
            close()
        }

        helper.runMigrationsAndValidate(TEST_DB, 12, true, MIGRATION_3_12)

        val migratedDb = getMigratedRoomDatabase()

        val entity1 =
            runBlocking { migratedDb.chatMessageFileEntityDao().getById(ENTITY_1.messageId) }!!
        assertEquals(entity1.filePath, ENTITY_1.filePath)

        val entity2 =
            runBlocking { migratedDb.chatMessageFileEntityDao().getById(ENTITY_2.messageId) }!!
        assertEquals(entity2.filePath, ENTITY_2.filePath)
    }

    @After
    fun clearDatabase() {
        testDatabase.execSQL("DROP TABLE IF EXISTS $TEST_DB")
        testDatabase.close()
    }

    private fun getMigratedRoomDatabase(): AppDatabase {
        return Room
            .databaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java,
                TEST_DB
            )
            .addMigrations(MIGRATION_3_12)
            .build()
            .apply {
                openHelper.writableDatabase
                close()
            }
    }
}