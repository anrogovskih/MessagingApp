package com.axmor.fsinphone.videomessages.ui.customViews.bubble_layout

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.axmor.fsinphone.videomessages.R
import timber.log.Timber

@BindingAdapter("bgColor")
fun setBackgroundColor(view: BubbleLayout, @ColorRes color: Int){
    view.backgroundColor = ContextCompat.getColor(view.context, color)
}

@SuppressLint("RtlHardcoded")
@BindingAdapter("isOutgoing")
fun setBubbleOrientation(view: BubbleLayout, isOutgoing: Boolean){
    view.orientation = if (isOutgoing) BubbleLayout.RIGHT else BubbleLayout.LEFT
    val gravity = if (isOutgoing) Gravity.RIGHT else Gravity.LEFT
    val arrowMargin = view.resources.getDimensionPixelSize(R.dimen.chat_message_arrow_margin)
    val largerHorizontalMargin = view.resources.getDimensionPixelSize(R.dimen.chat_message_larger_horizontal_margin)
    val marginStart = if (isOutgoing) largerHorizontalMargin else arrowMargin
    val marginEnd = if (isOutgoing) arrowMargin else largerHorizontalMargin

    val lp = view.layoutParams
    when (lp) {
        is FrameLayout.LayoutParams -> setBubbleOrientation(lp, gravity, marginStart, marginEnd)
        is LinearLayout.LayoutParams -> setBubbleOrientation(lp, gravity, marginStart, marginEnd)
        else -> Timber.e("unsupported LayoutParams type: ${lp.javaClass.simpleName}")
    }
    view.layoutParams = lp
}

private fun setBubbleOrientation(lp: FrameLayout.LayoutParams, gravity: Int, marginStart: Int, marginEnd: Int){
    lp.gravity = gravity
    setMargins(lp, marginStart, marginEnd)
}

private fun setBubbleOrientation(lp: LinearLayout.LayoutParams, gravity: Int, marginStart: Int, marginEnd: Int){
    lp.gravity = gravity
    setMargins(lp, marginStart, marginEnd)
}

private fun setMargins(lp: ViewGroup.MarginLayoutParams, marginStart: Int, marginEnd: Int){
    lp.marginStart = marginStart
    lp.marginEnd = marginEnd
}