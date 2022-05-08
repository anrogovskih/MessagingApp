package com.axmor.fsinphone.videomessages.core.di

import android.content.Context
import androidx.annotation.Nullable
import androidx.work.WorkManager
import com.axmor.fsinphone.videomessages.common.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationScopedProviderModule {

    @DeviceId
    @Provides
    @Singleton
    fun provideDeviceId(@ApplicationContext context: Context): String {
        return Utils.getDeviceId(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @LogFile
    @Provides
    fun provideLogFile(@ApplicationContext context: Context): File {
        val dir = context.getExternalFilesDir(null) ?: context.filesDir
        return Utils.getLogFile(dir)
    }
}