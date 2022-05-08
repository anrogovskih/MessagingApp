package com.axmor.fsinphone.videomessages.core.di

import com.axmor.fsinphone.videomessages.core.repos.download.DownloadsRepo
import com.axmor.fsinphone.videomessages.core.repos.download.DownloadsRepoImpl
import com.axmor.fsinphone.videomessages.core.repos.notifications.NotificationsRepo
import com.axmor.fsinphone.videomessages.core.repos.notifications.NotificationsRepoImpl
import com.axmor.fsinphone.videomessages.core.repos.user.UserRepo
import com.axmor.fsinphone.videomessages.core.repos.user.UserRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Объекты созданные через этот модуль привязаны к жизненному циклу Application
 *
 * Все методы должны иметь аннотацию @Singleton
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationScopedModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(repo: UserRepoImpl): UserRepo

    @Binds
    @Singleton
    abstract fun bindDownloadsRepo(repo: DownloadsRepoImpl): DownloadsRepo

    @Binds
    @Singleton
    abstract fun bindNotificationsRepo(repo: NotificationsRepoImpl): NotificationsRepo
}