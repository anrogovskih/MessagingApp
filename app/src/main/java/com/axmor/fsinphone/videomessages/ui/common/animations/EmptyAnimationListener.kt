package com.axmor.fsinphone.videomessages.ui.common.animations

import android.view.animation.Animation

abstract class EmptyAnimationListener : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation?) {
    }

    override fun onAnimationEnd(animation: Animation?) {
    }

    override fun onAnimationRepeat(animation: Animation?) {
    }
}