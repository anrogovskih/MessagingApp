package com.axmor.fsinphone.videomessages.core.di

import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntityWithFile
import com.axmor.fsinphone.videomessages.core.db.objects.SupportChatMessageEntity
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.core.mappers.*
import com.axmor.fsinphone.videomessages.ui.common.ChatItemObserver
import com.axmor.fsinphone.videomessages.ui.common.ChatTextBubbleItemObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

/**
 * Объекты созданные через этот модуль привязаны к жизненному циклу ViewModel
 *
 * Краткое описание пробемы с дженериками:
 * https://stackoverflow.com/questions/60320337/dagger2-binds-methods-parameter-type-must-be-assignable-to-the-return-type-wit
 *
 * Из-за этого нужно использовать @JvmSuppressWildcards
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelScopedModule {
    @Binds
    abstract fun bindChatMessageEntityToChatItemMapper(
        mapper: ChatMessageEntityToChatItemMapper
    ): Mapper<ChatMessageEntityWithFile, ChatItem?, ChatItemObserver>

    @Binds
    abstract fun bindChatMessageEntityToChatItemListMapper(
        mapper: ChatMessageEntityToChatItemListMapper
    ): Mapper<@JvmSuppressWildcards List<ChatMessageEntityWithFile>, @JvmSuppressWildcards List<ChatItem>, ChatItemObserver>

    @Binds
    abstract fun bindSupportChatMessageEntityToChatTextBubbleItemListMapper(
        mapper: SupportChatMessageEntityToChatTextBubbleItemListMapper
    ): Mapper<@JvmSuppressWildcards List<SupportChatMessageEntity>, @JvmSuppressWildcards List<ChatTextBubbleItem>, ChatTextBubbleItemObserver>

    @Binds
    abstract fun bindSupportChatMessageEntityToChatTextBubbleItemMapper(
        mapper: SupportChatMessageEntityToChatTextBubbleItemMapper
    ): Mapper<SupportChatMessageEntity, ChatTextBubbleItem, ChatTextBubbleItemObserver>
}