package com.axmor.fsinphone.videomessages.core.network

import com.axmor.fsinphone.videomessages.core.network.objects.DeleteMessageRequest
import com.axmor.fsinphone.videomessages.core.network.objects.PushTokenRequest
import com.axmor.fsinphone.videomessages.core.network.objects.ReadMessageRequest
import com.axmor.fsinphone.videomessages.core.network.objects.UnifiedResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddMessageResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_add_message.AddTextMessageRequest
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_contacts.ChatGetContactsRequest
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_contacts.ChatGetContactsResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_messages.GetMessagesRequest
import com.axmor.fsinphone.videomessages.core.network.objects.chat.chat_get_messages.GetMessagesResponse
import com.axmor.fsinphone.videomessages.core.network.objects.chat.edit_contact.ChatEditContactRequest
import com.axmor.fsinphone.videomessages.core.network.objects.code.CodeRequest
import com.axmor.fsinphone.videomessages.core.network.objects.code.CodeResponse
import com.axmor.fsinphone.videomessages.core.network.objects.faq.GetFaqRequest
import com.axmor.fsinphone.videomessages.core.network.objects.faq.GetFaqResponse
import com.axmor.fsinphone.videomessages.core.network.objects.get_settings.GetSettingsRequest
import com.axmor.fsinphone.videomessages.core.network.objects.get_settings.GetSettingsResponse
import com.axmor.fsinphone.videomessages.core.network.objects.support.SendMessageToSupportRequest
import com.axmor.fsinphone.videomessages.core.network.objects.support.SupportChatRequest
import com.axmor.fsinphone.videomessages.core.network.objects.support.SupportChatResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MainApi {
    /**
     * Авторизация в приложении
     */
    @POST(".")
    suspend fun getSettings(@Body request: GetSettingsRequest): GetSettingsResponse

    @POST(".")
    suspend fun requestCode(@Body request: CodeRequest): CodeResponse

    @POST(".")
    suspend fun sendPushToken(@Body request: PushTokenRequest): UnifiedResponse

    /**
     * Запросы, связанные с чатом
     */
    @POST(".")
    suspend fun getChatContacts(@Body request: ChatGetContactsRequest): ChatGetContactsResponse

    @POST(".")
    suspend fun chatEditContact(@Body request: ChatEditContactRequest): UnifiedResponse

    @POST(".")
    suspend fun getMessages(@Body request: GetMessagesRequest): GetMessagesResponse

    @POST(".")
    suspend fun sendTextMessage(@Body request: AddTextMessageRequest): AddMessageResponse

    @Multipart
    @POST("upload/")
    suspend fun sendMediaMessage(
        @Part("body") body: RequestBody,
        @Part filePart: MultipartBody.Part,
        @Part thumbPart: MultipartBody.Part
    ): AddMessageResponse

    @POST(".")
    suspend fun readMessage(@Body request: ReadMessageRequest): UnifiedResponse

    @POST(".")
    suspend fun getSupportChat(@Body request: SupportChatRequest): SupportChatResponse

    @POST(".")
    suspend fun sendMessageToSupport(@Body request: SendMessageToSupportRequest): AddMessageResponse

    @POST(".")
    suspend fun deleteMessage(@Body request: DeleteMessageRequest): UnifiedResponse
    
    /**
     * FAQ
     */
    @POST(".")
    suspend fun getFaq(@Body request: GetFaqRequest): GetFaqResponse
}