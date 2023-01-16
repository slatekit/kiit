/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 *
  *  </kiit_header>
 */

package kiit.common.types

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
        val empty = ContentFile("", byteArrayOf(),"", ContentTypes.Plain)

        @JvmStatic
        fun text(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypes.Plain)

        @JvmStatic
        fun html(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypes.Html)

        @JvmStatic
        fun json(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypes.Json)

        @JvmStatic
        fun csv(name: String, content: String): ContentFile = ContentFile(name, content.toByteArray(), content, ContentTypes.Csv)

        @JvmStatic
        fun other(name: String, content: String, tpe: ContentType): ContentFile = ContentFile(name, content.toByteArray(), content, tpe)

}
