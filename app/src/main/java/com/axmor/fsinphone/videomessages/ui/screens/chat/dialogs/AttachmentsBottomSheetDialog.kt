package com.axmor.fsinphone.videomessages.ui.screens.chat.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.databinding.DialogChatActionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AttachmentsBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogChatActionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_chat_actions, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val permissions = getParent()?.getPermissions()

        super.onViewCreated(view, savedInstanceState)
        binding.chatActionCancel.setOnClickListener { dismiss() }
        binding.chatActionPickImage.visibility =
            if (permissions?.get(Constants.KEY_PHOTO_MESSAGES_ALLOWED) == true) View.VISIBLE else View.GONE
        binding.chatActionPickImage.setOnClickListener {
            dismiss()
            getParent()?.pickFromGallery()
        }
        binding.chatActionTakePhoto.visibility =
            if (permissions?.get(Constants.KEY_PHOTO_MESSAGES_ALLOWED) == true) View.VISIBLE else View.GONE
        binding.chatActionTakePhoto.setOnClickListener {
            dismiss()
            getParent()?.takePhoto()
        }
        binding.chatActionVideoMessage.visibility =
            if (permissions?.get(Constants.KEY_VIDEO_MESSAGES_ALLOWED) == true) View.VISIBLE else View.GONE
        binding.chatActionVideoMessage.setOnClickListener {
            dismiss()
            getParent()?.takeVideo()
        }
    }

    private fun getParent(): Parent? = parentFragment as? Parent

    interface Parent {
        fun getPermissions(): Map<String, Boolean?>
        fun takePhoto()
        fun takeVideo()
        fun pickFromGallery()
    }
}