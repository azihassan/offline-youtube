package com.pingfrommorocco.youtubeoffline

import java.time.LocalDateTime

data class Video(
    val originalURL: String,
    val fileURL: String,
    val title: String,
    val location: String,
    val downloadedAt: LocalDateTime
)