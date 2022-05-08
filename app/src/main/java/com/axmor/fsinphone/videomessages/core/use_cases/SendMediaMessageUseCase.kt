package com.axmor.fsinphone.videomessages.core.use_cases

import android.content.Context
import com.axmor.fsinphone.videomessages.core.db.DatabaseManager
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageEntity
import com.axmor.fsinphone.videomessages.core.db.objects.ChatMessageFileEntity
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddMessageResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.random.Random

abstract class SendMediaMessageUseCase {

    suspend fun execute(draftMessage: ChatMessageEntity, deviceId: String, context: Context, file: File? = null, videoDuration: Long? = null) {
        val fileEntity = getFileEntity(draftMessage.id, file, videoDuration)
        val mediaFile = file ?: File(fileEntity.filePath)

        val compressedFile = compressFile(context, draftMessage.contactId, mediaFile)

        val mediaFilePart = getMediaPart(compressedFile)
        val thumbPart = getThumbPart(context, compressedFile)

        val request = getRequest(draftMessage, deviceId, fileEntity)
        val requestPart = Gson().toJson(request).toRequestBody("text/plain".toMediaTypeOrNull())

        val response = AddMessageResponse(Random(System.currentTimeMillis()).nextLong())
        response.checkResponse()

        val copy = draftMessage.copyWithServerId(response.message_id)
        val newFileEntity = ChatMessageFileEntity(copy.id, compressedFile.path)

        DatabaseManager.getDb().chatMessageFileEntityDao().replace(fileEntity, newFileEntity)
        DatabaseManager.getDb().chatMessagesDao().replace(draftMessage, copy)
    }

    private suspend fun getFileEntity(messageId: Long, file: File?, videoDuration: Long?): ChatMessageFileEntity {
        return if (file != null)
            ChatMessageFileEntity(messageId, file.path, videoDuration)
        else
            DatabaseManager.getDb().chatMessageFileEntityDao().getById(messageId)!!
    }

    protected open suspend fun compressFile(context: Context, contactId: Long, file: File): File {
        return file
    }

    protected abstract suspend fun getThumbPart(context: Context, mediaFile: File): MultipartBody.Part

    protected abstract fun getMediaPart(mediaFile: File): MultipartBody.Part

    protected abstract suspend fun getRequest(draftMessage: ChatMessageEntity, deviceId: String, fileEntity: ChatMessageFileEntity): Any
}