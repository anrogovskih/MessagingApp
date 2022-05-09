package com.axmor.fsinphone.videomessages.core.network

import com.axmor.fsinphone.videomessages.BuildConfig
import com.axmor.fsinphone.videomessages.core.network.download.DownloadProgressListener
import com.axmor.fsinphone.videomessages.core.network.download.DownloadProgressResponseBody
import com.axmor.fsinphone.videomessages.core.preferences.AppPreferences
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object ServerApi {

    private const val REQUEST_TIMEOUT: Long = 30000

    private const val DEFAULT_TIMEZONE = "Europe/Moscow"
    private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    private var downloadListener: DownloadProgressListener? = null
    private lateinit var headerInterceptor: HeaderInterceptor

    val api by lazy {
        getApi(MainApi::class.java, client, BuildConfig.SERVER_BASE_URL)
    }

    private val downloadService by lazy {
        getApi(DownloadService::class.java, downloadClient, BuildConfig.SERVER_BASE_URL)
    }

    fun init(preferences: AppPreferences) {
        headerInterceptor = HeaderInterceptor(preferences)
    }

    fun downloadService(listener: DownloadProgressListener): DownloadService {
        downloadListener = listener
        return downloadService
    }

    fun getDefaultDateFormat() = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE)
    }

    private val httpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    private val downloadInterceptor: Interceptor = Interceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        originalResponse.newBuilder()
            .body(DownloadProgressResponseBody(originalResponse.body!!, downloadListener))
            .build()
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerInterceptor)
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

    private val downloadClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(downloadInterceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private fun <T> getApi(classType: Class<T>, client: OkHttpClient, baseUrl: String): T {
        return Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(baseUrl)
            .build()
            .create(classType)
    }
}