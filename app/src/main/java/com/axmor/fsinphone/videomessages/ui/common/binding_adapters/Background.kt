package com.axmor.fsinphone.videomessages.ui.common.binding_adapters

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.axmor.fsinphone.videomessages.common.extensions.setBackgroundColorRes

sealed class Background

class BackgroundColor(@ColorInt val color: Int): Background()

class BackgroundColorRes(@ColorRes val color: Int): Background()

class BackgroundDrawable(val drawable: Drawable): Background()

class BackgroundDrawableRes(@DrawableRes val drawable: Int): Background()

@BindingAdapter("background")
fun setBackground(view: View, background: Background) {
    when (background) {
        is BackgroundColor -> view.setBackgroundColor(background.color)
        is BackgroundColorRes -> view.setBackgroundColorRes(background.color)
        is BackgroundDrawable -> view.background = background.drawable
        is BackgroundDrawableRes -> view.setBackgroundResource(background.drawable)
    }
}