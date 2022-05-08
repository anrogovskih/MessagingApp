package com.axmor.fsinphone.videomessages.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.axmor.fsinphone.videomessages.common.extensions.megabytes
import com.axmor.fsinphone.videomessages.core.di.LogFile
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.util.*

@HiltWorker
class LogCleaningWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    @LogFile private val file: File?
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val MAX_LOG_LINES_SIZE = 5000
        //Макс. размер файла логов при котором будут очищаться только старые записи. Делается для того,
        //чтобы не загружать в память слишком большое количество строк
        private const val MAX_FILE_SIZE_TO_CLEAR_PARTIALLY = 50
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            if (file != null && file.exists()) {
                Timber.d("LogCleaningWorker started; file.length = ${file.length().megabytes()} megabytes")

                if (file.length().megabytes() > MAX_FILE_SIZE_TO_CLEAR_PARTIALLY)
                    file.delete()
                else
                    clearPartially(file)
            }
            return@withContext Result.success()
        }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun clearPartially(file: File) = withContext(Dispatchers.IO) {
        val lines = arrayListOf<String>()
        val reader = Scanner(FileInputStream(file), "UTF-8")
        while (reader.hasNextLine())
            lines.add(reader.nextLine())
        reader.close()

        val message = "possiblyClearLogs, lines.size is ${lines.size}"
        if (lines.size > MAX_LOG_LINES_SIZE) {
            val newLines = lines.subList(lines.size - MAX_LOG_LINES_SIZE, lines.lastIndex)
            val writer = BufferedWriter(FileWriter(file, false))
            for (line in newLines) {
                writer.write(line)
                writer.write("\n")
            }
            writer.write(message)
            writer.write("\n")
            writer.flush()
            writer.close()
        } else
            Timber.w(message)
    }
}