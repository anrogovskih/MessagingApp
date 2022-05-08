package com.axmor.fsinphone.videomessages.common.extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.ui.common.animations.EmptyAnimationListener
import timber.log.Timber

fun View.setBackgroundColorRes(@ColorRes color: Int) {
    val colorInt = ContextCompat.getColor(context, color)
    setBackgroundColor(colorInt)
}

fun View.showFromBottom() {
    cancelCurrentAnimation()
    val animation = AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom)
    animation.fillAfter = true
    startAnimation(animation)
    //AnimationListener иногда не отрабатывает по неизвестной причине, поэтому устанавливать
    //visibility сразу после старта анимации надежнее
    visibility = View.VISIBLE
}

fun View.hideToBottom() {
    cancelCurrentAnimation()
    val animation = AnimationUtils.loadAnimation(context, R.anim.exit_to_bottom)
    animation.setAnimationListener(object : EmptyAnimationListener() {
        override fun onAnimationEnd(animation: Animation?) {
            visibility = View.GONE
        }
    })
    startAnimation(animation)
}

fun RecyclerView.isLastItemVisible(): Boolean {
    return adapter?.itemCount == 0
            || (layoutManager as? LinearLayoutManager)?.findFirstCompletelyVisibleItemPosition() in RecyclerView.NO_POSITION..0
}

private fun View.cancelCurrentAnimation() {
    if (animation != null && !animation.hasEnded())
        animation.cancel()
}