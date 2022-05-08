package com.axmor.fsinphone.videomessages.core.mappers

interface Mapper<From, To, Args> {
    fun map(input: From, args: Args? = null): To
}