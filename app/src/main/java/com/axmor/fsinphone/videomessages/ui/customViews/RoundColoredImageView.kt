package com.axmor.fsinphone.videomessages.ui.customViews

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat

class RoundColoredImageView : AppCompatImageView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setColor(@ColorInt color: Int) {
        val shapeDrawable = ShapeDrawable(OvalShape())
        shapeDrawable.paint.color = color
        ViewCompat.setBackground(this, shapeDrawable)
    }
}