package com.axmor.fsinphone.videomessages.ui.screens.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.extensions.addOnPropertyChanged
import com.axmor.fsinphone.videomessages.common.extensions.disposeWith
import com.axmor.fsinphone.videomessages.databinding.FragmentMainBinding
import com.axmor.fsinphone.videomessages.ui.Navigator
import com.axmor.fsinphone.videomessages.ui.common.BaseFragment
import com.axmor.fsinphone.videomessages.ui.common.view_models.BaseViewModel
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter
import com.axmor.fsinphone.videomessages.ui.screens.main.delegates.ChatContactDelegate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels(ownerProducer = ::requireActivity)
    private val adapter = UniversalAdapter().apply {
        addDelegate(ChatContactDelegate())
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun bindData(binding: FragmentMainBinding) {
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.stateLiveData.observe(this) { state ->
            state.settingsClick
                .addOnPropertyChanged { Navigator.goSettings(requireActivity()) }
                .disposeWith(onDestroy)
        }
        viewModel.itemsLiveData.observe(this, adapter::setItems)
        viewModel.errorLiveData.observe(this, ::handleErrorEventAsToast)
        viewModel.toChatLiveEvent.observe(this, ::navigateToChatWith)
        viewModel.toSupportChatLiveEvent.observe(this) { Navigator.goSupport(requireActivity()) }
        viewModel.toReceiverSelection.observe(this) { Navigator.goSelectReceiver(requireActivity()) }
        viewModel.toBalanceRefill.observe(this) { Navigator.goRefillBalance(requireActivity(), it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().recyclerView.apply {
            adapter = this@MainFragment.adapter
            itemAnimator = null
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

    private fun navigateToChatWith(contactId: Long) = Navigator.goChat(requireActivity(), contactId)
}