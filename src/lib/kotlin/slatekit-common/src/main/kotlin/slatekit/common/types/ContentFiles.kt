/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.types

/**
 * Represents string content type/format information.
 *
 * Use cases:
 * 1. Provide type info on a string
 * 2. Provide intent that the string should be treated as file vs content ( See Content.kt )
 * 3. Provide a way for consumers to infer the intent of the string.
 *    e.g. the API server can determine that if a service returns a Doc instead of a string,
 *         then the Doc should be sent back as a File instead of a string
 */
object ContentFiles {
        @JvmStatic
        val empty = ContentFile("", byteArrayOf(),"", ContentTypeText)

        @JvmStatic
        fun text(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypeText)

        @JvmStatic
        fun html(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypeHtml)

        @JvmStatic
        fun json(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypeJson)

        @JvmStatic
        fun csv(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypeCsv)

        @JvmStatic
        fun prop(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypeProp)

        @JvmStatic
        fun other(name: String, content: String, tpe: ContentType): ContentFile = ContentFile(name, content.toByteArray(), content, tpe)

}
