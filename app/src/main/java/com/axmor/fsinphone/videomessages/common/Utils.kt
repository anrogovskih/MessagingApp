package com.axmor.fsinphone.videomessages.common

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Point
import android.media.MediaCodecList
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings.Secure
import android.view.WindowManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


object Utils {
    const val MP4 = ".mp4"
    const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
    private const val LOG_FILE_NAME = "log_file.txt"

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
    }

    fun getTempVideoFile(context: Context): File {
        return File(context.filesDir, "temp_video_file.mp4")
    }

    fun getOutputDirectory(context: Context): File {
        val cacheDir = context.externalCacheDir ?: context.cacheDir
        val rootDir = cacheDir!!.absolutePath
        val root = File(rootDir)
        //Create ImageCompressor folder if it doesn't already exists.
        if (!root.exists()) root.mkdirs()
        return cacheDir
    }

    fun createFile(baseFolder: File, extension: String, format: String = FILENAME, prefix: String? = ""): File {
        Timber.d("createFile with extension $extension and format $format")
        val file = File(
            baseFolder,
            prefix + getUniqueFileName(format) + extension
        )
        file.createNewFile()
        return file
    }

    fun createFile(context: Context, extension: String, format: String = FILENAME): File {
        Timber.d("createFile with extension $extension and format $format")
        return createFile(getOutputDirectory(context), extension = extension, format = format)
    }

    fun getUniqueFileName(format: String = FILENAME): String {
        return SimpleDateFormat(format, Locale.US).format(System.currentTimeMillis())
    }

    fun getDurationForFile(context: Context, file: File): Long {
        var time: Long? = 0L
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, Uri.fromFile(file))
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
            retriever.release()
        }
        catch (e: Exception){
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }

        return time ?: getDurationForFile(file)
    }

    fun getDurationForFile(file: File): Long {
        val retriever = MediaMetadataRetriever()

        var inputStream: FileInputStream? = null;
        var time = 0L

        try {
            inputStream = FileInputStream(file.absolutePath);
            retriever.setDataSource(inputStream.fd);
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (duration != null)
                time = duration.toLong()
        } catch (e: FileNotFoundException) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: IOException) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: RuntimeException) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e)
        } finally{
            retriever.release()
            inputStream?.close()
        }
        return time
    }

    fun videoFileToMultipart(file: File): MultipartBody.Part {
        val requestFile = file.asRequestBody("video/mp4".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("videofile", file.name, requestFile)
        return body
    }

    fun bmpToMultipart(bmp: Bitmap): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val requestBody = stream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("thumb", "thumb", requestBody)
        return body
    }

    fun getThumbnail(context: Context, file: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.fromFile(file))
        return retriever.getFrameAtTime(0).also { retriever.release() }
    }

    fun getLogFile(outputDirectory: File): File {
        return File("${outputDirectory.absolutePath}${File.separator}${LOG_FILE_NAME}")
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun printCodecsForMimeType(mimeType: String): String {
        val sb = StringBuilder("\n")
        val mediaCodecList = MediaCodecList(MediaCodecList.REGULAR_CODECS)
        val codecInfos = mediaCodecList.codecInfos
        for (i in codecInfos.indices) {
            val codecInfo = codecInfos[i]
            if (!codecInfo.isEncoder) {
                continue
            }
            val types = codecInfo.supportedTypes
            for (j in types.indices) {
                if (types[j].startsWith(mimeType, ignoreCase = true)) {
                    val supportedTypes = codecInfo.supportedTypes.joinToString()
                    val bitrateRange = codecInfo.getCapabilitiesForType(types[j])?.videoCapabilities?.bitrateRange
                    sb.append("Codec name: ${codecInfo.name}, supportedTypes: ${supportedTypes}, bitrateRange = ${bitrateRange}\n")
                }
            }
        }
        return sb.toString()
    }
}