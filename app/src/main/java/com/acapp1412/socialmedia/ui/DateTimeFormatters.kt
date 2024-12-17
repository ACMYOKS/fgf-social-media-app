package com.acapp1412.socialmedia.ui

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

fun Long.toTimeString(): String {
    val localDateTime = Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    return localDateTime.format(dateTimeFormatter)
}