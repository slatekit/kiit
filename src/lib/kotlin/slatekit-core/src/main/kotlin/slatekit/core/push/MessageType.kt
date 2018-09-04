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

interface MessageType {
    val name:String
}


object MessageTypeAlert   : MessageType { override val name:String = "alert" }
object MessageTypeData    : MessageType { override val name:String = "data"  }
object MessageTypeBoth    : MessageType { override val name:String = "both"  }
data class MessageTypeOther(override val name:String): MessageType
