// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.pingfrommorocco.youtubeoffline

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.time.LocalDateTime

@Composable
@Preview
fun App(videos: List<Video>) {
    var text by remember { mutableStateOf("Hello, World!") }

    DesktopMaterialTheme {
        Column {
            DownloadForm({ url, location ->
                System.out.println("Downloading ${url} to ${location}")
            })
            VideoListing(videos, {
                System.out.println("Clicked on ${it.title}")
            })
        }
    }
}

fun main() = application {

    val videos = listOf(
        Video("https://www.google.com", "Video #1", "/home/tux/Videos/1.mp4", LocalDateTime.now()),
        Video("https://www.google.com", "Video #2", "/home/tux/Videos/2.mp4", LocalDateTime.now()),
        Video("https://www.google.com", "Video #3", "/home/tux/Videos/3.mp4", LocalDateTime.now()),
        Video("https://www.google.com", "Video #4", "/home/tux/Videos/4.mp4", LocalDateTime.now())
    )
    Window(onCloseRequest = ::exitApplication, title = "Youtube Offline") {
        App(videos = videos)
    }
}

@Composable
fun DownloadForm(onDownload: (url: String, location: String) -> Unit) {
    Column {
        var url by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        Row {
            TextField(url, onValueChange = {
                url = it
            }, label = {
                Text("URL")
            })
            Button(onClick = {
                onDownload(url, location)
            }) {
                Text("Download")
            }
        }
        Row {
            TextField(location, onValueChange = {
                location = it
            }, label = {
                Text("Target location")
            })
            Button({
                System.out.println("Clicked browse")
            }) {
                Text("Browse")
            }
        }
    }
}

@Composable
fun VideoListing(videos: List<Video>, onVideoClick: (video: Video) -> Unit) {
    Column {
        videos.forEach {
            VideoEntry(it, onVideoClick)
        }
    }
}

@Composable
fun VideoEntry(video: Video, onVideoClick: (video: Video) -> Unit) {
    Row(Modifier.border(1.dp, Color.LightGray, RectangleShape).then(Modifier.padding(5.dp)).then(Modifier.clickable { onVideoClick(video) })) {
        Text(video.title)
        Text(video.downloadedAt.toString())
    }
}

data class Video(
    val url: String,
    val title: String,
    val location: String,
    val downloadedAt: LocalDateTime
)