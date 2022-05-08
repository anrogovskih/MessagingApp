package com.axmor.fsinphone.videomessages.ui.screens.main.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.core.entities.IChatContactItem
import com.axmor.fsinphone.videomessages.databinding.ViewChatContactBindingImpl
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter
import com.axmor.fsinphone.videomessages.ui.screens.main.dataClasses.ChatContactItem
import timber.log.Timber

class ChatContactDelegate: UniversalAdapter.UniversalAdapterDelegate<IChatContactItem> {
    override fun getBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding? {
        return DataBindingUtil.inflate(layoutInflater, R.layout.view_chat_contact, parent, false)
    }

    override fun onBindViewHolder(
        baseViewHolder: UniversalAdapter.BaseViewHolder,
        position: Int,
        item: IChatContactItem
    ) {
        val binding = baseViewHolder.binding as ViewChatContactBindingImpl
        binding.item = item
    }

    override fun canWorkWith(item: Any): Boolean = item is IChatContactItem

    override fun getItemId(item: IChatContactItem, position: Int): Long = item.getId()
}