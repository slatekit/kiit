package slatekit.common.types

/**
 * Represents string content with type/format information.
 *
 * Use cases:
 * 1. Provide type info on a string
 * 2. Provide intent that the string should be treated as content instead of a file ( See Doc.kt )
 * 3. Provide a way for consumers to infer the intent of the string.
 *    e.g. the API server can determine that if a service returns a Content instead of a string,
 *         then the Content can be sent back with a specific content-type for http.
 *
 */
interface Content {
    val data: ByteArray
    val tpe : ContentType

    /**
     * whether this content is empty
     * @return
     */
    val isEmpty: Boolean get() { return data.isEmpty() }


    /**
     * whether this content is present
     * @return
     */
    val isDefined: Boolean get() { return !isEmpty }


    /**
     * the length of the content
     * @return
     */
    val size: Int get() { return data.size }


    companion object {

    }
}

class ContentText(override val data:ByteArray, val raw:String, override val tpe: ContentType) : Content
class ContentData(override val data:ByteArray, val raw:String?, override val tpe: ContentType) : Content
class ContentFile(val name:String, override val data:ByteArray, val raw:String?, override val tpe: ContentType) : Content



object Contents {

    @JvmStatic
    fun csv(text: String): Content = ContentText(text.toByteArray(), text, ContentTypeCsv)

    @JvmStatic
    fun html(text: String): Content = ContentText(text.toByteArray(), text, ContentTypeHtml)

    @JvmStatic
    fun json(text: String): Content = ContentText(text.toByteArray(), text, ContentTypeJson)

    @JvmStatic
    fun text(text: String): Content = ContentText(text.toByteArray(), text, ContentTypeText)

    @JvmStatic
    fun prop(text: String): Content = ContentText(text.toByteArray(), text, ContentTypeProp)

    @JvmStatic
    fun xml(text: String): Content = ContentText(text.toByteArray(), text, ContentTypeXml)

    @JvmStatic
    fun other(text: String, tpe: ContentType): Content = ContentText(text.toByteArray(), text, tpe)

    fun toText(content:Content?):String? {
        return when (content) {
            null -> ""
            is ContentText -> content.raw ?: String(content.data)
            is ContentData -> content.raw ?: String(content.data)
            else -> String(content.data)
        }
    }
}
