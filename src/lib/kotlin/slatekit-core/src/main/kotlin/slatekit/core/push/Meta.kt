/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package slatekit.core.push

import slatekit.common.DateTime


/**
 * The meta is a fixed set of metadata specifically about the payload
 * and is actually separately stored and outside of the dynamic payload structure
 * This can be used for fast lookup / hints of the actual payload data
 * this has more do with fields not directly visible to a user.
 * e.g. The content type, id, permissions, etc.
 *
 * @param category   : The category of the message    e.g. Alert | Share | Reg | Other
 * @param type       : The type of data being sent    e.g. Todo | Event
 * @param id         : The id of the data being sent  e.g. "abc123"
 * @param action     : The action being sent          e.g. create | update | process | delete
 * @param origin     : The origin of the message      e.g. device | server
 * @param tag        : Correlation tag
 * @param tsMsg      : Timestamp of creation from the client
 * @param tsSent     : Timestamp sent from the server
 * @param args       : Additional args that can be supplied
 */
data class Meta(
    val category: Category,
    val type: String,
    val action: String,
    val id: String,
    val origin: String = "",
    val tag: String = "",
    val tsMsg: DateTime? = null,
    val tsSent: DateTime? = null,
    val args: Map<String,String>? = null
) {

    companion object {

        val empty = of(OtherCategory(""), "", "")


        @JvmStatic
        fun of(category:Category, type:String, action:String, args:Map<String,String>? = null): Meta {
            return Meta(
                category,
                type,
                action,
                "",
                "",
                "",
                null,
                null,
                args = args
            )
        }
    }
}
