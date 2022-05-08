package com.axmor.fsinphone.videomessages.ui.screens.edit_contact

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.viewModels
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.Utils
import com.axmor.fsinphone.videomessages.common.extensions.pickImageFromGallery
import com.axmor.fsinphone.videomessages.common.extensions.toFile
import com.axmor.fsinphone.videomessages.databinding.FragmentEditContactBinding
import com.axmor.fsinphone.videomessages.ui.common.BaseFragment
import com.axmor.fsinphone.videomessages.ui.common.Event
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditContactFragment :
    BaseFragment<FragmentEditContactBinding>(R.layout.fragment_edit_contact) {
    private val viewModel by viewModels<EditContactViewModel>()
    private val PICK_IMAGE_REQUEST_CODE = 0

    override fun bindData(binding: FragmentEditContactBinding) {
        binding.viewModel = viewModel
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.pickImage.observe(this) {
            pickImageFromGallery(PICK_IMAGE_REQUEST_CODE)
        }

        viewModel.errorLiveData.observe(this, ::handleErrorEventAsToast)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                PICK_IMAGE_REQUEST_CODE -> {
                    try {
                        val file = data?.toFile(requireContext())
                        viewModel.setAvatar(file!!)
                    } catch (e: Exception) {
                        handleErrorEventAsToast(Event(e), R.string.error_image_load)
                    }
                }
            }
        }
    }

    companion object {
        fun build(contactId: Long): EditContactFragment {
            val args = Bundle()
            args.putLong(Constants.KEY_CONTACT_ID, contactId)
            val fragment = EditContactFragment()
            fragment.arguments = args
            return fragment
        }
    }
}