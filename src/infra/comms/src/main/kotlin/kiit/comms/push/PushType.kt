/**
 <kiit_header>
url: www.slatekit.com
git: www.github.com/slatekit/kiit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.

 </kiit_header>
 */
package kiit.comms.push

sealed class PushType {
    abstract val name: String
    object Alert : PushType() { override val name: String = "alert" }
    object Data : PushType() { override val name: String = "data" }
    object Both : PushType() { override val name: String = "both" }
    data class Other(override val name: String) : PushType()
}
