package com.axmor.fsinphone.videomessages.ui

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.ui.common.animations.FragmentTransactionAnimation
import com.axmor.fsinphone.videomessages.ui.common.animations.LeftToRightTransition
import com.axmor.fsinphone.videomessages.ui.screens.chat.ChatFragment
import com.axmor.fsinphone.videomessages.ui.screens.contact_details.ContactDetailsFragment
import com.axmor.fsinphone.videomessages.ui.screens.edit_contact.EditContactFragment
import com.axmor.fsinphone.videomessages.ui.screens.main.MainActivity
import com.axmor.fsinphone.videomessages.ui.screens.support.SupportFragment
import timber.log.Timber
import java.io.File

object Navigator {

    fun goEnterPhone(activity: Activity) {
    }

    fun goEnterCode(activity: FragmentActivity) {

    }

    fun goCreateVideoMessage(activity: FragmentActivity, contactId: Long, videoMaxLength: Int) {

    }

    fun goCreatePhotoMessage(activity: FragmentActivity, contactId: Long) {
    }

    fun goPreviewMessage(activity: FragmentActivity, contactId: Long, file: File, duration: Long) {

    }

    fun goPreviewPhotoMessage(activity: FragmentActivity, contactId: Long, filePath: String) {

    }

    fun goMainActivity(activity: Activity) =
        activity.startActivity(MainActivity.getIntent(activity))

    fun goSettings(activity: FragmentActivity) {
    }

    fun goFaq(activity: FragmentActivity) {
    }

    fun goSupport(activity: FragmentActivity) {
        activity.replace(SupportFragment(), LeftToRightTransition)
    }

    fun goChat(activity: FragmentActivity, contactId: Long) {
        val fragment = ChatFragment.build(contactId)
        activity.replace(fragment, LeftToRightTransition)
    }

    fun goContactDetails(activity: FragmentActivity, contactId: Long) {
        val fragment = ContactDetailsFragment.build(contactId)
        activity.replace(fragment)
    }

    fun goEditContact(activity: FragmentActivity, contactId: Long) {
        val fragment = EditContactFragment.build(contactId)
        activity.replace(fragment)
    }

    fun goViewPhotoMessage(activity: FragmentActivity, messageId: Long) {
    }

    fun goViewVideoMessage(activity: FragmentActivity, messageId: Long) {
    }

    fun goSelectReceiver(activity: FragmentActivity) {
    }

    fun goRefillBalance(activity: FragmentActivity, contactId: Long) {

    }

    fun goToPayment(activity: FragmentActivity) {
    }

    private fun FragmentActivity.replace(
        fragment: Fragment,
        animation: FragmentTransactionAnimation
    ) {
        val tag = fragment::class.java.name
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                animation.enter,
                animation.exit,
                animation.popEnter,
                animation.popExit
            )
            .addToBackStack(tag)
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun FragmentActivity.replace(
        fragment: Fragment,
        transition: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE
    ) {
        val tag = fragment::class.java.name
        supportFragmentManager.beginTransaction()
            .setTransition(transition)
            .addToBackStack(tag)
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }
}