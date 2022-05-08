package com.axmor.fsinphone.videomessages.ui.customViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.makeramen.roundedimageview.RoundedImageView

class AvatarView : RoundedImageView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    init {
        borderWidth = 2f
        borderColor = Color.WHITE
        isOval = true
        scaleType = ScaleType.CENTER_CROP
    }
}