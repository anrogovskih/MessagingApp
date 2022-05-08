package com.axmor.fsinphone.videomessages.core.enums

/**
 * 20.10.2020
 * Представляет тип иконки статуса сообщений, используется для отображения UI.
 * Статус DELIVERED пока не используется, заказчик попросил оставить только 2 статуса.
 */
enum class ChatMessageStatusIcon {
    READ, DELIVERED, SENT, NONE
}