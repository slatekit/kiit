package slatekit.common.types

open class ContentType(
    val http: String,
    val ext: String
) {
    companion object {

        @JvmStatic
        fun parse(format: String): ContentType {
            return when (format.toLowerCase()) {
                ContentTypes.Csv.ext  -> ContentTypes.Csv
                ContentTypes.Html.ext -> ContentTypes.Html
                ContentTypes.Json.ext -> ContentTypes.Json
                ContentTypes.Plain.ext -> ContentTypes.Plain
                ContentTypes.Xml.ext  -> ContentTypes.Xml
                else -> ContentTypes.Json
            }
        }
    }
}


