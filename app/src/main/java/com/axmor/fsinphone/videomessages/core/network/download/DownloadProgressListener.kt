package com.axmor.fsinphone.videomessages.core.network.download

interface DownloadProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}