package com.axmor.fsinphone.videomessages.ui.common.binding_adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageButton
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.extensions.toggle
import com.axmor.fsinphone.videomessages.ui.customViews.bubble_layout.BubbleLayout
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.view.BigImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("toggle")
fun toggle(view: View, toggle: ObservableBoolean?) {
    if (toggle != null) {
        view.setOnClickListener { toggle.set(!toggle.get()) }
    }
}

@BindingAdapter("toggle")
fun toggle(view: Toolbar, toggle: ObservableBoolean?) {
    if (toggle != null) {
        view.setNavigationOnClickListener { toggle.set(!toggle.get()) }
    }
}

@BindingAdapter("toggle")
fun toggle(view: View, toggle: MutableStateFlow<Boolean>?) {
    if (toggle != null) {
        view.setOnClickListener { toggle.value = !toggle.value }
    }
}

@BindingAdapter("longClick")
fun onLongClick(view: View, booleanObservable: ObservableBoolean?) {
    if (booleanObservable != null) {
        view.setOnLongClickListener {
            booleanObservable.set(!booleanObservable.get())
            return@setOnLongClickListener true
        }
    }
}

@BindingAdapter("setSelected")
fun setSelected(view: View, selected: Boolean) {
    view.isSelected = selected
}

@BindingAdapter("animation")
fun setAnimation(view: View, animation: Animation?) {
    view.animation = animation
}

@BindingAdapter("preview")
fun setPreview(view: CameraView, preview: Preview) {
    view.preview = preview
}

@BindingAdapter("onRefresh")
fun onRefresh(view: SwipeRefreshLayout, isRefreshing: ObservableBoolean) {
    view.setOnRefreshListener {
        isRefreshing.toggle()
    }
}

@BindingAdapter("onRefresh")
fun onRefresh(view: SwipyRefreshLayout, isRefreshing: ObservableBoolean) {
    view.setOnRefreshListener {
        isRefreshing.toggle()
    }
}

@BindingAdapter(
    value = ["marginStart", "marginTop", "marginEnd", "marginBottom"],
    requireAll = false
)
fun setLayoutMargins(
    view: View,
    marginStart: Float?,
    marginTop: Float?,
    marginEnd: Float?,
    marginBottom: Float?
) {
    if (view.layoutParams is ViewGroup.MarginLayoutParams) {
        val marginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        marginLayoutParams.setMargins(
            marginStart?.toInt() ?: view.marginStart,
            marginTop?.toInt() ?: view.marginTop,
            marginEnd?.toInt() ?: view.marginEnd,
            marginBottom?.toInt() ?: view.marginBottom
        )
    }
}

@BindingAdapter("drawable")
fun drawable(view: ImageButton, drawable: Drawable?) {
    view.setImageDrawable(drawable)
}

@BindingAdapter("filePath")
fun setUri(view: BigImageView, filePath: String?) {
    if (filePath != null) view.showImage(Uri.parse("file://${filePath}"))
}

@BindingAdapter("useExifOrientation")
fun useExifOrientation(view: BigImageView, useExifOrientation: Boolean) {
    if (useExifOrientation) {
        view.setImageLoaderCallback(object : ImageLoader.Callback {
            override fun onCacheHit(imageType: Int, image: File?) {}
            override fun onCacheMiss(imageType: Int, image: File?) {}
            override fun onStart() {}
            override fun onProgress(progress: Int) {}
            override fun onFinish() {}
            override fun onSuccess(image: File?) {
                view.ssiv.orientation = SubsamplingScaleImageView.ORIENTATION_USE_EXIF
            }
            override fun onFail(error: Exception) {}
        })
    }
}

@BindingAdapter("error")
fun error(inputLayout: TextInputLayout, observable: ObservableField<String?>) {
    val error = observable.get()
    inputLayout.error = error
    inputLayout.isErrorEnabled = error != null
}

@BindingAdapter("errorColor")
fun errorColor(inputLayout: TextInputLayout, isError: Boolean) {
    Timber.d("errorColor? $isError")
    val c = inputLayout.context
    val r = c.resources
    if (isError)
        inputLayout.boxStrokeColor = ContextCompat.getColor(c, R.color.colorSalmon)
    else
        inputLayout.setBoxStrokeColorStateList(r.getColorStateList(R.color.selector_edit_text_frame, null))
}

@BindingAdapter("backgroundColor")
fun setBackgroundColor(view: BubbleLayout, @ColorRes color: Int){
    view.backgroundColor = ContextCompat.getColor(view.context, color)
}

@BindingAdapter("isShown")
fun setVisibility(view: FloatingActionButton, isShown: Boolean) {
    if (isShown) view.show() else view.hide()
}

@BindingAdapter("isGone")
fun setGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) View.GONE else View.VISIBLE
}

@BindingAdapter("isInvisible")
fun setInvisible(view: View, isInvisible: Boolean) {
    view.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

class BindingAdapters {
    companion object {
        private val dateFormatters by lazy {
            HashMap<String, SimpleDateFormat>(10)
        }

        @JvmStatic
        fun formatDate(dateFormat: String?, date: Date?): String {
            if (dateFormat == null || date == null) {
                return ""
            }

            var existedFormatter = dateFormatters[formatterKey(dateFormat)]

            if (existedFormatter == null) {
                existedFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
                dateFormatters[formatterKey(dateFormat)] = existedFormatter
            }

            return existedFormatter.format(date)
        }

        private fun formatterKey(dateFormat: String) =
            dateFormat + Locale.getDefault().displayLanguage
    }
}