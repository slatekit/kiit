package slatekit.common.types


interface Content2 {
    val data: ByteArray
    val tpe : ContentType
}

class ContentText(override val data:ByteArray, val raw:String, override val tpe: ContentType) : Content2
class ContentData(override val data:ByteArray, val raw:String?, override val tpe: ContentType) : Content2
class ContentFile(val name:String, override val data:ByteArray, val raw:String?, override val tpe: ContentType, val size: Long) : Content2


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
class Content(val data:ByteArray, val raw:String?, val tpe: ContentType) {

    constructor(content: String, tpe: ContentType) :
            this(content.toByteArray(), content, tpe)


    val text: String? by lazy { raw ?: String(data) }


    /**
     * whether this content is empty
     * @return
     */
    val isEmpty: Boolean = data.isEmpty()


    /**
     * whether this content is present
     * @return
     */
    val isDefined: Boolean = !isEmpty


    /**
     * the length of the content
     * @return
     */
    val size: Int = data.size


    companion object {

        @JvmStatic
        fun csv(text: String): Content = Content(text, ContentTypeCsv)

        @JvmStatic
        fun html(text: String): Content = Content(text, ContentTypeHtml)

        @JvmStatic
        fun json(text: String): Content = Content(text, ContentTypeJson)

        @JvmStatic
        fun text(text: String): Content = Content(text, ContentTypeText)

        @JvmStatic
        fun prop(text: String): Content = Content(text, ContentTypeProp)

        @JvmStatic
        fun xml(text: String): Content = Content(text, ContentTypeXml)

        @JvmStatic
        fun other(text: String, tpe: ContentType): Content = Content(text, tpe)
    }
}
