package com.axmor.fsinphone.videomessages.ui.screens.support

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.extensions.isLastItemVisible
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatTextBubbleItem
import com.axmor.fsinphone.videomessages.databinding.FragmentChatWithSupportBinding
import com.axmor.fsinphone.videomessages.ui.common.BaseFragment
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import com.axmor.fsinphone.videomessages.ui.screens.chat.delegates.ChatTextBubbleDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupportFragment: BaseFragment<FragmentChatWithSupportBinding>(R.layout.fragment_chat_with_support) {

    private val viewModel by viewModels<SupportViewModel>()
    private val adapter = UniversalAdapter()
    private var resetAnimatorJob: Job? = null
    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                updateUI()
            }
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun bindData(binding: FragmentChatWithSupportBinding) {
        binding.viewModel = viewModel
        binding.apply {
            scrollToEndButtonBinding.scrollToEndButton.setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.apply {
            addDelegate(ChatTextBubbleDelegate())
        }
        viewModel.chatLiveData.observe(this, ::setItems)
        viewModel.errorLiveData.observe(this, ::handleErrorEventAsToast)
        viewModel.showAlertDraftItemOptionsLiveEvent.observe(this, ::showAlertDraftItemOptions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().recyclerView.apply {
            adapter = this@SupportFragment.adapter
        }
    }

    override fun onDestroyView() {
        resetAnimatorJob?.cancel()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        requireBinding().recyclerView.addOnScrollListener(onScrollListener)
        view?.post { updateUI() }
    }

    override fun onPause() {
        super.onPause()
        requireBinding().recyclerView.removeOnScrollListener(onScrollListener)
    }

    private fun updateUI() {
        getBinding()?.apply {
            this@SupportFragment.viewModel.setLastMessageVisible(recyclerView.isLastItemVisible())
        }
    }

    private fun showAlertDraftItemOptions(state: SupportViewModel.AlertDraftItemOptionsState) {
        context?.let {
            AlertDialog.Builder(it)
                .setMessage(R.string.chat_message_upload_error)
                .setPositiveButton(R.string.common_send_again) { _, _ ->
                    viewModel.resend(state.item)
                }
                .setNegativeButton(R.string.common_delete) { _, _ ->
                    viewModel.delete(state.item)
                }
                .show()
        }
    }

    private fun setItems(items: List<ChatTextBubbleItem>) {
        val oldCount = adapter.itemCount
        val newCount = items.size
        //убирает мерцание при обновлении сообщения c новым id
        if (oldCount == newCount)
            requireBinding().recyclerView.itemAnimator = null

        adapter.setItems(items)

        if (oldCount == newCount) {
            resetAnimatorJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(300)
                requireBinding().recyclerView.itemAnimator = DefaultItemAnimator()
            }
        }
    }
}