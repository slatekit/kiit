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

data class Doc(val name: String, val content: String, val format: String, val ext:String, val size:Long) {

    companion object {
        fun  csv (name:String, content:String):Doc =  Doc(name, content, "text/csv"        , "csv" , content.length.toLong())
        fun  html(name:String, content:String):Doc =  Doc(name, content, "text/html"       , "html", content.length.toLong())
        fun  json(name:String, content:String):Doc =  Doc(name, content, "application/json", "json", content.length.toLong())
        fun  text(name:String, content:String):Doc =  Doc(name, content, "text/plain"      , "text", content.length.toLong())
        fun  xml (name:String, content:String):Doc =  Doc(name, content, "application/xml" , "xml" , content.length.toLong())
    }
}