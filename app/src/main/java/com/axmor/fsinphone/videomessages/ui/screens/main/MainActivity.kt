package com.axmor.fsinphone.videomessages.ui.screens.main

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.viewModels
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.databinding.ActivityMainBinding
import com.axmor.fsinphone.videomessages.ui.BackPressHandler
import com.axmor.fsinphone.videomessages.ui.Navigator
import com.axmor.fsinphone.videomessages.ui.common.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main), BackPressHandler {
    private val viewModel: MainViewModel by viewModels()
    private var isBackPressLocked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel

        addInitialFragments()

        // While the user is in the app, the volume controls should adjust the music volume.
        volumeControlStream = AudioManager.STREAM_MUSIC
        Timber.d("test 1")
    }

    override fun setBackPressLocked(isLocked: Boolean) {
        isBackPressLocked = isLocked
    }

    override fun onBackPressed() {
        try {
            if (!isBackPressLocked)
                super.onBackPressed()
            else
                viewModel.setLockedBackPressClicked()
        }
        catch (e: IllegalArgumentException){
            e.printStackTrace()
        }
    }

    override fun backTo(tag: String) {
        supportFragmentManager.popBackStackImmediate(tag, 0)
    }

    private fun addInitialFragments() {
        intent?.extras?.apply {
            when(Action.valueOf(getString(ACTION, Action.NONE.name))){
                Action.GO_TO_CHAT -> goToChat(getLong(Constants.KEY_CONTACT_ID, -1))
                else -> {
                    //do nothing
                }
            }
        }

    }

    private fun goToChat(contactId: Long){
        if (contactId >= 0)
            Navigator.goChat(this, contactId)
        else if (contactId == Constants.ID_SUPPORT)
            Navigator.goSupport(this)
    }

    companion object {
        private const val ACTION = "ACTION"

        fun getIntent(context: Context) = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        fun getIntentShowChat(context: Context, contactId: Long) = getIntent(context).apply {
                putExtra(Constants.KEY_CONTACT_ID, contactId)
                putExtra(ACTION, Action.GO_TO_CHAT.name)
            }
    }

    enum class Action {
        GO_TO_CHAT,

        NONE
    }
}