package com.axmor.fsinphone.videomessages.common.extensions

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.axmor.fsinphone.videomessages.common.Constants
import java.io.File

fun Intent.toFile(context: Context): File? {
    data?.let { uri ->
        return when (uri.scheme) {
            Constants.SCHEME_CONTENT -> uri.toString().contentUrlToFile(context)
            Constants.SCHEME_FILE -> uri.path?.let { File(it) }
            else -> File(uri.toString())
        }
    }
    return null
}

fun Intent.startActivity(context: Context) = context.startActivity(this)

private fun String.contentUrlToFile(context: Context): File? {
    val cursor: Cursor? = context.contentResolver.query(
        Uri.parse(this),
        arrayOf(MediaStore.Images.ImageColumns.DATA),
        null,
        null,
        null
    )
    cursor?.moveToFirst()
    val filePath = cursor?.getString(0)
    cursor?.close()
    return if (filePath != null) File(filePath) else null
}