package com.axmor.fsinphone.videomessages.core.use_cases

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.axmor.fsinphone.videomessages.R
import com.axmor.fsinphone.videomessages.common.Constants
import com.axmor.fsinphone.videomessages.common.ImagesUtils
import com.axmor.fsinphone.videomessages.common.extensions.getExtensionNoDot
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageFileEntity
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddPhotoMessageRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Exception

object SendPhotoMessageUseCase : SendMediaMessageUseCase() {

    override suspend fun compressFile(context: Context, contactId: Long, file: File): File {
        val contact = DatabaseManager.getDb().chatContactsDao().getChatContact(contactId)
        val maxLength = contact?.chatSettings?.photo?.max_length
            ?: throw Exception(context.getString(R.string.error_download))

        return ImagesUtils.compressToDesiredFileSize(context, file, maxLength) ?: throw Exception(
            context.getString(
                R.string.error_image_compression,
                maxLength / 1024f / 1024
            )
        )
    }

    override suspend fun getThumbPart(context: Context, mediaFile: File): MultipartBody.Part {
        val bitmap = ImagesUtils.fileToBitmap(mediaFile.path)
        val resizedBitmap =
            ImagesUtils.resizeImageProportionally(bitmap, Constants.IMAGE_PREVIEW_SIZE)

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        val requestBody = outputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("thumb", "thumb", requestBody)
    }

    override fun getMediaPart(mediaFile: File): MultipartBody.Part {
        val requestFile = mediaFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", mediaFile.name, requestFile)
    }

    override suspend fun getRequest(
        draftMessage: ChatMessageEntity,
        deviceId: String,
        fileEntity: ChatMessageFileEntity
    ): Any {
        return AddPhotoMessageRequest(
            draftMessage.sender,
            deviceId,
            draftMessage.contactId,
            fileEntity.filePath.getExtensionNoDot()
        ).also {
            it.id = draftMessage.createdAt * 1000
        }
    }
}