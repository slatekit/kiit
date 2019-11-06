package slatekit.common.content

open class ContentType(
    val http: String,
    val ext: String
) {
    companion object {
        @JvmStatic
        fun parse(format: String): ContentType {
            return when (format.toLowerCase()) {
                ContentTypeCsv.ext  -> ContentTypeCsv
                ContentTypeHtml.ext -> ContentTypeHtml
                ContentTypeJson.ext -> ContentTypeJson
                ContentTypeProp.ext -> ContentTypeProp
                ContentTypeText.ext -> ContentTypeText
                ContentTypeXml.ext  -> ContentTypeXml
                else -> ContentTypeJson
            }
        }
    }
}

object ContentTypeCsv  : ContentType("text/csv", "csv")
object ContentTypeHtml : ContentType("text/html", "html")
object ContentTypeJson : ContentType("application/json", "json")
object ContentTypeText : ContentType("text/plain", "text")
object ContentTypeProp : ContentType("text/plain", "prop")
object ContentTypeXml  : ContentType("application/xml", "xml")
