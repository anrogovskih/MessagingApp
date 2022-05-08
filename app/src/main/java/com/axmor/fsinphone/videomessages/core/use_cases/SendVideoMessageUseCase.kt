package com.axmor.fsinphone.videomessages.core.use_cases

import android.content.Context
import com.axmor.fsinphone.videomessages.common.Utils
import com.axmor.fsinphone.videomessages.common.extensions.getExtension
import com.axmor.fsinphone.videomessages.common.extensions.getExtensionNoDot
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageFileEntity
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddVideoMessageRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

object SendVideoMessageUseCase: SendMediaMessageUseCase() {

    override suspend fun getThumbPart(context: Context, mediaFile: File): MultipartBody.Part {
        val thumb = Utils.getThumbnail(context, mediaFile)!!
        return Utils.bmpToMultipart(thumb)
    }

    override fun getMediaPart(mediaFile: File): MultipartBody.Part {
        val requestFile = mediaFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("videofile", mediaFile.name, requestFile)
    }

    override suspend fun getRequest(
        draftMessage: ChatMessageEntity,
        deviceId: String,
        fileEntity: ChatMessageFileEntity
    ): Any {
        val mediaFile = File(fileEntity.filePath)
        return AddVideoMessageRequest(
            draftMessage.sender,
            deviceId,
            draftMessage.contactId,
            TimeUnit.MILLISECONDS.toSeconds(fileEntity.videoDuration ?: 0),
            mediaFile.path.getExtensionNoDot()
        ).also {
            it.id = draftMessage.createdAt * 1000
            Timber.w("SendVideoMessageUseCase: extension is ${it.file_extension}, sending duration as ${it.duration}")
        }
    }
}