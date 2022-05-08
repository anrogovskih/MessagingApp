package com.axmor.fsinphone.videomessages.ui.screens.chat.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageWithImageItem
import com.axmor.fsinphone.videomessages.databinding.ViewChatMessageWithImagePreviewBinding
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter

class ChatMessageWithImageDelegate: UniversalAdapter.UniversalAdapterDelegate<ChatMessageWithImageItem> {
    override fun getBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding? {
        return DataBindingUtil.inflate(layoutInflater, R.layout.view_chat_message_with_image_preview, parent, false)
    }

    override fun onBindViewHolder(
        baseViewHolder: UniversalAdapter.BaseViewHolder,
        position: Int,
        item: ChatMessageWithImageItem
    ) {
        val binding = baseViewHolder.binding as ViewChatMessageWithImagePreviewBinding
        binding.item = item
    }

    override fun canWorkWith(item: Any): Boolean = item is ChatMessageWithImageItem

    override fun getItemId(item: ChatMessageWithImageItem, position: Int): Long = item.getId()
}