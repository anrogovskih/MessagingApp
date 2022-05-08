package com.axmor.fsinphone.videomessages.ui.screens.chat.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.databinding.ViewChatTextMessageBinding
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter

class ChatTextBubbleDelegate: UniversalAdapter.UniversalAdapterDelegate<ChatTextBubbleItem> {

    override fun getBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding? {
        return DataBindingUtil.inflate(layoutInflater, R.layout.view_chat_text_message, parent, false)
    }

    override fun onBindViewHolder(
        baseViewHolder: UniversalAdapter.BaseViewHolder,
        position: Int,
        item: ChatTextBubbleItem
    ) {
        val binding = baseViewHolder.binding as ViewChatTextMessageBinding
        binding.item = item
    }

    override fun canWorkWith(item: Any): Boolean = item is ChatTextBubbleItem

    override fun getItemId(item: ChatTextBubbleItem, position: Int): Long = item.getId()
}