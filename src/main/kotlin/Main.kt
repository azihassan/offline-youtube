// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.pingfrommorocco.youtubeoffline

import YoutubeVideoURLExtractor
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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.*
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.LocalDateTime

@Composable
@Preview
fun App(videos: List<Video>) {
    DesktopMaterialTheme {
        Column {
            DownloadForm({ url, location ->
                val html = downloadAsString(url)
                System.out.println("Downloaded ${html.length} bytes of HTML")
                val extractor = YoutubeVideoURLExtractor(html)
                System.out.println("Downloading ${extractor.getURL(18)} to ${location}")
            })
            VideoListing(videos, {
                System.out.println("Clicked on ${it.title}")
            })
        }
    }
}

fun main() = application {

    val videos = listOf(
        Video("https://www.youtube.com/watch?v=3Vx1Z2Y-ZVA", "", "Video #1", "/home/tux/Videos/1.mp4", LocalDateTime.now()),
        Video("https://www.youtube.com/watch?v=MCpl74MsfLE", "", "Video #2", "/home/tux/Videos/2.mp4", LocalDateTime.now()),
        Video("https://www.youtube.com/watch?v=UqW42_8kn0s", "", "Video #3", "/home/tux/Videos/3.mp4", LocalDateTime.now()),
        Video("https://www.youtube.com/watch?v=4h2-l68CTmY", "", "Video #4", "/home/tux/Videos/4.mp4", LocalDateTime.now())
    )
    Window(onCloseRequest = ::exitApplication, title = "Youtube Offline") {
        App(videos = videos)
    }
}

fun downloadAsString(url: String): String {
    val client = HttpClient.newHttpClient()
    val request= HttpRequest.newBuilder()
        .uri(URI.create(url))
        .build()
    return client.send(request, HttpResponse.BodyHandlers.ofString()).body()
}

@Composable
fun DownloadForm(onDownload: suspend (url: String, location: String) -> Unit) {
    Column {
        var url by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        Row {
            TextField(url, enabled = !isLoading, onValueChange = {
                url = it
            }, label = {
                Text("URL")
            })
            Button(enabled = !isLoading, onClick = {
                //runBlocking {
                    CoroutineScope(Dispatchers.IO).launch {
                        isLoading = true
                        onDownload(url, location)
                        isLoading = false
                    }
                //}
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
    val originalUrl: String,
    val url: String,
    val title: String,
    val location: String,
    val downloadedAt: LocalDateTime
)