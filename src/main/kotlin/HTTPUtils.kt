import java.io.FileOutputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

suspend fun downloadToFile(sourcePath: String, referrer: String, destinationPath: String, progressCallback: suspend (Long, Long) -> Unit) {
    val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
    val request = HttpRequest.newBuilder()
        .uri(URI.create(sourcePath))
        .setHeader("Referer", referrer)
        .setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0")
        .build()
    val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
    val size = response.headers().firstValue("Content-Length").map { it.toLong() }.orElse(0L)
    val stream = response.body()

    val buffer = ByteArray(8192)
    var read = 0L
    val out = FileOutputStream(destinationPath)
    while(true) {
        val length = stream.read(buffer)
        if(length <= 0) {
            break
        }
        read += length
        out.write(buffer, 0, length)
        progressCallback(read, size)
    }
}

fun downloadAsString(url: String): String {
    val client = HttpClient.newHttpClient()
    val request= HttpRequest.newBuilder()
        .uri(URI.create(url))
        .setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:95.0) Gecko/20100101 Firefox/95.0")
        .build()
    return client.send(request, HttpResponse.BodyHandlers.ofString()).body()
}
