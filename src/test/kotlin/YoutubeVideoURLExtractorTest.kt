import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.io.path.readText

class YoutubeVideoURLExtractorTest {
    @Test
    fun shouldParseYoutubeURL() {
        val html = Paths.get("src", "test", "resources", "zoz.html").readText()
        val extractor = YoutubeVideoURLExtractor(html)
        assertEquals(
            "https://r4---sn-f5o5-jhol.googlevideo.com/videoplayback?expire=1638935038&ei=ntWvYYf_NZiJmLAPtfySkAc&ip=105.66.6.95&id=o-AG7BUTPMmXcFJCtiIUgzrYXlgliHnrjn8IT0b4D_2u8U&itag=18&source=youtube&requiressl=yes&mh=Zy&mm=31%2C29&mn=sn-f5o5-jhol%2Csn-h5qzen7s&ms=au%2Crdu&mv=m&mvi=4&pl=24&initcwndbps=112500&vprv=1&mime=video%2Fmp4&ns=oWqcgbo-7-88Erb0vfdQlB0G&gir=yes&clen=39377316&ratebypass=yes&dur=579.012&lmt=1638885608167129&mt=1638913037&fvip=4&fexp=24001373%2C24007246&c=WEB&txp=3310222&n=RCgHqivzcADgV0inFcU&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAP5RM2aRT03WZPwBGRWRs25p6T03kecAfGoqqU1tQt0TAiAW-sbLCLqKm9XATrjmhgB5yIlGUeGF1WiWGWvFcVWgkA%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRgIhAJNGheTpD9UVxle1Q9ECIhRMs7Cfl9ZZtqifKo81o-XRAiEAyYKhi3IBXMhIfPyvfpwmj069jMAhaxapC1IhDCl4k90%3D",
            extractor.getURL(18)
        )

        assertEquals(
            "https://r4---sn-f5o5-jhol.googlevideo.com/videoplayback?expire=1638935038&ei=ntWvYYf_NZiJmLAPtfySkAc&ip=105.66.6.95&id=o-AG7BUTPMmXcFJCtiIUgzrYXlgliHnrjn8IT0b4D_2u8U&itag=22&source=youtube&requiressl=yes&mh=Zy&mm=31%2C29&mn=sn-f5o5-jhol%2Csn-h5qzen7s&ms=au%2Crdu&mv=m&mvi=4&pl=24&initcwndbps=112500&vprv=1&mime=video%2Fmp4&ns=oWqcgbo-7-88Erb0vfdQlB0G&cnr=14&ratebypass=yes&dur=579.012&lmt=1638885619798068&mt=1638913037&fvip=4&fexp=24001373%2C24007246&c=WEB&txp=3316222&n=RCgHqivzcADgV0inFcU&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Ccnr%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRQIhAJAAEjw50XBuXW4F5bLVKgzJQ-8HPiVFE9S94uknmEESAiBUZstN7FctoBLg25v5wJeJp5sNqlFziaYNcBdsJn3Feg%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRgIhAJNGheTpD9UVxle1Q9ECIhRMs7Cfl9ZZtqifKo81o-XRAiEAyYKhi3IBXMhIfPyvfpwmj069jMAhaxapC1IhDCl4k90%3D",
            extractor.getURL(22)
        )
    }

    @Test
    fun shouldParseYoutubeID() {
        val html = Paths.get("src", "test", "resources", "zoz.html").readText()
        val extractor = YoutubeVideoURLExtractor(html)
        assertEquals("sif2JVDhZrQ", extractor.getID())
    }

    @Test
    fun shouldParseYoutubeVideoTitle() {
        val html = Paths.get("src", "test", "resources", "zoz.html").readText()
        val extractor = YoutubeVideoURLExtractor(html)
        assertEquals("اللوبيا المغربية ديال دار سهلة و بنينة سخونة و حنينة", extractor.getTitle())
    }
}
