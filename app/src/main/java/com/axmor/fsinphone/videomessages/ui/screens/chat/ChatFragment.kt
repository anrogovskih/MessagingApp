package com.axmor.fsinphone.videomessages.ui.screens.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.extensions.isLastItemVisible
import com.axmor.fsinphone.videomessages.common.extensions.pickImageFromGallery
import com.axmor.fsinphone.videomessages.common.extensions.toFile
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatItem
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageItem
import com.axmor.fsinphone.videomessages.databinding.FragmentChatBinding
import com.axmor.fsinphone.videomessages.ui.Navigator
import com.axmor.fsinphone.videomessages.ui.common.BaseFragment
import com.axmor.fsinphone.videomessages.ui.common.Event
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import com.axmor.fsinphone.videomessages.ui.listeners.EndlessRecyclerOnScrollListener
import com.axmor.fsinphone.videomessages.ui.screens.chat.delegates.ChatMessageWithImageDelegate
import com.axmor.fsinphone.videomessages.ui.screens.chat.delegates.ChatTextBubbleDelegate
import com.axmor.fsinphone.videomessages.ui.screens.chat.dialogs.AttachmentsBottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(R.layout.fragment_chat),
    AttachmentsBottomSheetDialog.Parent {
    private val PICK_IMAGE_REQUEST_CODE = 1

    private val viewModel by viewModels<ChatViewModel>()
    private val adapter = UniversalAdapter()
    private var onScrollListener: EndlessRecyclerOnScrollListener? = null
    private var resetAnimatorJob: Job? = null

    override fun getViewModel(): BaseViewModel = viewModel

    override fun bindData(binding: FragmentChatBinding) {
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
            addDelegate(ChatMessageWithImageDelegate())
        }
        viewModel.chatLiveData.observe(this, ::setItems)
        viewModel.errorLiveData.observe(this, ::handleErrorEventAsToast)
        viewModel.pagingResetEventLiveData.observe(this) { onScrollListener?.reset() }
        viewModel.totalLiveData.observe(this) { onScrollListener?.setTotal(it) }
        viewModel.showAlertDraftItemOptionsLiveEvent.observe(this, ::showAlertDraftItemOptions)
        viewModel.showAlertDeleteMessage.observe(this, ::showAlertDeleteMessage)
        viewModel.showOptionsDialogLiveEvent.observe(this) { showAttachmentsDialog() }
        viewModel.toContactDetailsLiveEvent.observe(this) {
            Navigator.goContactDetails(requireActivity(), it)
        }
        viewModel.toViewPhotoMessage.observe(this) {
            Navigator.goViewPhotoMessage(requireActivity(), it)
        }
        viewModel.toViewVideoMessage.observe(this) {
            Navigator.goViewVideoMessage(requireActivity(), it)
        }
        viewModel.takePhotoEvent.observe(this) {
            Navigator.goCreatePhotoMessage(requireActivity(), it)
        }
        viewModel.takeVideoEvent.observe(this) {
            val contactId = it.first
            val maxLength = it.second
            Navigator.goCreateVideoMessage(requireActivity(), contactId, maxLength)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().recyclerView.apply {
            adapter = this@ChatFragment.adapter

            val layoutManager = layoutManager
            if (layoutManager is LinearLayoutManager)
                onScrollListener = initScrollListener(layoutManager)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.readAll()
    }

    override fun onResume() {
        super.onResume()
        onScrollListener?.let { requireBinding().recyclerView.addOnScrollListener(it) }
        view?.post {
            updateUI()
        }
    }

    override fun onPause() {
        super.onPause()
        onScrollListener?.let { requireBinding().recyclerView.removeOnScrollListener(it) }
    }

    override fun onDestroyView() {
        resetAnimatorJob?.cancel()
        super.onDestroyView()
    }

    override fun getPermissions(): Map<String, Boolean?> {
        val settings = viewModel.contactLiveData.value?.chatSettings
        return mapOf(
            Constants.KEY_PHOTO_MESSAGES_ALLOWED to settings?.photo?.is_allowed,
            Constants.KEY_VIDEO_MESSAGES_ALLOWED to settings?.video?.is_allowed
        )
    }

    override fun takePhoto() = viewModel.takePhoto()

    override fun takeVideo() = viewModel.takeVideo()

    override fun pickFromGallery() = pickImageFromGallery(PICK_IMAGE_REQUEST_CODE)

    private fun setItems(items: List<ChatItem>) {
        val oldCount = adapter.itemCount
        val newCount = items.size
        //убирает мерцание при обновлении сообщения c новым id
        if (oldCount == newCount)
            requireBinding().recyclerView.itemAnimator = null

        adapter.setItems(items)

        //предполагается, что триггером должно выступить новое добавленное входящее сообщение
        if (items.firstOrNull()?.requireReading() == true)
            viewModel.readAll()

        if (oldCount == newCount) {
            resetAnimatorJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(300)
                requireBinding().recyclerView.itemAnimator = DefaultItemAnimator()
            }
        }
    }

    private fun initScrollListener(manager: LinearLayoutManager): EndlessRecyclerOnScrollListener {
        return object : EndlessRecyclerOnScrollListener(manager) {
            override fun onLoadMore() {
                viewModel.loadMore()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        getBinding()?.apply {
            this@ChatFragment.viewModel.setLastMessageVisible(recyclerView.isLastItemVisible())
        }
    }

    private fun showAlertDraftItemOptions(state: ChatViewModel.AlertDraftItemOptionsState) {
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

    private fun showAlertDeleteMessage(message: ChatMessageItem) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.messages_list_delete_title)
                .setMessage(R.string.messages_list_delete_message)
                .setPositiveButton(R.string.common_delete) { _, _ ->
                    viewModel.delete(message.message)
                }
                .setNegativeButton(R.string.common_cancel) { _, _ -> }
                .setOnDismissListener {
                    message.isSelected.set(false)
                }
                .show()
        }
    }

    private fun showAttachmentsDialog() {
        val dialog = AttachmentsBottomSheetDialog()
        dialog.show(childFragmentManager, AttachmentsBottomSheetDialog::class.java.simpleName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                PICK_IMAGE_REQUEST_CODE -> {
                    try {
                        val file = data?.toFile(requireContext())
                        viewModel.sendPhotoMessage(file!!)
                    } catch (e: Exception) {
                        handleErrorEventAsToast(Event(e), R.string.error_image_load)
                    }
                }
            }
        }
    }

    companion object {
        fun build(contactId: Long): ChatFragment {
            val args = Bundle()
            args.putLong(Constants.KEY_CONTACT_ID, contactId)
            val fragment = ChatFragment()
            fragment.arguments = args
            return fragment
        }
    }
}