package com.axmor.fsinphone.videomessages.core.di

import com.axmor.fsinphone.videomessages.core.repos.chat.ChatRepo
import com.axmor.fsinphone.videomessages.core.repos.chat.ChatRepoImpl
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepo
import com.axmor.fsinphone.videomessages.core.repos.chatContacts.ChatContactsRepoImpl
import com.axmor.fsinphone.videomessages.core.repos.faq.FaqRepo
import com.axmor.fsinphone.videomessages.core.repos.faq.FaqRepoImpl
import com.axmor.fsinphone.videomessages.core.repos.support.SupportRepo
import com.axmor.fsinphone.videomessages.core.repos.support.SupportRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

/**
 * Объекты созданные через этот модуль привязаны к жизненному циклу Activity
 *
 * Все методы должны иметь аннотацию @ActivityRetainedScoped
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedScopedModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindChatContactsRepository(repository: ChatContactsRepoImpl): ChatContactsRepo

    @Binds
    @ActivityRetainedScoped
    abstract fun bindChatRepository(repository: ChatRepoImpl): ChatRepo

    @Binds
    @ActivityRetainedScoped
    abstract fun bindFaqRepository(repository: FaqRepoImpl): FaqRepo

    @Binds
    @ActivityRetainedScoped
    abstract fun bindSupportRepository(repository: SupportRepoImpl): SupportRepo
}