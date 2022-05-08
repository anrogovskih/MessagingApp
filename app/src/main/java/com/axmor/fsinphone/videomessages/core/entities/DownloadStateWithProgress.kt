package com.axmor.fsinphone.videomessages.core.entities

import com.axmor.fsinphone.videomessages.core.enums.DownloadState

data class DownloadStateWithProgress(val progress: Int, val state: DownloadState)