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

interface Platform {
    val name:String
}


object PlatformIOS   : Platform { override val name:String = "ios" }
object PlatformAnd   : Platform { override val name:String = "android" }
object PlatformWeb   : Platform { override val name:String = "web" }
object PlatformSrv   : Platform { override val name:String = "server" }
object PlatformNone  : Platform { override val name:String = "none" }


data class OtherPlatform(override val name:String): Platform
