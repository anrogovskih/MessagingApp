package com.axmor.fsinphone.videomessages.common.extensions

import android.net.Uri
import android.webkit.URLUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.getExtension(): String = ".${getExtensionNoDot()}"

fun String.getExtensionNoDot(): String {
    val uri = Uri.parse(this)
    val lastSegment = uri.lastPathSegment
    return lastSegment?.split(".")?.last()?:""
}

fun String.isValidUrl() = URLUtil.isValidUrl(this)