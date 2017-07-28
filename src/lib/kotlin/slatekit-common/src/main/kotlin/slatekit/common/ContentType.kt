package slatekit.common

open class ContentType(
    val http:String,
    val ext:String
)


object ContentTypeCsv  : ContentType("text/csv"        , "csv" )
object ContentTypeHtml : ContentType("text/html"       , "html")
object ContentTypeJson : ContentType("application/json", "json")
object ContentTypeText : ContentType("text/plain"      , "text")
object ContentTypeProp : ContentType("text/plain"      , "prop")
object ContentTypeXml  : ContentType("application/xml" , "xml" )