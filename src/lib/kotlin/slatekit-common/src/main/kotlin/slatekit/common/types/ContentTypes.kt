package slatekit.common.types

object ContentTypes {
    // Text
    object Csv   : ContentType("text/csv", "csv")
    object Css   : ContentType("text/css", "css")
    object Html  : ContentType("text/html", "html")
    object Plain : ContentType("text/plain", "plain")
    object Xml   : ContentType("text/xml", "xml")

    // Image
    object Gif  : ContentType("image/gif", "gif")
    object Jpg  : ContentType("image/jpg", "jpg")
    object Jpeg : ContentType("image/jpeg", "jpeg")
    object Png  : ContentType("image/png", "png")
    object Tiff : ContentType("image/tiff", "tiff")

    // Audio
    object Mpeg  : ContentType("audio/mpeg", "mpeg")
    object Mp3  : ContentType("audio/mp3", "mp3")
    object Wav : ContentType("audio/wav", "wav")

    // Apps
    object Json : ContentType("application/json", "json")
    object Pdf  : ContentType("application/pdf", "pdf")
}