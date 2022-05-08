package com.axmor.fsinphone.videomessages.common.extensions

import java.util.*

fun Calendar.isSameDayAs(calendar: Calendar?): Boolean{
    return calendar != null &&
            calendar.get(Calendar.DAY_OF_MONTH) == get(Calendar.DAY_OF_MONTH) &&
            calendar.get(Calendar.MONTH) == get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == get(Calendar.YEAR)
}

fun Calendar.isToday(): Boolean {
    val now = Calendar.getInstance()
    return isSameDayAs(now)
}

fun Calendar.isYesterday(): Boolean {
    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_MONTH, -1)
    }
    return isSameDayAs(yesterday)
}

fun Calendar.isCurrentYear(): Boolean {
    val now = Calendar.getInstance()
    return now.get(Calendar.YEAR) == get(Calendar.YEAR)
}