package com.axmor.fsinphone.videomessages.common.extensions

//converts from bytes
fun Long.kilobytes(): Long {
    return this / 1024
}

fun Long.megabytes(): Long {
    return this.kilobytes() / 1024
}

fun Long.gigabytes(): Long {
    return this.megabytes() / 1024
}

//converts to bytes
fun Int.kilobytes(): Int {
    return this * 1024
}

fun Int.megabytes(): Int {
    return this.kilobytes() * 1024
}

fun Int.gigabytes(): Int {
    return this.megabytes() * 1024
}