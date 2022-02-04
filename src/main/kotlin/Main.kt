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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import downloadAsString
import downloadToFile
import kotlinx.coroutines.*
import java.nio.file.Paths
import java.time.LocalDateTime

@Composable
@Preview
fun App(videos: SnapshotStateList<Video>) {
    DesktopMaterialTheme {
        Column {
            DownloadForm({ url, location ->
                val html = downloadAsString(url)
                System.out.println("Downloaded ${html.length} bytes of HTML")
                val extractor = YoutubeVideoURLExtractor(html)
                val fileURL = extractor.getURL(18)
                val title = extractor.getTitle()
                val path = Paths.get(location, "$title-${extractor.getID()}.mp4")
                val video = Video(url, fileURL, title, path.toString(), LocalDateTime.now())

                videos.add(video)
                System.out.println("Queued ${video.originalURL}")
            })
            VideoListing(videos, {
                System.out.println("Clicked on ${it.title}")
                Runtime.getRuntime().exec(arrayOf("gnome-mpv", it.location))
            }) { video, progressCallback ->
                System.out.println("Downloading ${video.originalURL} to ${video.location}")
                downloadToFile(video.fileURL, video.originalURL, video.location, progressCallback)
            }
        }
    }
}

fun main() = application {
    val videos = remember { mutableStateListOf<Video>() }

    Window(onCloseRequest = ::exitApplication, title = "Youtube Offline") {
        App(videos = videos)
    }
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
                scope.launch {
                    withContext(Dispatchers.IO) {
                        isLoading = true
                        try {
                            onDownload(url, location)
                        } catch(e: Exception) {
                            System.out.println("Failed to download URL : ${e.message}")
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                }
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
fun VideoListing(
    videos: SnapshotStateList<Video>,
    onVideoClick: (video: Video) -> Unit,
    onVideoDownload: suspend (video: Video, progressCallback: suspend (Long, Long) -> Unit) -> Unit
) {
    Column {
        videos.forEach {
            VideoEntry(it, onVideoClick, onVideoDownload)
        }
    }
}

@Composable
fun VideoEntry(
    video: Video,
    onVideoClick: (video: Video) -> Unit,
    onVideoDownload: suspend (video: Video, progressCallback: suspend (Long, Long) -> Unit) -> Unit
) {
    var isDownloading by remember { mutableStateOf(false) }
    var readInMegaBytes by remember { mutableStateOf(0.0) }
    var sizeInMegaBytes by remember { mutableStateOf(0.0) }
    val scope = rememberCoroutineScope()
    var downloadJob: Job? = null

    fun hasFinishedDownloading() = readInMegaBytes >= sizeInMegaBytes
    fun formatProgress(readInMegaBytes: Double, sizeInMegaBytes: Double) = String.format("%.2f / %.2f MB", readInMegaBytes, sizeInMegaBytes)

    Row(Modifier.border(1.dp, Color.LightGray, RectangleShape).then(Modifier.padding(5.dp)).then(Modifier.clickable { onVideoClick(video) })) {
        Text(video.title)
        Text(video.downloadedAt.toString())
        Text(if(isDownloading) formatProgress(readInMegaBytes, sizeInMegaBytes) else "Pending")

        Button(enabled = isDownloading && !hasFinishedDownloading(), onClick = {
            downloadJob!!.cancel()
        }) { Text("Cancel") }

        Button(enabled = !isDownloading || hasFinishedDownloading(), onClick = {
            isDownloading = true
            downloadJob = scope.launch {
                withContext(Dispatchers.IO) {
                    onVideoDownload(video, { read, size ->
                        withContext(Dispatchers.IO) {
                            System.out.printf("\r%.2f / %.2f MB", read / 1024.0 / 1024.0, size / 1024.0 / 1024.0)
                            readInMegaBytes = read / 1024.0 / 1024.0
                            sizeInMegaBytes = size / 1024.0 / 1024.0
                        }
                    })
                }
            }
        }) {
            Text("Download")
        }
    }
}

