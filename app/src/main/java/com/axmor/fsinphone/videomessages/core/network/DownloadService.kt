package com.axmor.fsinphone.videomessages.core.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadService {
    @Streaming
    @GET
    suspend fun getFile(@Url url: String): ResponseBody
}