package com.axmor.fsinphone.videomessages.ui.customViews

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.axmor.fsinphone.videomessages.R
import com.google.android.material.badge.BadgeDrawable


class BadgeView : View {

    private val badgeDrawable = BadgeDrawable.create(context)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView)
        badgeDrawable.number = typedArray.getInteger(R.styleable.BadgeView_number, 1)
        badgeDrawable.backgroundColor = typedArray.getColor(
            R.styleable.BadgeView_backgroundColor,
            ContextCompat.getColor(context, R.color.colorAccent)
        )
        typedArray.recycle()

        badgeDrawable.isVisible = true
        badgeDrawable.updateBadgeCoordinates(this, null)

        updateBadge()
    }

    fun setNumber(number: Int) {
        badgeDrawable.number = number
        invalidate()
        updateBadge()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(badgeDrawable.intrinsicWidth, badgeDrawable.intrinsicHeight)
    }

    private fun updateBadge() {
        post {
            overlay.remove(badgeDrawable)
            val badgeBounds = Rect()
            getDrawingRect(badgeBounds)
            badgeDrawable.bounds = badgeBounds
            val inset = resources.getDimensionPixelSize(R.dimen.mtrl_badge_text_horizontal_edge_offset)
            badgeDrawable.horizontalOffset = badgeBounds.right - inset
            badgeDrawable.verticalOffset = badgeBounds.bottom / 2
            badgeDrawable.updateBadgeCoordinates(this, null)
            overlay.add(badgeDrawable)
        }
    }
}