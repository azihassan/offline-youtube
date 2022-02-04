import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class YoutubeVideoURLExtractor(
    private val html: String
) {
    private val parser: Document = Jsoup.parse(html)

    fun getURL(itag: Int = 18): String {
        System.out.println("HTML length : ${html.substring(0, 20)}")
        val prefix = """itag":${itag},"url":"""
        System.out.println("prefix: ${prefix}")
        val startIndex = html.indexOf(prefix)
        if(startIndex == -1) {
            return ""
        }
        val html = html.substring(startIndex + prefix.length + 1)
        val endIndex = html.indexOf('"')
        val url = html.substring(0, endIndex)
        return url.replace("\\u0026", "&")
    }

    fun getTitle(): String {
        return parser.selectFirst("meta[name=title]")!!.attr("content")
    }

    fun getID(): String {
        return parser.selectFirst("meta[itemprop=videoId]")!!.attr("content")
    }
}