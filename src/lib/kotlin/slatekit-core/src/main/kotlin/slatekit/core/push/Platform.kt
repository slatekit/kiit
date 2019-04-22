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

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

sealed class Platform(
    override val name: String,
    override val value: Int
) : EnumLike {

    object PlatformIOS     : Platform("ios", 0)
    object PlatformAnd     : Platform("android", 1)
    object PlatformWeb     : Platform("web", 2)
    object PlatformSrv     : Platform("server", 3)
    object PlatformNone    : Platform("none", 4)
    data class PlatformUnknown(val text:String) : Platform("unknown", 5)


    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(PlatformIOS, PlatformAnd, PlatformWeb, PlatformWeb, PlatformSrv, PlatformNone)
        }

        override fun isUnknownSupported(): Boolean {
            return true
        }

        override fun unknown(name: String): EnumLike {
            return PlatformUnknown(name)
        }

        override fun unknown(value: Int): EnumLike {
            return PlatformUnknown("unknown")
        }
    }
}