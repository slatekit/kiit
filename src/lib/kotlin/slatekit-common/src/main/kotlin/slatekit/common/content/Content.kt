package slatekit.common.content

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
 * @param text
 * @param format
 */
data class Content(val text: String, val tpe: ContentType) {

    /**
     * whether this content is empty
     * @return
     */
    val isEmpty: Boolean = text.isNullOrEmpty()

    /**
     * whether this content is present
     * @return
     */
    val isDefined: Boolean = !isEmpty

    /**
     * the length of the content
     * @return
     */
    val size: Int = text.length

    companion object {

        @JvmStatic
        fun from(format: String): ContentType {
            return when (format.toLowerCase()) {
                "csv" -> ContentTypeCsv
                "json" -> ContentTypeJson
                "prop" -> ContentTypeProp
                else -> ContentTypeJson
            }
        }

        @JvmStatic fun csv(text: String): Content =
            Content(text, ContentTypeCsv)
        @JvmStatic fun html(text: String): Content =
            Content(text, ContentTypeHtml)
        @JvmStatic fun json(text: String): Content =
            Content(text, ContentTypeJson)
        @JvmStatic fun text(text: String): Content =
            Content(text, ContentTypeText)
        @JvmStatic fun prop(text: String): Content =
            Content(text, ContentTypeProp)
        @JvmStatic fun xml(text: String): Content =
            Content(text, ContentTypeXml)
        @JvmStatic fun other(text: String, tpe: ContentType): Content =
            Content(text, tpe)
    }
}
