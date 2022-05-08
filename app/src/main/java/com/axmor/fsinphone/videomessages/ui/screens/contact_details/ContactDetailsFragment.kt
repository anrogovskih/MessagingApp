package com.axmor.fsinphone.videomessages.ui.screens.contact_details

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.databinding.FragmentContactDetailsBinding
import com.axmor.fsinphone.videomessages.ui.Navigator
import com.axmor.fsinphone.videomessages.ui.common.BaseFragment
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDetailsFragment :
    BaseFragment<FragmentContactDetailsBinding>(R.layout.fragment_contact_details) {
    private val viewModel by viewModels<ContactDetailsViewModel>()

    override fun bindData(binding: FragmentContactDetailsBinding) {
        binding.viewModel = viewModel
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.goEditContact.observe(this, {
            Navigator.goEditContact(requireActivity(), it)
        })
    }

    companion object {
        fun build(contactId: Long): ContactDetailsFragment {
            val args = Bundle()
            args.putLong(Constants.KEY_CONTACT_ID, contactId)
            val fragment = ContactDetailsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}