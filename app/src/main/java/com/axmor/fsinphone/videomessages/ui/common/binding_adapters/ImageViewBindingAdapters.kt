package com.axmor.fsinphone.videomessages.ui.common.binding_adapters

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.ImagesUtils
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageWithImageItem
import com.axmor.fsinphone.videomessages.core.enums.ChatMessageStatusIcon
import com.axmor.fsinphone.videomessages.core.enums.ChatWarningIcon
import com.bumptech.glide.Glide

@BindingAdapter("loadUrl")
fun loadUrl(view: ImageView, url: String) {
    Glide.with(view).load(url).into(view)
}

@BindingAdapter("imageUri")
fun loadImage(view: ImageView, uri: String) {
    if (URLUtil.isNetworkUrl(uri))
        ImagesUtils.loadUrl(uri, view)
    else
        ImagesUtils.loadLocalFile(Uri.parse(uri), view)
}

@BindingAdapter(value = ["imageUri", "item"], requireAll = true)
fun loadChatMessagePreview(view: ImageView, uri: String, item: ChatMessageWithImageItem) {
    val foregroundIcon = when (item.getCentralIcon()) {
        ChatMessageWithImageItem.Icon.PLAY -> R.drawable.ic_play
        else -> -1
    }
    val onSuccessAction: (Bitmap) -> Unit = if (foregroundIcon != -1) {
        { resource: Bitmap ->
            ImagesUtils.drawForegroundIcon(
                resource,
                foregroundIcon,
                view.context
            )
        }
    } else {
        {}
    }

    if (URLUtil.isNetworkUrl(uri))
        ImagesUtils.loadUrl(
            uri,
            view,
            onSuccess = onSuccessAction,
        )
    else
        ImagesUtils.loadLocalFile(
            Uri.parse(uri),
            view,
            onSuccess = onSuccessAction,
        )
}

@BindingAdapter("stringRes")
fun stringRes(view: ImageView, @StringRes stringRes: Int) {
    view.contentDescription = view.resources.getString(stringRes)
}

@BindingAdapter("icon")
fun setIcon(view: ImageView, icon: ChatWarningIcon) {
    view.visibility = if (icon != ChatWarningIcon.NONE) View.VISIBLE else View.GONE
    when (icon) {
        ChatWarningIcon.ALERT -> view.setImageResource(R.drawable.ic_error)
        ChatWarningIcon.WATCH_LATER -> view.setImageResource(R.drawable.ic_watch_later)
        else -> {
        }
    }
}

@BindingAdapter("status")
fun setStatus(view: ImageView, status: ChatMessageStatusIcon) {
    view.visibility = if (status != ChatMessageStatusIcon.NONE) View.VISIBLE else View.GONE
    when (status) {
        ChatMessageStatusIcon.SENT -> {
            view.setImageResource(R.drawable.ic_done)
            val color = ContextCompat.getColor(view.context, R.color.colorDarkGreen)
            view.imageTintList = ColorStateList.valueOf(color)
        }
        ChatMessageStatusIcon.DELIVERED -> {
            view.setImageResource(R.drawable.ic_done_double)
            val color = ContextCompat.getColor(view.context, R.color.colorGray3)
            view.imageTintList = ColorStateList.valueOf(color)
        }
        ChatMessageStatusIcon.READ -> {
            view.setImageResource(R.drawable.ic_done_double)
            val color = ContextCompat.getColor(view.context, R.color.colorDarkGreen)
            view.imageTintList = ColorStateList.valueOf(color)
        }
        else -> {
        }
    }
}

@BindingAdapter(value = ["urlOrBase64", "onErrorAction"], requireAll = false)
fun setAvatarImage(view: ImageView, urlOrBase64: String?, onErrorAction: (() -> Unit)?) {
//    Timber.d("setAvatarImage $urlOrBase64")
    val onError = onErrorAction ?: {}
    when {
        URLUtil.isValidUrl(urlOrBase64) || urlOrBase64 == null -> {
            ImagesUtils.loadUrl(urlOrBase64, view, onError)
        }
        else -> {
            ImagesUtils.loadImageFromBase64(view, urlOrBase64)
        }
    }
}