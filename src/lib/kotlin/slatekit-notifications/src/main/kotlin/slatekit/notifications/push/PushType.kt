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
package slatekit.notifications.push


sealed class PushType {
    abstract val name:String
    object Alert : PushType() { override val name: String = "alert" }
    object Data  : PushType() { override val name: String = "data" }
    object Both  : PushType() { override val name: String = "both" }
    data class Other( override val name: String) : PushType()
}
