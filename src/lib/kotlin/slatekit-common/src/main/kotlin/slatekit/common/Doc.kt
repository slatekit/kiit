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

package slatekit.common

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
data class Doc(val name: String, val content: String, val tpe:ContentType, val size:Long) {

    companion object {
        @JvmStatic fun  csv (name:String, content:String):Doc =  Doc(name, content, ContentTypeCsv , content.length.toLong())
        @JvmStatic fun  html(name:String, content:String):Doc =  Doc(name, content, ContentTypeHtml, content.length.toLong())
        @JvmStatic fun  json(name:String, content:String):Doc =  Doc(name, content, ContentTypeJson, content.length.toLong())
        @JvmStatic fun  text(name:String, content:String):Doc =  Doc(name, content, ContentTypeText, content.length.toLong())
        @JvmStatic fun  prop(name:String, content:String):Doc =  Doc(name, content, ContentTypeProp, content.length.toLong())
        @JvmStatic fun  xml (name:String, content:String):Doc =  Doc(name, content, ContentTypeXml , content.length.toLong())
        @JvmStatic fun  other(name:String, content:String, tpe:ContentType):Doc =  Doc(name, content, tpe, content.length.toLong())
    }
}