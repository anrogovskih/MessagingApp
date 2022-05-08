package com.axmor.fsinphone.videomessages.common

object Constants {
    //id пуш-нотификаций
    const val FAILURE_NOTIFICATION_ID = 102
    const val NEW_MESSAGE_NOTIFICATION_ID = 103
    const val COMMON_MESSAGE_NOTIFICATION_ID = 104

    //характеристики устройства заключенного
    const val LOW_QUALITY_WIDTH = 480
    const val LOW_QUALITY_HEIGHT = 640
    const val HD720P_WIDTH = 720
    const val HD720P_HEIGHT = 1280
    const val HD1080P_WIDTH = 1080
    const val HD1080P_HEIGHT = 1920

    const val PRISONER_DEVICE_WIDTH = HD1080P_WIDTH
    const val PRISONER_DEVICE_HEIGHT = HD1080P_HEIGHT

    const val SUPPORT_EMAIL = "itcsfsinet@gmail.com"

    const val SECONDS_TO_CODE_REQUEST = 1 * 60
    const val PHONES_MASK_RU = "([000]) [000]-[00]-[00]"
    const val PHONES_HINT_RU = "(XXX) XXX-XX-XX"
    const val PHONES_MASK_INTERNATIONAL = "[00009999999999]"
    const val PHONES_CODE_MASK = "+[0999]"
    const val PHONES_CODE_RU = "+7"
    const val AMOUNT_MASK = "[19999] ₽"

    const val ID_SUPPORT: Long = 0
    const val MAX_ATTEMPTS_TO_SEND_TEXT = 3
    const val FIRST_ID_FOR_DRAFT = -1L
    const val NO_AVATAR = ""

    // Keys and tags
    const val KEY_CONTACT_ID = "KEY_CONTACT_ID"
    const val KEY_CONTACT_VIDEO_MAX_LENGTH = "KEY_CONTACT_VIDEO_MAX_LENGTH"
    const val KEY_MESSAGE_ID = "KEY_MESSAGE_ID"
    const val TAG_CAMERA_PREVIEW = "CAMERA_PREVIEW"
    const val TAG_CAMERA_WIDTH = "TAG_CAMERA_WIDTH"
    const val TAG_CAMERA_HEIGHT = "TAG_CAMERA_HEIGHT"
    const val TAG_IS_VIDEO_SNAPSHOT = "TAG_IS_VIDEO_SNAPSHOT"
    const val KEY_FILE_PATH = "KEY_FILE_PATH"
    const val KEY_DURATION = "KEY_DURATION"
    const val KEY_PHOTO_MESSAGES_ALLOWED = "KEY_PHOTO_MESSAGES_ALLOWED"
    const val KEY_VIDEO_MESSAGES_ALLOWED = "KEY_VIDEO_MESSAGES_ALLOWED"
    const val KEY_ENTER_CODE_PARAM = "KEY_ENTER_CODE_PARAM"
    const val KEY_CAMERA_RELEASE_TIMESTAMP = "KEY_CAMERA_RELEASE_TIMESTAMP"
    const val TAG_PUSH_TOKEN = "PUSH_TOKEN"
    const val KEY_TYPE = "type"

    const val CONTACT_DETAILS_LAST_ACTIVITY_DATE_FORMAT = "d MMMM"
    const val CHAT_DATE_FORMAT = "dd MMM"
    const val CHAT_DATE_FORMAT_WITH_YEAR = "dd MMM yyyy"

    // Camera
    const val MIN_GAP_TO_REOPEN_CAMERA = 2000L

    // Аватар
    const val AVATAR_SIZE = 300
    const val IMAGE_PREVIEW_SIZE = 380

    //schemes
    const val SCHEME_FILE = "file"
    const val SCHEME_CONTENT = "content"

    //video calls
    const val CALL_RESPONSE_ACTION_KEY = "CALL_RESPONSE_ACTION_KEY"
    const val CALL_RECEIVE_ACTION = "CALL_RECEIVE_ACTION"
    const val CALL_CANCEL_ACTION = "CALL_CANCEL_ACTION"
    const val MESSAGE_DATA = "MESSAGE_DATA"
    const val CONTACT_NAME = "CONTACT_NAME"
    const val VIDEO_ENABLED = "VIDEO_ENABLED"
}