package com.axmor.fsinphone.videomessages.common.logging

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Записывает все логи типа Log.WARN в файл на устройстве
 */
class FileLogTree(private val file: File) : Timber.Tree() {

    private val format: SimpleDateFormat = SimpleDateFormat("dd.MM HH:mm:ss", Locale.getDefault())

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.WARN || priority == Log.ERROR) {
            try {
                file.createNewFile()

                if (file.exists()) {
                    val fos = FileOutputStream(file, true)

                    fos.write("${format.format(Date())}: $message\n".toByteArray(Charsets.UTF_8))
                    fos.close()
                }
            } catch (e: IOException) {
                Log.println(Log.ERROR, "FileLogTree", "Error while logging into file: $e")
            }
        }
    }
}